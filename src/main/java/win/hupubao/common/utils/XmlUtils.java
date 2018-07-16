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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.Map;

/**
 * @author L.feihong.wei
 * @date 2018-07-12
 * <p>
 * xml解析工具
 */
public class XmlUtils {

    private static class El implements Serializable {
        private static final long serialVersionUID = -1016024230871811400L;
        private int level = 0;
        private String html;
        private String text = "";
        private String tagName;
        private El parent;
        private El child;

        public El() {
        }

        public El(String tagName) {
            this.tagName = tagName;
            this.html = getHtml(this);
        }

        public String html() {
            return getHtml(this);
        }

        private String getHtml(El el) {
            String blank = blank(el.level);
            StringBuilder sb = new StringBuilder(blank + "<" + el.tagName + ">");
            if (el.child != null) {
                sb.append("\n");
                sb.append(blank);
                sb.append(getHtml(el.child));
                sb.append(blank);
                sb.append("</" + el.tagName + ">\n");
                return sb.toString();
            }

            sb.append(el.text);
            sb.append("</");
            sb.append(el.tagName);
            sb.append(">\n");
            return sb.toString();
        }

        private String blank(int level) {
            if (level <= 0) {
                return "";
            }
            return String.format("%-" + level * 4 + "s", "");
        }



        public El append(String tagName) {
            this.child = new El(tagName);
            this.html = getHtml(this);
            this.child.parent = this;
            ++ this.child.level;
            return this.child;
        }

        public void text(String text,
                         boolean addXmlCDATA) {
            this.text = addXmlCDATA ? "<![CDATA[" + text + "]]>" : text;
            this.html = getHtml(this);
            this.parent.html = getHtml(this.parent);
        }
    }


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

        El root = new El(rootTagName);
        convertJsonToElement(root, JSON.parseObject(json), addXMLCDATA);
        return root.html();
    }

    private static void convertJsonToElement(El parentEl,
                                             JSONObject json,
                                             boolean addXMLCDATA) {
        if (json == null) {
            return;
        }
        for (Map.Entry entry : json.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            El el = parentEl.append((String) key);
            if (value instanceof JSONObject) {
                convertJsonToElement(el, (JSONObject) value, addXMLCDATA);
            } else if (value instanceof String) {
                el.text((String) value, addXMLCDATA);
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

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        JSONObject json2 = new JSONObject();

        json2.put("b-1", "b-1content");

        json.put("aa", "vv");
        json.put("b", json2);
        String str = jsonToXml("root", json.toJSONString(), true);

        System.out.println(str);
    }

}
