package com.iflytek.im.demo.common.http;

import android.text.TextUtils;

import com.iflytek.im.demo.common.Logging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


/**
 * Http通用管理类
 *
 * @author ruiding
 * @date 2015/01/07
 */
@SuppressWarnings("deprecation")
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private final static boolean DEBUG = true;
    private static final String HTTP_STATUS_CODE_ERROR = "ERROR";

    /**
     * OK: Success!
     */
    public static final int OK = 200;
    /**
     * Not Modified: There was no new data to return.
     */
    public static final int NOT_MODIFIED = 304;
    /**
     * Bad Request: The request was invalid. An accompanying error message will
     * explain why. This is the status code will be returned during rate
     * limiting.
     */
    public static final int BAD_REQUEST = 400;
    /**
     * Not Authorized: Authentication credentials were missing or incorrect.
     */
    public static final int NOT_AUTHORIZED = 401;
    /**
     * Forbidden: The request is understood, but it has been refused. An
     * accompanying error message will explain why.
     */
    public static final int FORBIDDEN = 403;
    /**
     * Not Found: The URI requested is invalid or the resource requested, such
     * as a user, does not exists.
     */
    public static final int NOT_FOUND = 404;
    /**
     * Not Acceptable: Returned by the Search API when an invalid format is
     * specified in the request.
     */
    public static final int NOT_ACCEPTABLE = 406;
    /**
     * Internal Server Error: Something is broken. Please post to the group so
     * the Weibo team can investigate.
     */
    public static final int INTERNAL_SERVER_ERROR = 500;
    /**
     * Bad Gateway: Weibo is down or being upgraded.
     */
    public static final int BAD_GATEWAY = 502;
    /**
     * Service Unavailable: The Weibo servers are up, but overloaded with
     * requests. Try again later. The search and trend methods use this to
     * indicate when you are being rate limited.
     */
    public static final int SERVICE_UNAVAILABLE = 503;

    private static final int RETRY_TIME = 3;
    private static final String CHARSET_UTF8 = "UTF-8";
    //private static final String CHARSET_GBK = "GBK";
    private static final String SSL_DEFAULT_SCHEME = "https";
    private static final int SSL_DEFAULT_PORT = 443;
    private static final int connectionTimeoutMillis = 30000;
    private static final int socketTimeoutMillis = 30000;

    private static DefaultHttpClient httpclient;
    // 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
        // 自定义的恢复策略
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {
            // 设置恢复策略，在发生异常时候将自动重试3次
            if (executionCount >= RETRY_TIME) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                return false;
            }
            HttpRequest request = (HttpRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
            if (!idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }
    };

    private static BasicHttpContext localcontext = new BasicHttpContext();

    // 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
    private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        // 自定义响应处理
        public String handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            Response res = new Response(response);
            int statusCode = response.getStatusLine().getStatusCode();
            try {
                HandleResponseStatusCode(statusCode, res);
            } catch (HttpException e1) {
                Logging.d(TAG, "", e1);
            }

            if (statusCode != OK) {
                return HTTP_STATUS_CODE_ERROR;
            }
            try {
                return res.asString();
            } catch (ResponseException e) {
                Logging.d(TAG, "", e);
            }

            return null;
        }
    };

    /**
     * 解析HTTP错误码
     *
     * @param statusCode
     * @return
     */
    private static String getCause(int statusCode) {
        String cause = null;
        switch (statusCode) {
            case NOT_MODIFIED:
                break;
            case BAD_REQUEST:
                cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
                break;
            case NOT_AUTHORIZED:
                cause = "Authentication credentials were missing or incorrect.";
                break;
            case FORBIDDEN:
                cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
                break;
            case NOT_FOUND:
                cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
                break;
            case NOT_ACCEPTABLE:
                cause = "Returned by the Search API when an invalid format is specified in the request.";
                break;
            case INTERNAL_SERVER_ERROR:
                cause = "Something is broken.  Please post to the group so the Weibo team can investigate.";
                break;
            case BAD_GATEWAY:
                cause = "Weibo is down or being upgraded.";
                break;
            case SERVICE_UNAVAILABLE:
                cause = "Service Unavailable: The Weibo servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
                break;
            default:
                cause = "";
        }
        return statusCode + ":" + cause;
    }

    /**
     * Handle Status code
     *
     * @param statusCode 响应的状态码
     * @param res        服务器响应
     * @throws HttpException 当响应码不为200时都会报出此异常:<br />
     *                       <li>HttpRequestException, 通常发生在请求的错误,如请求错误了 网址导致404等, 抛出此异常,
     *                       首先检查request log, 确认不是人为错误导致请求失败</li> <li>HttpAuthException,
     *                       通常发生在Auth失败, 检查用于验证登录的用户名/密码/KEY等</li> <li>
     *                       HttpRefusedException, 通常发生在服务器接受到请求, 但拒绝请求, 可是多种原因, 具体原因
     *                       服务器会返回拒绝理由, 调用HttpRefusedException#getError#getMessage查看</li>
     *                       <li>HttpServerException, 通常发生在服务器发生错误时, 检查服务器端是否在正常提供服务</li>
     *                       <li>HttpException, 其他未知错误.</li>
     */
    private static void HandleResponseStatusCode(int statusCode, Response res)
            throws HttpException {
        String msg = getCause(statusCode) + "\n";
        //RefuseError error = null;

        switch (statusCode) {
            // It's OK, do nothing
            case OK:
                break;

            // Mine mistake, Check the Log
            case NOT_MODIFIED:
            case BAD_REQUEST:
            case NOT_FOUND:
            case NOT_ACCEPTABLE:
                 
            case NOT_AUTHORIZED:
                //throw new HttpAuthException(msg + res.asString(), statusCode);

                // Server will return a error message, use
                // HttpRefusedException#getError() to see.
            case FORBIDDEN:
                //throw new HttpRefusedException(msg, statusCode);

                // Something wrong with server
            case INTERNAL_SERVER_ERROR:
            case BAD_GATEWAY:
            case SERVICE_UNAVAILABLE:
                //throw new HttpServerException(msg, statusCode);

                // Others
            default:
                throw new HttpException(msg + res.asString(), statusCode);
        }
    }

    /**
     * Get方式提交,URL中包含查询参数, 格式：http://www.g.cn?search=p&name=s.....
     *
     * @param url 提交地址
     * @return 响应消息
     * @throws HttpException
     */
    public static String get(String url) throws HttpException {
        return get(url, null, null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn
     *
     * @param url    提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     * @throws HttpException
     */
    public static String get(String url, Map<String, String> params) throws HttpException {
        return get(url, params, null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.g.cn
     *
     * @param url     提交地址
     * @param params  查询参数集, 键/值对
     * @param charset 参数提交编码集
     * @return 响应消息
     * @throws HttpException
     */
    public static String get(String url, Map<String, String> params,
                             String charset) throws HttpException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        List<NameValuePair> qparams = getParamsList(params);
        if (qparams != null && qparams.size() > 0) {
            charset = (charset == null ? CHARSET_UTF8 : charset);
            String formatParams = URLEncodedUtils.format(qparams, charset);
            url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url
                    .substring(0, url.indexOf("?") + 1) + formatParams);
        }
        DefaultHttpClient httpclient = getDefaultHttpClient(charset);
        HttpGet hg = new HttpGet(url);
        // 发送请求，得到响应
        String responseStr = null;
        try {
            responseStr = httpclient.execute(hg, responseHandler);
        } catch (ClientProtocolException e) {
            throw new HttpException("客户端连接协议错误", e);
        } catch (IOException e) {
            throw new HttpException("IO操作异常", e);
        } finally {
            abortConnection(hg, httpclient);
        }
        return responseStr;
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.g.cn
     *
     * @param url    提交地址
     * @param params 提交参数集, 键/值对
     * @return 响应消息
     * @throws HttpException
     */
    public static String post(String url, Map<String, String> params) throws HttpException {
        return post(url, params, null);
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.g.cn
     *
     * @param url     提交地址
     * @param params  提交参数集, 键/值对
     * @param charset 参数提交编码集
     * @return 响应消息
     * @throws HttpException
     */
    public static String post(String url, Map<String, String> params,
                              String charset) throws HttpException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        // 创建HttpClient实例
        DefaultHttpClient httpclient = getDefaultHttpClient(charset);
        UrlEncodedFormEntity formEntity = null;
        try {
            if (TextUtils.isEmpty(charset)) {
                formEntity = new UrlEncodedFormEntity(getParamsList(params));
            } else {
                formEntity = new UrlEncodedFormEntity(getParamsList(params),
                        charset);
            }
        } catch (UnsupportedEncodingException e) {
            Logging.d(TAG, "post | throw UnsupportedEncodingException!!!");
            throw new HttpException("不支持的编码集", e);
        }
        HttpPost hp = new HttpPost(url);
        hp.setEntity(formEntity);
        // 发送请求，得到响应
        String responseStr = null;
        try {
            responseStr = httpclient.execute(hp, responseHandler);
        } catch (ClientProtocolException e) {
            throw new HttpException("客户端连接协议错误", e);
        } catch (IOException e) {
            throw new HttpException("IO操作异常", e);
        } finally {
            abortConnection(hp, httpclient);
        }
        return responseStr;
    }

    public static String post(String url, String content) throws HttpException {
        // 发送请求，得到响应
        String responseStr = null;
        HttpPost hp = null;
        DefaultHttpClient httpclient = null;
        try {
            httpclient = getDefaultHttpClient(CHARSET_UTF8);
            hp = new HttpPost(url);
            HttpEntity entity = new StringEntity(new String (content.getBytes(),CHARSET_UTF8),CHARSET_UTF8);
           // Logging.d(TAG, "post | Encoding " +  entity.getContentEncoding()+ " Content"+ entity.getContent());
           
            hp.setEntity(entity);
            String host =  hp.getURI().getHost();
            Logging.d(TAG, "post | host = " + host);
            hp.addHeader("Host", host);
            hp.addHeader("Content-Type", "text/html;charset=UTF-8");
            responseStr = httpclient.execute(hp, responseHandler);
        } catch (UnsupportedEncodingException e) {
            Logging.d(TAG, "post | throw UnsupportedEncodingException!!!");
            throw new HttpException("不支持的编码集", e);
        } catch (ClientProtocolException e) {
            throw new HttpException("客户端连接协议错误", e);
        } catch (IOException e) {
            throw new HttpException("IO操作异常", e);
        } finally {
            abortConnection(hp, httpclient);
        }
        return responseStr;
    }

    /**
     * 获取DefaultHttpClient实例
     *
     * @param charset 参数编码集, 可空
     * @return DefaultHttpClient 对象
     */
    public static DefaultHttpClient getDefaultHttpClient(final String charset) {
        if (httpclient == null) {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,
                    Boolean.FALSE);
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
                    charset == null ? CHARSET_UTF8 : charset);
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);

			/* DefaultHttpClient */
            httpclient = new DefaultHttpClient(conMgr,
                    params);
            httpclient.setHttpRequestRetryHandler(requestRetryHandler);
            HttpConnectionParams.setConnectionTimeout(params,
                    connectionTimeoutMillis);
            HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);
        }
        return httpclient;
    }

    /**
     * 释放HttpClient连接
     *
     * @param hrb        请求对象
     * @param httpclient client对象
     */
    public static void abortConnection(final HttpRequestBase hrb,
                                       final HttpClient httpclient) {
        if (hrb != null) {
            hrb.abort();
        }
//		if (httpclient != null) {
//			httpclient.getConnectionManager().shutdown();
//		}
    }

    /**
     * 将传入的键/值对参数转换为NameValuePair参数集
     *
     * @param paramsMap 参数集, 键/值对
     * @return NameValuePair参数集
     */
    private static List<NameValuePair> getParamsList(
            Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return null;
        }
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, String> map : paramsMap.entrySet()) {
            params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
        }
        return params;
    }

    private static String format(List<? extends NameValuePair> parameters) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair parameter : parameters) {
            String encodedName = parameter.getName();
            String value = parameter.getValue();
            String encodedValue = (value != null) ? value : "";
            if (result.length() > 0)
                result.append("&");
            result.append(encodedName);
            result.append("=");
            result.append(encodedValue);
        }
        return result.toString();
    }
    
    public static String processRequest(HttpPacket packet) throws HttpException{
    	switch (packet.getRequestType()) {
		case POST:
	        String postResponseStr = null;
	        HttpPost postRequest = null;
	        DefaultHttpClient postHttpClient = null;
	        try {
	        	postHttpClient = getDefaultHttpClient(CHARSET_UTF8);
	        	String URL = packet.getUrl();
	        	Logging.d(TAG, "url == "+ URL);
	            postRequest = new HttpPost(URL);
	            HttpEntity entity = new StringEntity(packet.getContent(),CHARSET_UTF8);
	            postRequest.setEntity(entity);
//	            String host =  postRequest.getURI().getHost();
//	            postRequest.addHeader("Host", host);
	            for(Entry<String, String> entry : packet.getHeaders().entrySet()){
	            	postRequest.addHeader(entry.getKey(), entry.getValue());
	            }
	            postResponseStr = postHttpClient.execute(postRequest, responseHandler);
	            return postResponseStr;
	        } catch (ClientProtocolException e) {
	            //throw new HttpException("客户端连接协议错误", e);
	        } catch (IOException e) {
	            //throw new HttpException("IO操作异常", e);
	        } finally {
	            abortConnection(postRequest, postHttpClient);
	        }
	        return "network error";
	       
		case GET:
			String getResponseStr = null;
			HttpGet getRequest = null;
			DefaultHttpClient getHttpClient = null;
			try {
				getHttpClient = getDefaultHttpClient(CHARSET_UTF8);
				getRequest = new HttpGet(packet.getUrl() + "?appid=" + packet.getContent().substring(10, 18));
				// String host = postRequest.getURI().getHost();
				// postRequest.addHeader("Host", host);
				for (Entry<String, String> entry : packet.getHeaders().entrySet()) {
					getRequest.addHeader(entry.getKey(), entry.getValue());
				}
				getResponseStr = getHttpClient.execute(getRequest, responseHandler);
			} catch (ClientProtocolException e) {
				throw new HttpException("客户端连接协议错误", e);
			} catch (IOException e) {
				throw new HttpException("IO操作异常", e);
			} finally {
				abortConnection(getRequest, getHttpClient);
			}
			return getResponseStr;

		default:
			throw new HttpException("process unknown Request Type");
		}
    }


    public static String processHttpsRequest(HttpPacket packet) throws com.iflytek.cloud.im.core.exception.HttpException {
        String responseStr = "";

        switch (packet.getRequestType()) {
            case POST:
                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new TrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostNameVerifier());
                    HttpsURLConnection conn = (HttpsURLConnection)new URL(packet.getUrl()).openConnection();
                    conn.setRequestMethod("POST");
                    Map<String ,String > headers = packet.getHeaders();
                    for(String key : headers.keySet()){
                        String value = headers.get(key);
                        conn.setRequestProperty(key, value);
                    }
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    if (packet.getContent() != null)
                        out.writeBytes(packet.getContent());
                    out.flush();
                    out.close();
                    conn.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null)  {
                        responseStr += line;
                    }
                    if (responseStr.contains("{")){
                        responseStr = responseStr.substring(responseStr.indexOf("{"),responseStr.lastIndexOf("}")+1);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return responseStr;
            case GET:
                try {
                    String getUrl = packet.getUrl() + "?appid=" + packet.getContent().substring(10, 18);


                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new TrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostNameVerifier());
                    HttpsURLConnection conn = (HttpsURLConnection)new URL(getUrl).openConnection();
                    conn.setRequestMethod("GET");
                    Map<String ,String > headers = packet.getHeaders();
                    for(String key : headers.keySet()){
                        String value = headers.get(key);
                        conn.setRequestProperty(key, value);
                    }
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    if (packet.getContent() != null)
                        out.writeBytes(packet.getContent());
                    out.flush();
                    out.close();
                    conn.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null)  {
                        responseStr += line;
                    }
                    if (responseStr.contains("{")){
                        responseStr = responseStr.substring(responseStr.indexOf("{"),responseStr.lastIndexOf("}")+1);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return responseStr;
            default:
                throw new com.iflytek.cloud.im.core.exception.HttpException("process unknown Request Type");
        }
    }


    private static class HostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }
    }


    private static class TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }



}