**Http工具**
- 起因：爬小说数据时发现有些页面jsoup不能爬到小说内容，而HttpClient可以，又不想用太大的爬虫框架，于是自己动手把HttpC封装了一下

- 使用方法

```java

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import win.hupubao.common.http.Page;

public class TestPage {
    public static void main(String[] args) {
        JSONObject paramsJson = new JSONObject();//json参数，当然也可以用Map
        paramsJson.put("q", "太阳");

        String paramsXml = "<xml><id>123456</id><name>大帅比</name></xml>";//raw参数，post方式
        Page.Response responseGet = Page.create()
                .connectionTimeout(3000)
                .retryTimes(3)
                .request("https://www.google.com/search",
                        paramsJson,
                        Connection.Method.GET);

        System.out.println(responseGet);

        Page.Response responsePost = Page.create()
                .connectionTimeout(3000)
                .retryTimes(3)
                .request("https://www.google.com/search",
                        paramsXml,
                        Connection.Method.POST);

        System.out.println(responsePost);
    }
}

```

- 重要

最近爆发了微信xxe攻击问题，本工具因为使用了Jsoup解析xml，并无此问题。
原xml：
```xml
<?xml version="1.0" encoding="utf-8"?>
        <!DOCTYPE xdsec [
                <!ELEMENT methodname ANY >
                <!ENTITY xxe SYSTEM "file:///C:/Users/Administrator/Desktop/aa.txt" >]>
<test>
    <content>&xxe;</content>
</test>

```
Jsoup会把xml解析成如下：
```xml
<!--?xml version="1.0" encoding="utf-8"?--><!doctype xdsec>
<!--ENTITY xxe SYSTEM "file:///C:/Users/Administrator/Desktop/aa.txt" -->
<html>
 <head></head>
 <body>
  ]&gt; 
  <test> 
   <content>
    &amp;xxe;
   </content> 
  </test>
 </body>
</html>
```
