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

package win.hupubao.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * @author L.feihong.wei
 * @date 2018-07-12
 * <p>
 * xml解析工具
 */
public class XmlUtils {


    /**
     * @param extractXMLCDATA 是否提取CDATA值
     * @return
     * @throws UnsupportedJsonFormatException
     */
    public static JSON xmlToJson(String xml,
                                 boolean extractXMLCDATA) throws UnsupportedJsonFormatException {
        JSON result = null;
        try {
            result = JSON.parseObject(xml);
        } catch (Exception e) {
            try {
                result = JSON.parseArray(xml);
            } catch (Exception ignored) {
            }
        }

        if (result == null) {
            Element element = Jsoup.parse(xml).body();
            Object o = convertToJson(element, extractXMLCDATA);
            if (o instanceof JSONObject) {
                result = (JSONObject) o;
            }
        }

        if (result == null) {
            throw new UnsupportedJsonFormatException("Can not convert to json.");
        }
        return result;
    }

    /**
     * Json转xml
     *
     * @param rootTagName
     * @param json        Json对象字符串
     * @return
     */
    public static String jsonToXml(String rootTagName,
                                   String json,
                                   boolean addXMLCDATA) {
        if (StringUtils.isEmpty(json)) {
            return "";
        }

        Document document = new Document("");
        Element element = document.appendElement(rootTagName);
        convertJsonToElement(element, JSON.parseObject(json), addXMLCDATA);
        return document.html();
    }

    private static void convertJsonToElement(Element parentElement,
                                             JSONObject json,
                                             boolean addXMLCDATA) {
        if (json == null) {
            return;
        }
        for (Map.Entry entry : json.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Element element = parentElement.appendElement((String) key);
            if (value instanceof JSONObject) {
                convertJsonToElement(element, (JSONObject) value, addXMLCDATA);
            } else if (value instanceof String) {
                if (addXMLCDATA) {
                    element.text("<![CDATA[" + value + "]]");
                } else {
                    element.text((String) value);
                }
            }
        }
    }

    /**
     * @param element
     * @return
     */
    private static Object convertToJson(Element element,
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

    private static class UnsupportedJsonFormatException extends RuntimeException {

        private static final long serialVersionUID = 2515892027385048310L;

        UnsupportedJsonFormatException(String message) {
            super(message);
        }
    }

}
