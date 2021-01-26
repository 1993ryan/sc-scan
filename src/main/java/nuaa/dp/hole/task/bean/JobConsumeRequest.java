package nuaa.dp.hole.task.bean;

import lombok.Data;

@Data
public class JobConsumeRequest {

	/** 源，一般为系统的唯一标识 */
	private int origin;

	/** 消息的Topic */
	private int topic;

	/** TTR超时时间，在之后的*秒后未收到ACK则会重新消费 */
	private int nextSeconds;

	/** 获取任务的机器名 */
	private String endpoint;

	/** 任务是否区分主机运行，true - 只运行该主机下的任务 */
	private boolean withHost = false;

}
