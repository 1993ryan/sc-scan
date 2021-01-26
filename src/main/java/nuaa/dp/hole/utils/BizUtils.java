package nuaa.dp.hole.utils;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.bo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/23 5:38 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/23     Dapeng Yan         v1.0.0
 */
@Slf4j
public class BizUtils {
    public static final String DEFAULT_POM_PATH = "pom.xml";

    public static List<BeforeItem> findPathBySince(QueryResultBO result, Git git, String findPath, AnyObjectId since, List<String> removedLines) throws Exception {
        List<BeforeItem> retList = new ArrayList<>();
        org.eclipse.jgit.lib.Repository repository = git.getRepository();
        Iterable<RevCommit> revCommits = git.log().addPath(findPath).add(since).call();
        boolean found = false;
        for (RevCommit revCommit : revCommits) {
            AnyObjectId foundObjectId = revCommit.getId();
            String revision = foundObjectId.getName();
            if(revision.equals(since.getName())) {
                continue;
            } else if(found) {
                break;
            }

            List<DiffEntry> diffEntries = showDiff(git, revision);
            if(Objects.isNull(diffEntries)) {
                continue;
            }

            for (DiffEntry diffEntry : diffEntries) {
                if(!diffEntry.getOldPath().equals(findPath)) {
                    continue;
                }

                String oldPath = diffEntry.getOldPath();
                String subFolderName = "";
                if(oldPath.contains("/")) {
                    subFolderName = oldPath.substring(0, oldPath.indexOf("/"));
                }

                MavenModel model = readVersionFromPomFile(repository, revCommit, subFolderName);
                if(Objects.nonNull(model)) {
                    result.addTag(new TagItem(model.getArtifactId(), model.getVersion(), new Date(revCommit.getCommitTime() * 1000L)));
                    log.info("found tag, commit id: {}, version: {}", revision, model.getVersion());
                }

                String diffDetail = formatDiff(git, diffEntry);
                String[] lines = diffDetail.split("\n");
                boolean bMatch = false;
                String matchedFirstLine = removedLines.get(0);
                for(int index=0; index < lines.length; index++) {
                    String strLine = lines[index];
                    if(StringUtils.isBlank(strLine) || !strLine.startsWith("+")) {
                        continue;
                    }
                    String newLine = "-" + strLine.substring(1);
                    if(matchedFirstLine.equals(newLine)) {
                        int matchedRows = 1;
                        for(int j=index+1; j < lines.length && matchedRows < removedLines.size(); j++, matchedRows++ ) {
                            String compareLine = "-" + lines[j].substring(1);
                            if(!removedLines.get(matchedRows).equals(compareLine)) {
                                break;
                            }
                        }

                        if(matchedRows == removedLines.size()) {
                            bMatch = true;
                            break;
                        }
                    }
                }

                if(bMatch) {
                    BeforeItem item = new BeforeItem();
                    item.setBeforeRevision(revision);
                    item.setBeforeCommitTime(new Date(revCommit.getCommitTime() * 1000L));
                    item.setBeforeAuthor(revCommit.getAuthorIdent().getName());
                    retList.add(item);

                    found = true;
                }
            }

        }
        return retList;
    }

    public static MavenModel readVersionFromPomFile(Repository repository, RevCommit revCommit, String subFolderName) throws IOException {
        RevTree revTree = revCommit.getTree();
        String filePath = nuaa.dp.hole.utils.StringUtils.isBlank(subFolderName) ? DEFAULT_POM_PATH : subFolderName + File.separator + DEFAULT_POM_PATH;
        TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, revTree);
        if(Objects.nonNull(treeWalk)) {
            ObjectId blobId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(blobId);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                Model model = reader.read(new ByteArrayInputStream(loader.getBytes()));
                String version = model.getVersion();
                if(StringUtils.isBlank(model.getVersion()) && Objects.nonNull(model.getParent())) {
                    version = model.getParent().getVersion();
                }
                return new MavenModel(model.getGroupId(), model.getArtifactId(), version);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<List<String>> findRemovedLines(String diffText) {
        List<List<String>> retList = new ArrayList<>();
        String[] lines = diffText.split("\n");
        boolean bContinue = false;
        List<String> child = new ArrayList<>();
        for(String strLine: lines) {
            if(StringUtils.isBlank(strLine) ||
                    !strLine.startsWith("-") ||
                    strLine.trim().equals("-") ||
                strLine.startsWith("---")) {
                bContinue = false;
                continue;
            }

            if(false == bContinue) {
                child = new ArrayList<>();
                child.add(strLine);
                retList.add(child);
            } else {
                child.add(strLine);
            }
            bContinue = true;
        }
        return retList;
    }

    public static List<DiffEntry> showDiff(Git git, String revision) throws Exception {
        org.eclipse.jgit.lib.Repository repository = git.getRepository();
        ObjectId objId = repository.resolve(revision);
        Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
        Iterator<RevCommit> iter = allCommitsLater.iterator();
        RevCommit commit = iter.next();
        TreeWalk tw = new TreeWalk(repository);
        List<RevTree> revTrees = new ArrayList<>();

        revTrees.add(commit.getTree());
        commit = iter.next();
        if (commit != null)
            revTrees.add(commit.getTree());
        else
            return null;

        Collections.reverse(revTrees);
        for(RevTree revTree: revTrees) {
            tw.addTree(revTree);
        }

        tw.setRecursive(true);
        RenameDetector rd = new RenameDetector(repository);
        rd.addAll(DiffEntry.scan(tw));

        return rd.compute();
    }

    public static String formatDiff(Git git, DiffEntry diff) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DiffFormatter formatter = new DiffFormatter(outputStream);
        formatter.setRepository(git.getRepository());
        try {
            formatter.format(diff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }

    public static <T> boolean hasElem(List<T> array, T value) {
        for(T key: array) {
            if(key.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsElem(List<String> array, String value) {
        for(String key: array) {
            if(StringUtils.isNotBlank(value) && value.contains(key)) {
                return true;
            }
        }
        return false;
    }
}
