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
import org.apache.commons.lang3.StringUtils;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Moses.wei
 * @date 2018-06-13 17:44:35
 * <p>
 * 通用HTTP请求工具
 */

public class Page {
    private Logger logger = Logger.getLogger(Page.class.getName());

    private static int TIMEOUT_REQUEST = 5000;
    private static int TIMEOUT_CONNECTION = 5000;
    private static int TIMEOUT_READ_DATA = 12000;
    private static int RETRY_TIMES = 0;
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    private static String CHARSET = "UTF-8";

    private static boolean IGNORE_USER_AGENT = false;

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

    public Page loggerOn(Level level) {
        logger.setLevel(level);
        return this;
    }

    public Page loggerOff() {
        logger.setLevel(Level.OFF);
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
        HttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig).build();

        return requestAndParse(httpClient, httpMethod, context);
    }

    private Response requestAndParse(HttpClient httpClient,
                                     HttpRequestBase method,
                                     HttpClientContext context) {
        Response response = new Response();
        Document document = new Document("");
        response.setDocument(document);
        try {
            logger.log(Level.INFO, "Sending request to {0}", method.getURI());
            HttpResponse httpResponse = httpClient.execute(method, context);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            response.setStatusCode(statusCode);
            HttpHost target = context.getTargetHost();
            List<URI> redirectLocations = context.getRedirectLocations();
            URI location = null;
            try {
                location = URIUtils.resolve(method.getURI(), target, redirectLocations);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String baseUri = location != null ? location.toASCIIString() : "";
            document.setBaseUri(baseUri);

            byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
            String html = new String(bytes);

            if (statusCode == HttpStatus.SC_OK
                    && StringUtils.isNotBlank(html)) {
                String charset = getCharset(Jsoup.parse(html));
                html = new String(bytes, charset);
                if (StringUtils.isNotBlank(html)) {
                    Document doc = Jsoup.parse(html);
                    if (doc == null) {
                        return response;
                    }
                    response.setDocument(doc);
                    return response;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if (RETRY_TIMES > 0) {
                RETRY_TIMES--;
                logger.info("[Page request retry]:" + method.getURI());
                return requestAndParse(httpClient, method, context);
            }
        }
        return response;
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
        private Document document;

        public Response() {
            this.document = new Document("");
        }

        public Response(int responseCode,
                        Document document) {
            this.statusCode = responseCode;
            this.document = document == null ? new Document("") : document;
        }


        /**
         * 解析html或xml
         *
         * @param htmlOrXml
         * @return
         */
        public static Response parse(String htmlOrXml) {
            return new Response(HttpStatus.SC_OK, Jsoup.parse(htmlOrXml));
        }

        /**
         * @param extractXMLCDATA 是否提取CDATA值
         * @return
         * @throws UnsupportedJsonFormatException
         */
        public JSONObject bodyToJSONObject(boolean extractXMLCDATA) throws UnsupportedJsonFormatException {
            JSONObject result = null;

            /*
             * body json string to json
             */
            try {
                Element body = document.body();
                if (body != null) {
                    result = JSON.parseObject(body.text());
                }
            } catch (Exception ignored) {
            }

            /*
             * body to json
             */

            if (result == null) {
                Object o = convertToJson(document.body(), extractXMLCDATA);
                if (o instanceof JSONObject) {
                    result = (JSONObject) o;
                }
            }

            if (result == null) {
                throw new UnsupportedJsonFormatException("Body can not convert to json.");
            }
            return result;
        }

        public JSONObject bodyToJSONObject() throws UnsupportedJsonFormatException {
            return bodyToJSONObject(false);
        }

        /**
         * 支持的body类型：json 字符串，xml字符串
         *
         * @param element
         * @return
         */
        private Object convertToJson(Element element,
                                     boolean extractXMLCDATA) {
            JSONObject json = new JSONObject();
            Elements children = element.children();
            int childSize = children.size();

            if (childSize == 0) {
                return extractXMLCDATA ? element.text() : element.html();
            } else {
                for (Element e : element.children()) {
                    json.put(e.tagName(), convertToJson(e, extractXMLCDATA));
                }
                return json;
            }
        }


        @Override
        public String toString() {
            return document.toString();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }
    }

    private static class UnsupportedJsonFormatException extends RuntimeException {

        private static final long serialVersionUID = 2515892027385048310L;

        UnsupportedJsonFormatException(String message) {
            super(message);
        }
    }
}


