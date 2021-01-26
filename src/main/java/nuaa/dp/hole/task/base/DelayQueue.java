package nuaa.dp.hole.task.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.task.bean.Job;
import nuaa.dp.hole.task.bean.JobConsumeRequest;
import nuaa.dp.hole.utils.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @Copyright MiXuan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2019-05-25 19:22
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2019-05-25     Dapeng Yan          v1.0.0
 */
@Slf4j
public class DelayQueue {
	private String baseUrl = "";

	public DelayQueue(String uri){
		this.baseUrl = uri + (uri.endsWith("/")?"":"/");
	}

	/**
	 * 发布延迟消息任务，生产者调用
	 * @param job
	 * @return 如果出错则返回错误信息，否则返回空串
	 * @throws Exception
	 */
	public String publish(Job job) throws Exception {
		String strResponse = new RestTemplate().postForObject(baseUrl + "publish", JSON.toJSONBytes(job), String.class);
		if(StringUtils.isEmpty(strResponse) || strResponse.startsWith("<html>")){
			return "delayq server error";
		}

		JSONObject json = JSON.parseObject(strResponse);
		if(null == json){
			return null;
		} else if(false == json.getBoolean("success")){
			return json.getString("data");
		} else {
			return "";
		}
	}

	/**
	 * 消费消息, 消费者调用
	 * @param request 参数消息体
	 * @return
	 * @throws Exception
	 */
	public Job consume(JobConsumeRequest request) throws Exception {
		String strResponse = new RestTemplate().postForObject(baseUrl + "consume", JSON.toJSONBytes(request), String.class);
		if(StringUtils.isEmpty(strResponse)){
			return null;
		}

//		logger.info("comsume job response<<<==={}", strResponse);
		JSONObject json = JSON.parseObject(strResponse);
		if(null == json || false == json.getBoolean("success")){
			return null;
		} else {
			JSONObject dataJson = json.getJSONObject("data");
			Job job = new Job();
			job.setOrigin(dataJson.getIntValue("origin"));
			job.setTopic(dataJson.getIntValue("topic"));
			job.setBizId(dataJson.getString("bizId"));
			job.setBody(dataJson.getString("body"));
			job.setRetryCount(dataJson.getIntValue("retryCount"));
			job.setEndpoint(dataJson.getString("endpoint"));
			String strGmtCreate = dataJson.getString("gmtCreate");
			if(StringUtils.isNotBlank(strGmtCreate)) {
				job.setGmtCreate(DateUtils.parseDate(strGmtCreate.substring(0, "2017-12-30T02:19:06".length()), "yyyy-MM-dd'T'HH:mm:ss"));
			}

			String strGmtNext = dataJson.getString("gmtNext");
			if(StringUtils.isNotBlank(strGmtNext)) {
				job.setGmtCreate(DateUtils.parseDate(strGmtNext.substring(0, "2017-12-30T02:19:06".length()), "yyyy-MM-dd'T'HH:mm:ss"));
			}
			return job;
		}
	}

	/**
	 * 消息消费成功后，告知DelayQ，否则会重复消费
	 * @param jobId 格式为origin_topic_bizId, 中间以英文状态的下划线拼接
	 * @return 如果出错则返回错误信息，否则返回空串
	 * @throws Exception
	 */
	public String ack(String jobId) throws Exception {
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("jobId", jobId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(paramMap, headers);
		String strResponse = new RestTemplate().postForObject(baseUrl + "ack", request, String.class);
		JSONObject json = JSON.parseObject(strResponse);
		if(null == json){
			return null;
		} else if(false == json.getBoolean("success")){
			return json.getString("data");
		} else {
			return "";
		}
	}

	/**
	 * 客户端主动发起任务删除请求，若消息尚未消费则执行强制删除
	 * @param jobId 格式为origin_topic_bizId, 中间以英文状态的下划线拼接
	 * @return 如果出错则返回错误信息，否则返回空串
	 * @throws Exception
	 */
	public String delete(String jobId) throws Exception {
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("jobId", jobId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(paramMap, headers);
		String strResponse = new RestTemplate().postForObject(baseUrl + "delete", request, String.class);
		JSONObject json = JSON.parseObject(strResponse);
		if(null == json){
			return null;
		} else if(false == json.getBoolean("success")){
			return json.getString("data");
		} else {
			return "";
		}
	}
}
