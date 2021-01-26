package nuaa.dp.hole.biz.service;

import nuaa.dp.hole.dal.model.TaskQueue;
import nuaa.dp.hole.task.bean.JobConsumeRequest;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import nuaa.dp.hole.task.bean.TaskBean;

import java.util.List;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2019-05-25 19:20
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2019-05-25      Dapeng Yan          v1.0.0
 */
public interface ITaskQueueService {

    void insert(final TaskCreateDto dto);
    TaskBean getTask(JobConsumeRequest request);
    void completeTask(int topic, long bizId);

    List<TaskQueue> queryForTimeout();

}
