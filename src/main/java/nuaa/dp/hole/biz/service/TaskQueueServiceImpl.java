package nuaa.dp.hole.biz.service;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.config.ParamHandler;
import nuaa.dp.hole.dal.dao.TaskQueueMapper;
import nuaa.dp.hole.dal.model.TaskQueue;
import nuaa.dp.hole.dal.model.TaskQueueExample;
import nuaa.dp.hole.task.base.DelayQueue;
import nuaa.dp.hole.task.bean.Job;
import nuaa.dp.hole.task.bean.JobConsumeRequest;
import nuaa.dp.hole.task.bean.TaskBean;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * 默认使用DelayQ, 如果发现DelayQ不可用时，先将任务插入数据库中
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapng Yan
 * @date: 2019-05-25 19:22
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2019-05-25      Dapeng Yan          v1.0.0
 */
@Slf4j
@Service
public class TaskQueueServiceImpl implements ITaskQueueService {

    @Autowired
    private ParamHandler paramHandler;

    @Autowired
    private TaskQueueMapper taskQueueMapper;

    private DelayQueue delayQueue = null;

    @PostConstruct
    public void init() {
        this.delayQueue = new DelayQueue(this.paramHandler.getDelayq().getServer());
    }

    @Override
    public void insert(final TaskCreateDto dto){
        boolean publishResult = false;
        try{
            Job job = new Job(this.paramHandler.getDelayq().getOrigin(), dto.getTopic().getTopic(), dto.getBizId());

            Calendar cal = Calendar.getInstance();
            int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
            int dstOffset = cal.get(Calendar.DST_OFFSET);
            cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            cal.add(Calendar.SECOND, dto.getDelaySeconds());

            job.setGmtNext(new Timestamp(cal.getTimeInMillis()));
            job.setBody(dto.getExtInfo());
            job.setEndpoint(dto.getEndpoint());
            job.setWithHost(dto.isWithHost());
            job.setRetryCount(dto.getRetryCount());
            job.setInterval(dto.getTopic().getInterval());
            job.setFibonacci(dto.isFibonacci());

            String result = this.delayQueue.publish(job);
            log.info("publish job: {}, bizId: {}, result: {}", dto.getTopic().name(), dto.getBizId(), result);
            if(StringUtils.isNotBlank(result)){
                log.error("publish job error " + dto.getTopic().name() + "," + dto.getBizId() + "," + dto.getExtInfo() +",result:" + result);
            } else {
                publishResult = true;
            }
        } catch(Exception ex){
            log.error("publish job error, topic: " + dto.getTopic() + ",bizId:" + dto.getBizId(), ex);
        } finally {
            this.deleteFromDB(dto.getTopic().getTopic(), dto.getBizId());
        }
    }

    @Override
    public TaskBean getTask(JobConsumeRequest request) {
        try{
            request.setOrigin(this.paramHandler.getDelayq().getOrigin());
            Job job = this.delayQueue.consume(request);
            if(null == job){
                return null;
            }
//			logger.info("consume task topic {}, bizId: {}", request.getTopic(), job.getBizId());
            TaskBean task = new TaskBean();
            task.setTopic(request.getTopic());
            task.setBizId(Long.parseLong(job.getBizId()));
            task.setExtInfo(job.getBody());
            task.setExeCount(job.getRetryCount());
            task.setEndpoint(job.getEndpoint());
            task.setGmtCreate(job.getGmtCreate());

            return task;
        } catch(Exception ex){
            log.error("consume job error", ex);
        }
        return null;
    }

    @Override
    public void completeTask(int topic, long bizId){
        try{
            this.delayQueue.ack(this.getJobIdFromTaskInfo(topic, bizId));
        } catch (Exception ex){
            log.error("delete job error", ex);
        }
    }

    @Override
    public List<TaskQueue> queryForTimeout() {
        TaskQueueExample example = new TaskQueueExample();
        example.createCriteria().andNextTimeLessThanOrEqualTo(DateUtils.addMinutes(DateUtils.now(), 10));
        example.setOrderByClause("id asc limit 1000");
        return this.taskQueueMapper.selectByExample(example);
    }

    /**
     * 强制删除任务，谨慎使用
     * @param topic
     * @param bizId
     */
    public void deleteTask(int topic, long bizId){
        try{
            this.delayQueue.delete(this.getJobIdFromTaskInfo(topic, bizId));
        } catch (Exception ex){
            log.error("delete job error", ex);
        }
    }

    private String getJobIdFromTaskInfo(int topic, long bizId){
        return this.paramHandler.getDelayq().getOrigin() + "_" + topic + "_" + bizId;
    }

    private int deleteFromDB(int topic, long bizId) {
        TaskQueueExample example = new TaskQueueExample();
        example.createCriteria().andTopicEqualTo(topic).andBizIdEqualTo(bizId);
        return this.taskQueueMapper.deleteByExample(example);
    }

}
