package com.common.net;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	// private static Logger logger = LogManager.getLogger(HttpClientUtil.class);
	static final int connectionRequestTimeout = 5000;// ms毫秒,从池中获取链接超时时间
	static final int connectTimeout = 5000;// ms毫秒,建立链接超时时间
	static final int socketTimeout = 10000;// ms毫秒,读取超时时间

	/**
	 * 
	 * @param url
	 * @param params
	 * @param proxy
	 * @param headers
	 * @return
	 */
	public static String getHttp(String url, Map<String, String> params, Map<String, String> headers, InetSocketAddress proxy) {
		String body = "";
		try {
			CloseableHttpClient getClient = HttpClients.createDefault();
			// 设置参数
			URIBuilder uriBuilder = new URIBuilder(url);
			if (params != null) {
				for (Entry<String, String> param : params.entrySet()) {
					uriBuilder.addParameter(param.getKey(), param.getValue());
				}
			}

			HttpGet get = new HttpGet(uriBuilder.build());

			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					get.setHeader(header.getKey(), header.getValue());
				}
			}

			// 设置代理、延迟
			Builder builder = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout);
			if (proxy != null) {
				HttpHost httpHost = new HttpHost(proxy.getHostName(), proxy.getPort());
				RequestConfig config = builder.setProxy(httpHost).build();
				get.setConfig(config);
			} else {
				RequestConfig config = builder.build();
				get.setConfig(config);
			}

			CloseableHttpResponse getResponse = getClient.execute(get);
			int code = getResponse.getStatusLine().getStatusCode();
			if (code >= 200 && code < 300) { // 请求成功
				// 获取实体
				HttpEntity entity = getResponse.getEntity();
				if (entity != null) {
					entity = new BufferedHttpEntity(entity);
				}
				body = EntityUtils.toString(entity, "UTF-8");
				// System.out.println("EntityUtils:\n" + body);
				// 释放实体
				EntityUtils.consume(entity);
			} else {
				body = " {\"状态码\":\"" + code + "\",\"Reason\":\"" + getResponse.getStatusLine().getReasonPhrase() + "\"}";
			}
			if (getClient != null)
				getClient.close();
		} catch (Exception e) {
			body = e.getMessage();
		}
		return body;
	}

	public static String postHttp(String url, Map<String, String> params, Map<String, String> headers, InetSocketAddress proxy) {
		String body = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					post.setHeader(header.getKey(), header.getValue());
				}
			}
			// 设置参数
			if (params != null) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Entry<String, String> param : params.entrySet()) {
					nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
				}
				post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			}

			// 设置代理、延迟
			Builder builder = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout);
			if (proxy != null) {
				HttpHost httpHost = new HttpHost(proxy.getHostName(), proxy.getPort());
				RequestConfig config = builder.setProxy(httpHost).build();
				post.setConfig(config);
			} else {
				RequestConfig config = builder.build();
				post.setConfig(config);
			}

			CloseableHttpResponse response = client.execute(post);
			// System.out.println("StatusLine:\n" + response.getStatusLine().toString() + "\n");
			int code = response.getStatusLine().getStatusCode();
			if (code >= 200 && code < 300) { // 请求成功
				// 获取实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity = new BufferedHttpEntity(entity);
				}
				body = EntityUtils.toString(entity, "UTF-8");
				// System.out.println("EntityUtils:\n" + body);
				// 释放实体
				EntityUtils.consume(entity);
			} else {
				body = " {\"状态码\":\"" + code + "\",\"Reason\":\"" + response.getStatusLine().getReasonPhrase() + "\"}";
			}
			if (client != null)
				client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;

	}

	/**
	 * 
	 * @param url
	 * @param params json格式
	 * @param headers
	 * @param proxy
	 * @return
	 */
	public static String postHttp(String url, String params, Map<String, String> headers, InetSocketAddress proxy) {
		String body = "";
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);

			post.setHeader("Content-Type", "application/json");
			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					post.setHeader(header.getKey(), header.getValue());
				}
			}

			// 设置参数
			if (params != null) {
				StringEntity entity = new StringEntity(params, "UTF-8");
				post.setEntity(entity);
			}

			// 设置代理、延迟
			Builder builder = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout);
			if (proxy != null) {
				HttpHost httpHost = new HttpHost(proxy.getHostName(), proxy.getPort());
				RequestConfig config = builder.setProxy(httpHost).build();
				post.setConfig(config);
			} else {
				RequestConfig config = builder.build();
				post.setConfig(config);
			}

			CloseableHttpResponse response = client.execute(post);
			// System.out.println("StatusLine:\n" + response.getStatusLine().toString() + "\n");
			int code = response.getStatusLine().getStatusCode();
			if (code >= 200 && code < 300) { // 请求成功
				// 获取实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity = new BufferedHttpEntity(entity);
				}
				body = EntityUtils.toString(entity, "UTF-8");
				// System.out.println("EntityUtils:\n" + body);
				// 释放实体
				EntityUtils.consume(entity);
			} else {
				body = " {\"状态码\":\"" + code + "\",\"Reason\":\"" + response.getStatusLine().getReasonPhrase() + "\"}";
			}
			if (client != null)
				client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;

	}

	public Map<String, String> getHeaders(String cookie, String userAgent, String host) {
		Map<String, String> headers = new HashMap<String, String>();
		// headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0");
		// headers.put("Host", "ecd.ec.com.cn");
		// headers.put("Cookie", cookie);
		return headers;
	}
}
