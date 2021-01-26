package nuaa.dp.hole.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	public static int CONNECT_TIMEOUT = 10000;
	public static int READ_TIMEOUT = 120000;
	public static final String USER_AGENT_DEFAULT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36";

	public static String doGet(String link) throws Exception {
		return doGet(link, null);
	}
	
	public static String doGet(String link, Map<String, String> paramMap) throws Exception {
		return doGet(link, paramMap, StringUtils.UTF_8);
	}

	public static String doGet(String link, Map<String, String> paramMap, String charset) throws Exception {
		return doGet(link, paramMap, charset, null);
	}

	public static String doGet(String link, Map<String, String> paramMap, String charset, Map<String, String> headers) throws Exception {
		try {
			String strUrl = assembleUrlParams(link, paramMap, StringUtils.UTF_8);
			URL url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			if("https".equals(url.getProtocol())) {
				HttpsURLConnection conn = (HttpsURLConnection) urlConn;
				final TrustManager[] tm = {new TrustAnyTrustManager()};
				final SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
				sslContext.init(null, tm, new java.security.SecureRandom());
				SSLSocketFactory ssf = sslContext.getSocketFactory();
				
				conn.setHostnameVerifier(new HostnameVerifier(){
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
		        });
				conn.setSSLSocketFactory(ssf);
			}
			
			urlConn.setRequestMethod("GET");
			urlConn.setRequestProperty("User-Agent", USER_AGENT_DEFAULT);
			urlConn.setConnectTimeout(CONNECT_TIMEOUT);
			urlConn.setReadTimeout(READ_TIMEOUT);
			urlConn.setDoOutput(true);
			urlConn.setRequestProperty("connection", "keep-alive");
			if (null != headers && !headers.isEmpty()) {
				for (Iterator<String> iter = headers.keySet().iterator(); iter.hasNext();) {
					String key = iter.next();
					urlConn.setRequestProperty(key, headers.get(key));
				}
			}
			return new String(IOUtils.toByteArray(urlConn.getInputStream()), charset);
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static String doPost(String strUrl, Map<String, String> paramMap, String charset) throws Exception {
		return doPost(strUrl, paramMap, charset, null);
	}
	
	public static String doPost(String strUrl, Map<String, String> paramMap, String charset, Map<String, String> headerMap) throws Exception {
		HttpPost postMethod = new HttpPost(strUrl);
		List<NameValuePair> nvpl = new ArrayList<NameValuePair>();
		postMethod.addHeader("User-Agent", USER_AGENT_DEFAULT);
		if(null != headerMap){
			for(String key: headerMap.keySet()) {
				postMethod.addHeader(key, headerMap.get(key));
			}
		}
		if(null != paramMap){
			for(String key: paramMap.keySet()){
				String value = paramMap.get(key);
				nvpl.add(new BasicNameValuePair(key, value));
			}
		}
		
		postMethod.setEntity(new UrlEncodedFormEntity(nvpl, charset));
		return doHttpRequest(postMethod, charset);
	}
	
	public static String httpPost(String strUrl, Map<String, Object> paramMap, String charset) {
		HttpPost postMethod = new HttpPost(strUrl);
		List<NameValuePair> nvpl = new ArrayList<NameValuePair>();
		postMethod.addHeader("User-Agent", USER_AGENT_DEFAULT);
		try {
			if(null != paramMap){
				for(String key: paramMap.keySet()){
					Object value = paramMap.get(key);
					nvpl.add(new BasicNameValuePair(key, ObjectUtils.toString(value)));
				}
			}
			
			postMethod.setEntity(new UrlEncodedFormEntity(nvpl, charset));
			return doHttpRequest(postMethod, charset);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String post(String strUrl, byte[] body, Charset charset, ContentType contentType) throws Exception {
		return post(strUrl, body, charset, contentType, null);
	}
	
	public static String post(String strUrl, byte[] body, Charset charset) throws Exception {
		return post(strUrl, body, charset, null);
	}
	
	public static String post(String strUrl, byte[] body, Charset charset, ContentType contentType, Map<String, String> headers) throws Exception {
		HttpPost postMethod = new HttpPost(strUrl);
		postMethod.setEntity(new ByteArrayEntity(body, contentType));
		postMethod.addHeader("User-Agent", USER_AGENT_DEFAULT);
		if(null != headers && !headers.isEmpty()){
			for(String key: headers.keySet()){
				postMethod.addHeader(key, headers.get(key));
			}
		}
		return doHttpRequest(postMethod, charset.name());
	}
	
	public static String doHttpRequest(HttpRequestBase postMethod, String charset) throws Exception {
		HttpResult result = doHttpRequest(postMethod);
		if(null != result && HttpStatus.SC_OK == result.getStatusCode() && null != result.getData()) {
			if(StringUtils.isBlank(charset)) {
				return new String(result.getData());
			} else {
				return new String(result.getData(), charset);
			}
		}
		return null;
	}
	
	public static HttpResult doHttpRequest(HttpRequestBase requestMethod) {
		HttpResult result = new HttpResult();
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(new KeyManager[0], new TrustManager[]{new TrustAnyTrustManager()}, null);
//			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
			PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            clientConnectionManager.setMaxTotal(100);
            clientConnectionManager.setDefaultMaxPerRoute(25);
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(clientConnectionManager).build();
			
			httpResponse = httpClient.execute(requestMethod);
			if(HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode() 
					|| HttpStatus.SC_NOT_MODIFIED == httpResponse.getStatusLine().getStatusCode()){
				
			} else {
				logger.error("http request error, status code is " + httpResponse.getStatusLine().getStatusCode() + ", strUrl: " + requestMethod.getURI().toString());
			}
			result.setStatusCode(httpResponse.getStatusLine().getStatusCode());
			
			Header[] headers = httpResponse.getAllHeaders();
			for(Header header: headers) {
				result.addRespHeader(header.getName(), header.getValue());
			}
			HttpEntity entity = httpResponse.getEntity();
			result.setData(EntityUtils.toByteArray(entity));
			return result;
		} catch(Exception e){
			logger.error("http request fail, url: {}, {}", requestMethod.getURI().toString(), e.getMessage());
		} finally{
			try {
				if(null != httpResponse){
					httpResponse.close();
					httpResponse = null;
				}
				if(null != requestMethod) {
					requestMethod.releaseConnection();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String doUploadFile(String strUrl, Map<String, String> parameters, String fileParamName,
			String filename, String contentType, byte[] data) {
		HttpURLConnection urlConn = null;
		try {
			URL url = new URL(strUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(CONNECT_TIMEOUT);
			urlConn.setReadTimeout(READ_TIMEOUT);
			urlConn.setDoOutput(true);
			urlConn.setRequestProperty("connection", "keep-alive");

			String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			boundary = "--" + boundary;
			StringBuffer params = new StringBuffer();
			if (parameters != null) {
				for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = parameters.get(name);
					params.append(boundary + "\r\n");
					params.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
					// params.append(URLEncoder.encode(value,
					// StringUtils.CHARSET_UTF8));
					params.append(value);
					params.append("\r\n");
				}
			}

			StringBuilder sb = new StringBuilder();
			// sb.append("\r\n");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + filename
					+ "\"\r\n");
			sb.append("Content-Type: " + contentType + "\r\n\r\n");
			byte[] fileDiv = sb.toString().getBytes();
			byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes();
			byte[] ps = params.toString().getBytes();

			OutputStream os = urlConn.getOutputStream();
			os.write(ps);
			os.write(fileDiv);
			os.write(data);
			os.write(endData);
			os.flush();
			os.close();
			
			return new String(IOUtils.toByteArray(urlConn.getInputStream()));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}
	
	public static String assembleUrlParams(String baseUrl, Map<String, String> args, String encoding) {
		StringBuilder url = new StringBuilder(baseUrl);
		if(null != args) {
			if(!baseUrl.contains("?")){
				url.append("?");
			}
			int index = 0;
			for (Entry<String, String> entry : args.entrySet()) {
				String v = encode(entry.getValue(), encoding);
				url.append(entry.getKey()).append('=').append(v);
				if (index < args.size() - 1) {
					url.append("&");
				}
				index++;
			}
		}
		return url.toString();
	}
	
	public static String encode(String value, String encoding) {
		if(StringUtils.isBlank(value)){
			return "";
		}
		try {
			return URLEncoder.encode(value, encoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("encode fail,value=" + value + ",encoding=" + encoding, e);
			return value;
		}
	}
}

class TrustAnyTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
