package nuaa.dp.hole.biz.repository;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.bo.*;
import nuaa.dp.hole.biz.service.ProjectInfoService;
import nuaa.dp.hole.dal.common.ErrorCode;
import nuaa.dp.hole.dal.common.Result;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.utils.BizUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/23 12:00 AM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/23      Dapeng Yan         v1.0.0
 */
@Slf4j
@Component
public class GitRepository {

    private Map<String, Git> gitMap = new ConcurrentHashMap<>();
    private Map<String, Map<String, MavenModel>> mavenModelMap = new ConcurrentHashMap<>();

    @Autowired
    private ProjectInfoService projectInfoService;

    public Result<Git> parseGit(String projectName) {
        ProjectInfo projectInfo = this.projectInfoService.findByName(projectName);
        if(Objects.isNull(projectInfo)) {
            return Result.failed("Project not found");
        }

        try {
            File folder = null;
            if(StringUtils.isNotBlank(projectInfo.getSourcePath())) {
                folder = new File(projectInfo.getSourcePath());
                if(folder.exists()) {
                    if(false == folder.isDirectory()) {
                        return Result.failed("Source path is not directory");
                    }
                }
            }

            if(null == folder) {
                this.cloneRepository(projectInfo.getProjectName(), projectInfo.getRemoteUrl());
                folder = new File("tmp/" + projectName);
            }

            Git git = Git.open(folder);
            File[] childFiles = folder.listFiles();
            List<String> children = new ArrayList<>();
            for (File childFile : childFiles) {
                if(!childFile.isDirectory()) {
                    continue;
                }
                children.add(childFile.getName());
            }
            this.readSubProject(projectName, children, git);
            gitMap.put(projectName, git);
            return Result.success(git);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.failed("it is not git folder");
    }

    public boolean cloneRepository(String folderName, String remoteUrl) throws GitAPIException {
        File tmpFolder = new File("tmp");
        if(!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }

        File localPath = new File("tmp/" + folderName);
        if(localPath.exists()) {
            return true;
        }
        Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(localPath)
                .call();
        return true;
    }

    private void readSubProject(String projectName, List<String> children, Git git) {
        children.forEach(key -> {
            try {
                MavenModel model = this.readMavenModel(git, key);
                if(Objects.isNull(model)) {
                    return;
                }

                Map<String, MavenModel> childMap = mavenModelMap.get(projectName);
                if(Objects.isNull(childMap)) {
                    childMap = new ConcurrentHashMap<>();
                }
                childMap.put(key, model);
                log.info("project: {}, sub: {}, artifactId: {}", projectName, key, model.getArtifactId());
                mavenModelMap.put(projectName, childMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Result<ResultItem> findByRevision(Git git, String revision) throws Exception {
        ObjectId foundObjectId = git.getRepository().resolve(revision);
        if(Objects.isNull(foundObjectId)) {
            return Result.failed(ErrorCode.OBJECT_NOT_EXISTS);
        }

        RevCommit revCommit = git.getRepository().parseCommit(foundObjectId);
        ResultItem item = new ResultItem();
        item.setRevision(revision);
        item.setCommitTime(new Date(revCommit.getCommitTime() * 1000L));
        item.setHistoryMessage(revCommit.getFullMessage());

        List<DiffEntry> diffEntries = BizUtils.showDiff(git, revision);
        if(Objects.nonNull(diffEntries)) {
            StringBuffer buffer = new StringBuffer();
            for(DiffEntry diff: diffEntries) {
                buffer.append(BizUtils.formatDiff(git, diff));
            }
            item.setDifferent(buffer.toString());
        }

        return Result.success(item);
    }

    public QueryResultBO queryByKeyword(Git git, String keyword) throws Exception {
        QueryResultBO result = new QueryResultBO();
        Set<String> uniqueSet = new TreeSet<>();
        Iterable<RevCommit> revCommits = git.log().setRevFilter(new RevFilter() {
            @Override
            public boolean include(RevWalk revWalk, RevCommit revCommit) throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
                return revCommit.getFullMessage().toLowerCase().contains(keyword.toLowerCase());
            }
            @Override
            public RevFilter clone() {
                return this;
            }
        }).call();

        for (RevCommit revCommit : revCommits) {
            AnyObjectId foundObjectId = revCommit.getId();
            String revision = foundObjectId.getName();

            log.info("current commit id: {}, time: {}", revision, revCommit.getCommitTime());
            log.info(revCommit.getFullMessage());

            ResultItem item = new ResultItem();
            item.setRevision(revision);
            item.setCommitTime(new Date(revCommit.getCommitTime() * 1000L));
            item.setAuthor(revCommit.getAuthorIdent().getName());
            item.setEmail(revCommit.getAuthorIdent().getEmailAddress());
            item.setHistoryMessage(revCommit.getFullMessage());
            result.addCurrent(item);

            List<DiffEntry> diffEntries = BizUtils.showDiff(git, revision);
            if(Objects.isNull(diffEntries)) {
                continue;
            }

            for(DiffEntry diff: diffEntries) {
                String diffText = BizUtils.formatDiff(git, diff);
                List<List<String>> removedLineList = BizUtils.findRemovedLines(diffText);
                for (List<String> removedLines : removedLineList) {
                    String uniqueKey = diff.getOldPath() + "^" + StringUtils.toString(removedLines, "\n");
                    if(uniqueSet.contains(uniqueKey)) {
                        continue;
                    }

                    List<BeforeItem> childItems = BizUtils.findPathBySince(result, git, diff.getOldPath(), foundObjectId, removedLines);
                    for (BeforeItem child : childItems) {
                        child.setLastRevision(revision);
                        child.setLastAuthor(revCommit.getAuthorIdent().getName());
                        child.setLastCommitTime(new Date(revCommit.getCommitTime() * 1000L));
                        child.setFilePath(diff.getOldPath());
                        child.setNewLine(StringUtils.toString(removedLines, "\n"));
                        result.addBefore(child);
                    }
                    uniqueSet.add(uniqueKey);
                }
            }
        }
        return result;
    }

    /**
     * 读取当前工程里面的maven头信息
     * @return
     * @throws IOException
     */
    public MavenModel readMavenModel(Git git, String subFolderName) throws IOException {
        ObjectId objectId = git.getRepository().resolve("refs/heads/master");
        RevWalk revWalk = new RevWalk(git.getRepository());
        RevCommit revCommit = revWalk.parseCommit(objectId);
        RevTree revTree = revCommit.getTree();

        String filePath = StringUtils.isBlank(subFolderName) ? BizUtils.DEFAULT_POM_PATH : subFolderName + File.separator + BizUtils.DEFAULT_POM_PATH;
        TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), filePath, revTree);
        if(Objects.nonNull(treeWalk)) {
            ObjectId blobId = treeWalk.getObjectId(0);
            ObjectLoader loader = git.getRepository().open(blobId);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                Model model = reader.read(new ByteArrayInputStream(loader.getBytes()));
                return new MavenModel(model.getGroupId(), model.getArtifactId(), model.getVersion());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
