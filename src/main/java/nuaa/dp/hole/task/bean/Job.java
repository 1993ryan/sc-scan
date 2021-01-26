package nuaa.dp.hole.task.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Job {
	private int origin;
	private int topic;
	private String bizId;

	@JSONField(format="yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date gmtCreate;

	@JSONField(format="yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date gmtNext;
	private boolean fibonacci = false;
	private int interval = 0;

	private String body;
	private int retryCount;

	private String endpoint;
	private boolean withHost = false;

	public Job(){

	}

	public Job(int origin, int topic, long bizId){
		this.origin = origin;
		this.topic = topic;
		this.bizId = String.valueOf(bizId);
	}

	public int getOrigin() {
		return origin;
	}
	public void setOrigin(int origin) {
		this.origin = origin;
	}
	public int getTopic() {
		return topic;
	}
	public void setTopic(int topic) {
		this.topic = topic;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	public Date getGmtNext() {
		return gmtNext;
	}

	public void setGmtNext(Date gmtNext) {
		this.gmtNext = gmtNext;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public boolean isWithHost() {
		return withHost;
	}

	public void setWithHost(boolean withHost) {
		this.withHost = withHost;
	}

	public boolean isFibonacci() {
		return fibonacci;
	}

	public void setFibonacci(boolean fibonacci) {
		this.fibonacci = fibonacci;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
