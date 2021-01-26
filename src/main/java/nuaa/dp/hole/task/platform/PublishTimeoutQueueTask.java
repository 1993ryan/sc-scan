package nuaa.dp.hole.task.platform;

import nuaa.dp.hole.biz.service.ITaskQueueService;
import nuaa.dp.hole.dal.dao.TaskQueueMapper;
import nuaa.dp.hole.dal.model.TaskQueue;
import nuaa.dp.hole.task.base.BaseTask;
import nuaa.dp.hole.task.base.TaskTopic;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: Oct 22, 2016 10:24:24 PM
 * @copyright Dapeng Yan
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * Oct 22, 2016      Dapeng Yan          v1.0.0
 */
@Component
public class PublishTimeoutQueueTask extends BaseTask {

    @Autowired
    private ITaskQueueService taskQueueService;

    @Autowired
    protected TaskQueueMapper taskQueueMapper;

    //5分钟1次
    @Scheduled(fixedDelay = 60 * 1000)
    public void start() {
        run();
    }

    @Override
    protected void doInternalTask() throws Exception {
        List<TaskQueue> taskList = this.taskQueueService.queryForTimeout();
//		logger.info("publish timeout task, size: {}", taskList.size());
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }

        try {
            for (TaskQueue task : taskList) {
                logger.info("topic: {}, bizId: {}", task.getTopic(), task.getBizId());
                if (0 == task.getTopic()) {
                    continue;
                }
                TaskTopic taskTopic = TaskTopic.valueOf(task.getTopic());
                if (Objects.isNull(taskTopic)) {
                    continue;
                }

                TaskCreateDto createDto = new TaskCreateDto(taskTopic);
                createDto.setBizId(task.getBizId());
                long nextSeconds = (task.getNextTime().getTime() - System.currentTimeMillis()) / 1000;
                createDto.setDelaySeconds(Integer.parseInt(String.valueOf(nextSeconds)));
                createDto.setExtInfo(task.getExtInfo());

                this.taskQueueService.insert(createDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
