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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author W.feihong.wei
 * @date 2018-07-12
 * <p>
 * xml解析工具
 */
public class XmlUtils {

    private static class El implements Serializable {
        private static final long serialVersionUID = -1016024230871811400L;
        private int level = 0;
        private String text = "";
        private String tagName;
        private List<El> children = new ArrayList<>();

        public El() {
        }

        public El(String tagName) {
            this.tagName = tagName;
        }

        public String html() {
            return getHtml(this);
        }

        private String getHtml(El el) {
            String blank = blank(el.level);
            boolean hasChildren = !el.children.isEmpty();
            StringBuilder sb = new StringBuilder(blank + "<" + el.tagName + ">" + (hasChildren ? "\n" : ""));
            if (hasChildren) {
                for (El child :
                        el.children) {
                    sb.append(getHtml(child));
                }
                sb.append(blank);
                sb.append("</");
                sb.append(el.tagName);
                sb.append(">\n");
                return sb.toString();
            }

            sb.append(el.text);
            sb.append("</");
            sb.append(el.tagName);
            sb.append("> ");
            sb.append("\n");
            return sb.toString();
        }

        private String blank(int level) {
            if (level <= 0) {
                return "";
            }
            return String.format("%-" + level * 4 + "s", "");
        }

        private String appendEnter(String str) {
            if (StringUtils.isBlank(str)) {
                return "\n";
            }
            if (str.endsWith("\n")) {
                return str;
            }
            return str + "\n";
        }


        public El append(String tagName) {
            El child = new El(tagName);
            child.level = level + 1;
            this.children.add(child);
            return child;
        }

        public void text(String text,
                         boolean addXmlCDATA) {
            this.text = addXmlCDATA ? "<![CDATA[" + text + "]]>" : text;
        }
    }


    /**
     * @param extractXMLCDATA 是否提取CDATA值
     * @return
     * @throws UnsupportedJsonFormatException
     */
    public static Object xmlToJson(String xml,
                                 boolean extractXMLCDATA) throws UnsupportedJsonFormatException {
        Object result = null;
        try {
            result = JSON.parse(xml);
        } catch (Exception ignored) {
        }

        if (result == null) {
            Element element = Jsoup.parse(xml).body();
            result = elementToJson(element, extractXMLCDATA);
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

        rootTagName = StringUtils.isBlank(rootTagName) ? "root" : rootTagName;
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
            } else {
                el.text(value == null ? "" : value.toString(), addXMLCDATA);
            }
        }
    }

    /**
     * @param element
     * @return
     */
    private static Object elementToJson(Element element,
                                        boolean extractXMLCDATA) {
        JSONObject json = new JSONObject();
        Elements children = element.children();
        int childSize = children.size();

        if (childSize == 0) {
            return extractXMLCDATA ? element.text() : element.html();
        } else {
            for (Element e : element.children()) {
                json.put(e.tagName(), elementToJson(e, extractXMLCDATA));
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
