package nuaa.dp.hole.task.base;

import nuaa.dp.hole.biz.service.ITaskQueueService;
import nuaa.dp.hole.task.bean.JobConsumeRequest;
import nuaa.dp.hole.task.bean.TaskBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时任务基类
 * <p>
 * 所有基于task_queue
 *
 * @author biliang.wbl
 */
public abstract class QueueBaseTask extends BaseTask {
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	@Autowired
	protected ITaskQueueService taskQueueService;

	@Override
	protected void doInternalTask(){
		JobConsumeRequest request = new JobConsumeRequest();
		request.setTopic(this.getTopic().getTopic());
		request.setNextSeconds(this.getTopic().getInterval());
		request.setEndpoint(getHostName());
		request.setWithHost(this.isWithHost());

		while(true){
			final TaskBean task = this.taskQueueService.getTask(request);
			if(null == task){
				break;
			}

			executor.submit(() -> {
				doTaskWrap(task);
			});
		}
	}



	@Transactional
	public void doTaskWrap(TaskBean task) {
		logger.info("doTask ===>>> topic:" + this.getTopic().name() + ", biz_id: " + task.getBizId() + ", exe_count:" + task.getExeCount());
		try {
			boolean delete = doTask(task);
			if (delete) {
				this.taskQueueService.completeTask(this.getTopic().getTopic(), task.getBizId());
			}
		} catch (Exception e) {
			logger.error("doTaskWrap fail, topic: "+ getTopic().name() +", bizId: " + task.getBizId(), e);
			e.printStackTrace();
		}
	}

	protected boolean isWithHost() {
		return false;
	}

	protected abstract boolean doTask(TaskBean task) throws Exception;

	protected abstract TaskTopic getTopic();

}
