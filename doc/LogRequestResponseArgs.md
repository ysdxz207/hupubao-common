**自动记录请求，返回参数日志工具**

- 使用方法

1.需要加入maven插件：aspectj-maven-plugin

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.10</version>
    <configuration>
        <encoding>UTF-8</encoding>
        <complianceLevel>1.8</complianceLevel>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>win.hupubao</groupId>
                <artifactId>hupubao-common</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </execution>
    </executions>
</plugin>

```

2.使用

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LogRequestResponseArgs("书架接口")
public static void bookShelf(HttpServletRequest request,
                             HttpServletResponse response) {

    //Your code.
}
```