package org.yjcycc.tools.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Http 请求实用
 * @author HeQiang
 *
 */
public class HttpClientUtil {	
	// socket超时时间、连接超时时间、请求超时时间
	private RequestConfig requestConfig = RequestConfig.custom()
	        .setSocketTimeout(5000)
	        .setConnectTimeout(5000)
	        .setConnectionRequestTimeout(5000)
	        .build(); 
	
	public String Get(String url) throws Exception {
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httprequest = new HttpGet(url);
			httprequest.setConfig(requestConfig);
			
			try(CloseableHttpResponse response = httpclient.execute(httprequest)) {
				HttpEntity entity = response.getEntity();
				
				if(entity == null) {
					return null;
				}
				
				try(InputStream instream = entity.getContent()) {
					try(ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
						byte[] bytes = new byte[255];
						int len = 1;
						
						while((len=instream.read(bytes)) != -1) {
							outstream.write(bytes,0,len);
						}						
						return outstream.toString();
					} 
				} 
			} 
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	public String post(String url, Map<String, Object> params) throws Exception {
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			RequestBuilder rb = RequestBuilder.post();
			rb.setUri(url);
			rb.setConfig(requestConfig);
			
			
			for(String key : params.keySet()) {
				rb.addParameter(key, params.get(key).toString());
			}
			
			HttpUriRequest httprequest = rb.build();
			
			
			try(CloseableHttpResponse response = httpclient.execute(httprequest)) {
				HttpEntity entity = response.getEntity();
				
				if(entity == null) {
					return null;
				}
				
				try(InputStream instream = entity.getContent()) {
					try(ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
						byte[] bytes = new byte[255];
						int len = 1;
						
						while((len=instream.read(bytes)) != -1) {
							outstream.write(bytes,0,len);
						}						
						return outstream.toString();
					} 
				} 
			} 
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	public String post(String url, Map<String, String> header, String jsonContent) throws Exception {
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);			
			
			if(header != null && header.size() > 0) {
				for(String key : header.keySet()) {
					post.addHeader(key, header.get(key));
				}
			}
			
			StringEntity e = new StringEntity(jsonContent);
			e.setContentEncoding("utf-8");
			e.setContentType("application/json");
			
			post.setEntity(e);
			
			try(CloseableHttpResponse response = httpclient.execute(post)) {
				HttpEntity entity = response.getEntity();
				
				if(entity == null) {
					return null;
				}
				
				try(InputStream instream = entity.getContent()) {
					try(ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
						byte[] bytes = new byte[255];
						int len = 1;
						
						while((len=instream.read(bytes)) != -1) {
							outstream.write(bytes,0,len);
						}						
						return outstream.toString();
					} 
				} 
			} 
			
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	public String post(String url, Map<String, Object> params, String streamName, File iStream) throws Exception {
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			MultipartEntityBuilder meb = MultipartEntityBuilder.create();
			
			// 参数
			for(String key : params.keySet()) {
				meb.addTextBody(key, params.get(key).toString());
			}
			
			FileBody bin = new FileBody(iStream);
			meb.addPart(streamName, bin);
			
			HttpEntity reqEntity = meb.build();		
			HttpPost httpPost = new HttpPost(url);			
			httpPost.setEntity(reqEntity);
			httpPost.setConfig(requestConfig);
			
			try(CloseableHttpResponse response = httpclient.execute(httpPost)) {
				HttpEntity entity = response.getEntity();
				
				if(entity == null) {
					return null;
				}
				
				try(InputStream instream = entity.getContent()) {
					try(ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
						byte[] bytes = new byte[255];
						int len = 1;
						
						while((len=instream.read(bytes)) != -1) {
							outstream.write(bytes,0,len);
						}						
						return outstream.toString();
					} 
				} 
			} 
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	public String delete(String url, Map<String, String> header) throws Exception {
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpDelete delete = new HttpDelete(url);			
			
			if(header != null && header.size() > 0) {
				for(String key : header.keySet()) {
					delete.addHeader(key, header.get(key));
				}
			}
			
			try(CloseableHttpResponse response = httpclient.execute(delete)) {
				HttpEntity entity = response.getEntity();
				
				if(entity == null) {
					return null;
				}
				
				try(InputStream instream = entity.getContent()) {
					try(ByteArrayOutputStream outstream = new ByteArrayOutputStream()) {
						byte[] bytes = new byte[255];
						int len = 1;
						
						while((len=instream.read(bytes)) != -1) {
							outstream.write(bytes,0,len);
						}						
						return outstream.toString();
					} 
				} 
			} 
			
		} catch(Exception ex) {
			throw ex;
		}
	}
}
