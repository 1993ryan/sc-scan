package nuaa.dp.hole.task.trade;

import nuaa.dp.hole.biz.repository.GitRepository;
import nuaa.dp.hole.biz.service.ITaskQueueService;
import nuaa.dp.hole.biz.service.TaskService;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.task.base.QueueBaseTask;
import nuaa.dp.hole.task.base.TaskTopic;
import nuaa.dp.hole.task.bean.TaskBean;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

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
@Component
public class RepositoryCloneTask extends QueueBaseTask {

	@Autowired
	private ITaskQueueService taskQueueService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private GitRepository gitRepository;

	@Scheduled(fixedRate = 1000 * 15)
	public void start() {
		run();
	}

	@Override
	protected boolean doTask(TaskBean task) throws Exception {
		TaskItem taskItem = this.taskService.getTaskItem(task.getBizId());
		String REMOTE_URL = "https://github.com/" + taskItem.getDependentName() + ".git";
		String folderName = taskItem.getDependentName().replaceAll("/", "^");

		this.gitRepository.cloneRepository(folderName, REMOTE_URL);

		TaskCreateDto tcd = new TaskCreateDto(TaskTopic.COMMIT_HISTORY_MATCH);
		tcd.setBizId(task.getBizId());
		this.taskQueueService.insert(tcd);
		return true;
	}

	@Override
	protected TaskTopic getTopic() {
		return TaskTopic.REPOSITORY_CLONE;
	}

}
