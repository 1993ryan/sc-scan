package nuaa.dp.hole.task.trade;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.repository.TaskRepository;
import nuaa.dp.hole.biz.service.ProjectInfoService;
import nuaa.dp.hole.biz.service.TaskService;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.dao.TaskItemMapper;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.dal.model.TaskChild;
import nuaa.dp.hole.dal.model.TaskInfo;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.task.base.QueueBaseTask;
import nuaa.dp.hole.task.base.TaskTopic;
import nuaa.dp.hole.task.bean.TaskBean;
import nuaa.dp.hole.utils.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 *
* @Copyright Dapeng Yan
* @Description: 
* @version: v1.0.0
* @author: Dapeng Yan
* @date: Jan 15, 2019 10:52:59 AM
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* Jan 15, 2019      Dapeng Yan          v1.0.0
 */
@Slf4j
@Component
public class CommitHistoryMatchTask extends QueueBaseTask {

	private static String FILE_TO_READ = "pom.xml";

	@Autowired
	private TaskService taskService;

	@Autowired
	private ProjectInfoService projectInfoService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TaskItemMapper taskItemMapper;

	@Scheduled(fixedRate = 1000 * 15)
	public void start() {
		run();
	}

	@Override
	protected boolean doTask(TaskBean task) throws Exception {
		TaskItem taskItem = this.taskService.getTaskItem(task.getBizId());
		TaskInfo taskInfo = this.taskService.loadById(taskItem.getTaskId());
		if(Objects.isNull(taskInfo)) {
			return true;
		}
		ProjectInfo projectInfo = this.projectInfoService.findByName(taskInfo.getProjectName());
		String folderName = taskItem.getDependentName().replaceAll("/", "^");
		File localPath = new File("tmp/" + folderName);
		Git git = Git.open(localPath);
		Repository repo = git.getRepository();

		Iterable<RevCommit> revCommits = git.log().addPath(FILE_TO_READ).call();
		for (RevCommit revCommit : revCommits) {
			AnyObjectId foundObjectId = revCommit.getId();
			String revision = foundObjectId.getName();
			String content = this.fetchContentFromRepository(repo, foundObjectId, FILE_TO_READ);
			log.info("revision: {}, content: {}", revision, content);

			TaskChild child = new TaskChild();
			child.setArtifactId(taskItem.getArtifactId());
			child.setFoundVersions(taskItem.getFoundVersions());
			String matchedVersion = this.taskRepository.parsePomFile(projectInfo.getGroupId(), child, content);
			if(StringUtils.isNotBlank(matchedVersion)) {
				taskItem.setMatchedVersion(matchedVersion);
				taskItem.setOtherVersion(revision);

				break;
			}
		}

		taskItem.setOrderStatus(OrderStatus.SUCCESS.name());
		this.taskItemMapper.updateByPrimaryKeySelective(taskItem);

		if(StringUtils.isNotBlank(taskItem.getOtherVersion())) {
			this.taskRepository.refreshMatchedCount(taskItem.getTaskId(), taskItem.getArtifactId());
		}

		return true;
	}

	private String fetchContentFromRepository(Repository repo, AnyObjectId commitId, String path) throws IOException {
		RevWalk revWalk = new RevWalk(repo);
		RevCommit commit = revWalk.parseCommit(commitId);
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repo);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(path));
		if (!treeWalk.next()) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repo.open(objectId);
		loader.copyTo(baos);
		return baos.toString();
	}

	@Override
	protected TaskTopic getTopic() {
		return TaskTopic.COMMIT_HISTORY_MATCH;
	}

}
