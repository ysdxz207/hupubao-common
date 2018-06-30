**金额工具**

单位：元

- 使用方法

```java

import win.hupubao.common.beans.Yuan;

public class TestYuan {
    public static void main(String[] args) {
        Yuan oneApple = new Yuan(2.23);
        Yuan oneOrange = new Yuan(2.15);

        Yuan myFruits = oneApple.multiply(new Yuan(3))
                .add(oneOrange.multiply(new Yuan(5)));
        
        System.out.println("My fruits amount:" + myFruits + "元");

        Yuan yuan = new Yuan(32.44574245);
        System.out.println(yuan);
    }
}

```