package nuaa.dp.hole.task.bean;


import nuaa.dp.hole.task.base.TaskTopic;

/**
 * @Copyright MiXuan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2019-05-25 19:14
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2019-05-25      Dapeng Yan          v1.0.0
 */
public class TaskCreateDto {
    private long bizId;
    private final TaskTopic topic;
    private String extInfo;
    /** 执行的机器名称 */
    private String endpoint = "";

    // 默认 立刻执行
    private int delaySeconds = 0;
    private boolean withHost = false;
    private int retryCount = 0;
    private boolean fibonacci = false;

    public TaskCreateDto(TaskTopic topic) {
        super();
        this.topic = topic;
    }

    public long getBizId() {
        return bizId;
    }

    public void setBizId(long bizId) {
        this.bizId = bizId;
    }

    public TaskTopic getTopic() {
        return topic;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isWithHost() {
        return withHost;
    }

    public void setWithHost(boolean withHost) {
        this.withHost = withHost;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isFibonacci() {
        return fibonacci;
    }

    public void setFibonacci(boolean fibonacci) {
        this.fibonacci = fibonacci;
    }

}

