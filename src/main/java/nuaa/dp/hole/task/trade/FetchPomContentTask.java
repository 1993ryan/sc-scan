package nuaa.dp.hole.task.trade;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.repository.DependentRepository;
import nuaa.dp.hole.biz.service.ITaskQueueService;
import nuaa.dp.hole.biz.service.ProjectDependService;
import nuaa.dp.hole.biz.service.TaskService;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.model.ProjectDependent;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.task.base.QueueBaseTask;
import nuaa.dp.hole.task.base.TaskTopic;
import nuaa.dp.hole.task.bean.TaskBean;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import nuaa.dp.hole.utils.StringUtils;
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
* @date: Jan 2, 2021 10:52:59 AM
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* Jan 2, 2021      Dapeng Yan          v1.0.0
 */
@Slf4j
@Component
public class FetchPomContentTask extends QueueBaseTask {

	@Autowired
	private ProjectDependService projectDependService;

	@Autowired
	private DependentRepository dependentRepository;

	@Scheduled(fixedRate = 1000)
	public void start() {
		run();
	}

	@Override
	protected boolean doTask(TaskBean task) throws Exception {
		ProjectDependent item = this.projectDependService.loadById(task.getBizId());
		item.setOrderStatus(OrderStatus.PROCESS.name());
		this.projectDependService.saveDependent(item);

		try {
			if(StringUtils.isBlank(item.getPomContent())) {
				boolean result = this.dependentRepository.fetchPomFileContent(item);
				if(false == result) {
					log.error("fetch pom content for dependent {} fail. statusCode: {}", item.getSimpleName(), item.getStatusCode());
				}
			}
		} catch(Exception e) {
			log.error("taskItem id: {}, project: {}, error: {}", item.getProjectId(), item.getSimpleName(), e);
		}

		return true;
	}

	@Override
	protected TaskTopic getTopic() {
		return TaskTopic.FETCH_POM_CONTENT;
	}

}
