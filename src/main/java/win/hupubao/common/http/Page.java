/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package win.hupubao.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import win.hupubao.common.utils.LoggerUtils;
import win.hupubao.common.utils.StringUtils;
import win.hupubao.common.utils.XmlUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ysdxz207
 * @date 2018-06-13 17:44:35
 * <p>
 * 通用HTTP请求工具
 */

public class Page {
    private static int TIMEOUT_REQUEST = 5000;
    private static int TIMEOUT_CONNECTION = 5000;
    private static int TIMEOUT_READ_DATA = 12000;
    private static int RETRY_TIMES = 0;
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    private static String CHARSET = "UTF-8";

    private static boolean IGNORE_USER_AGENT = false;
    private static boolean LOGGER = true;

    private static Map<String, String> HEADERS = new HashMap<>();

    private static final Pattern PATTERN_CHARSET = Pattern.compile(".*charset=([^;]*).*");
    private static final Pattern PATTERN_CHARSET_DEEP = Pattern.compile(".*charset=\"(.*)\".*");

    public static Page create() {
        return new Page();
    }

    public Page readTimeout(int readTimeoutMillis) {
        TIMEOUT_READ_DATA = readTimeoutMillis;
        return this;
    }

    public Page requestTimeout(int requestTimeoutMillis) {
        TIMEOUT_REQUEST = requestTimeoutMillis;
        return this;
    }

    public Page connectionTimeout(int connectionTimeoutMillis) {
        TIMEOUT_CONNECTION = connectionTimeoutMillis;
        return this;
    }

    public Page retryTimes(int n) {
        RETRY_TIMES = n;
        return this;
    }

    public Page userAgent(String userAgent) {
        if (StringUtils.isNotBlank(userAgent)) {
            USER_AGENT = userAgent;
        }
        return this;
    }

    public Page ignoreUserAgent(boolean ignoreUserAgent) {
        IGNORE_USER_AGENT = ignoreUserAgent;
        return this;
    }

    public Page postCharset(String charset) {
        if (StringUtils.isBlank(charset)) {
            CHARSET = charset;
        }
        return this;
    }

    public Page loggerOn() {
        LOGGER = true;
        return this;
    }

    public Page loggerOff() {
        LOGGER = false;
        return this;
    }

    public Page header(String key, String value) {
        HEADERS.put(key, value);
        return this;
    }

    private HttpRequestBase getMethod(String url,
                                      String method,
                                      Object params) {

        HttpRequestBase httpMethod;

        switch (method) {
            default:
            case "GET":

                httpMethod = buildGetMethod(url, params);
                break;
            case "POST":

                httpMethod = buildPostMethod(url, params);
                break;
        }


        return httpMethod;
    }

    private HttpRequestBase buildGetMethod(String url, Object params) {
        if (params == null) {
            return new HttpGet(url);
        }
        if (!(params instanceof Map)) {
            throw new RuntimeException("Get method paramaters should be JSONObject or Map.");
        }

        if (!(params instanceof JSONObject)) {
            params = JSON.parseObject(JSON.toJSONString(params));
        }

        JSONObject paramsJSON = (JSONObject) params;
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            if (!paramsJSON.isEmpty()) {
                builder.addParameters(getParams(paramsJSON));
            }
            return new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            throw new RuntimeException("[Page get parameters exception]:" + paramsJSON.toJSONString());
        }
    }

    private HttpRequestBase buildPostMethod(String url,
                                            Object params) {
        HttpPost post = new HttpPost(url);

        if (params == null) {
            return post;
        }

        if (params instanceof Map) {
            params = JSON.parseObject(JSON.toJSONString(params));
        }

        if (params instanceof JSONObject) {
            JSONObject paramsJSON = (JSONObject) params;
            if (!paramsJSON.isEmpty()) {

                UrlEncodedFormEntity paramsEntity;
                try {
                    paramsEntity = new UrlEncodedFormEntity(getParams(paramsJSON), CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("[Page post parameters exception]:" + paramsJSON.toJSONString());
                }
                post.setEntity(paramsEntity);
            }
        } else if (params instanceof String) {
            StringEntity stringEntity;
            try {
                stringEntity = new StringEntity(params.toString(), CHARSET);
            } catch (Exception e) {
                throw new RuntimeException("Build post method exception:" + e.getMessage());
            }
            post.setEntity(stringEntity);
        } else {
            throw new RuntimeException("Unsupported paramaters type.");
        }
        return post;
    }

    private List<NameValuePair> getParams(JSONObject params) {

        List<NameValuePair> list = new ArrayList<>();
        if (params == null
                || params.isEmpty()) {
            return list;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString()));
        }
        return list;
    }

    /**
     * @param url
     * @param params GET方式支持JSONObject类型
     *               POST方式支持JSONObject和String类型
     * @param method
     * @return
     */
    public Response request(String url,
                            Object params,
                            Connection.Method method) {
        return request(url, params, method, null, null);
    }

    /**
     * @param url
     * @param params GET方式支持JSONObject类型
     *               POST方式支持JSONObject和String类型
     * @param method
     * @param filePKCS12 证书
     * @return
     */
    public Response request(String url,
                            Object params,
                            Connection.Method method,
                            File filePKCS12,
                            String password) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_CONNECTION)
                .setConnectionRequestTimeout(TIMEOUT_REQUEST)
                .setSocketTimeout(TIMEOUT_READ_DATA)
                .build();

        Response response = new Response();
        if (StringUtils.isBlank(url)) {
            return response;
        }

        HttpRequestBase httpMethod = getMethod(url, method.name(), params);
        HttpClientContext context = HttpClientContext.create();
        if (!IGNORE_USER_AGENT) {
            httpMethod.addHeader("User-Agent", USER_AGENT);
        }

        if (!HEADERS.isEmpty()) {
            HEADERS.entrySet().forEach(s -> {
                httpMethod.addHeader(s.getKey(), s.getValue());
            });
        }

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig);

        if (filePKCS12 != null) {
            httpClientBuilder.setSSLSocketFactory(buildSSLConnectionSocketFactory(filePKCS12, password));

        }
        HttpClient httpClient = httpClientBuilder.build();

        return requestAndParse(httpClient, httpMethod, context);
    }

    private SSLConnectionSocketFactory buildSSLConnectionSocketFactory(File filePKCS12,
                                                                       String password) {
        if (password == null) {
            password = "";
        }
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(filePKCS12), password.toCharArray());
            SSLContext sslcontext = SSLContexts.custom()
                    // 忽略掉对服务器端证书的校验
                    .loadTrustMaterial((TrustStrategy) (chain, authType) -> true)
                    .loadKeyMaterial(keyStore, password.toCharArray())
                    .build();
            return new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        } catch (Exception e) {
            LoggerUtils.error(e);
        }

        return null;
    }

    private Response requestAndParse(HttpClient httpClient,
                                     HttpRequestBase method,
                                     HttpClientContext context) {
        try {
            if (LOGGER) {
                LoggerUtils.info("Sending request to {}", method.getURI());
            }
            HttpResponse httpResponse = httpClient.execute(method, context);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpHost target = context.getTargetHost();
            List<URI> redirectLocations = context.getRedirectLocations();
            URI location = null;
            try {
                location = URIUtils.resolve(method.getURI(), target, redirectLocations);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String baseUri = location != null ? location.toASCIIString() : "";

            byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
            String html = new String(bytes);

            if (statusCode == HttpStatus.SC_OK
                    && StringUtils.isNotBlank(html)) {
                String charset = getCharset(Jsoup.parse(html));
                html = new String(bytes, charset);
                return new Response(statusCode, baseUri, html);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if (RETRY_TIMES > 0) {
                RETRY_TIMES--;
                if (LOGGER) {
                    LoggerUtils.info("[Page request retry]:{}", method.getURI());
                }
                return requestAndParse(httpClient, method, context);
            }
        }
        return new Response(0, "", null);
    }

    private String getCharset(Document document) {
        boolean deep = false;
        Elements eles = document.select("meta[http-equiv=Content-Type]");

        if (eles.size() == 0) {
            deep = true;
            eles = document.select("meta");
        }
        for (Element element : eles) {
            Matcher m;
            if (!deep) {
                m = PATTERN_CHARSET.matcher(element.attr("content"));
            } else {
                m = PATTERN_CHARSET_DEEP.matcher(element.toString());
            }
            if (m.find()) {
                return m.group(1);
            }
        }


        return CHARSET;
    }

    public static class Response {
        private int statusCode = 0;
        private String baseUri;
        private String result;

        public Response() {
        }

        public Response(int statusCode,
                        String baseUri,
                        String result) {
            this.statusCode = statusCode;
            this.baseUri = baseUri;
            this.result = result;
        }

        /**
         * 返回结果内容转Json
         * 支持的body类型：json 字符串，xml字符串
         * @param extractXMLCDATA 是否提取CDATA值
         * @return
         * @throws XmlUtils.UnsupportedJsonFormatException
         */
        public Object toJson(boolean extractXMLCDATA) {
            if (StringUtils.isBlank(this.result)) {
                return new JSONObject();
            }
            return XmlUtils.xmlToJson(this.result, extractXMLCDATA);
        }

        /**
         * 使用须注意字符串中包含未转义的html内容也会被解析，
         * 可能会导致非预期结果
         * @return
         */
        public Document parse() {
            return Jsoup.parse(this.result == null ? "" : this.result);
        }

        @Override
        public String toString() {
            return this.result;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getBaseUri() {
            return baseUri;
        }

        public void setBaseUri(String baseUri) {
            this.baseUri = baseUri;
        }
    }


}


