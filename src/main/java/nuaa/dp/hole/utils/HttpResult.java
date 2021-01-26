package nuaa.dp.hole.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class HttpResult {

	private int statusCode;
	private List<NameValuePair> respHeaders = new ArrayList<NameValuePair>();
	private byte[] data;
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void addRespHeader(String key, String value) {
		this.respHeaders.add(new BasicNameValuePair(key, value));
	}
	public List<NameValuePair> getRespHeaders() {
		return respHeaders;
	}
}
