**定时器工具**

- 使用方法

```java

import win.hupubao.common.scheduler.Scheduler;

public class TestScheduler {

    @org.junit.Test
    public void test() {
        Scheduler.runTimer("timer",
                4000L, 1000L, () -> {
                    System.out.println("heihei");
                });

        Scheduler.runOnce("runOnce",
                2000L, () -> {
                    System.out.println("once");
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Scheduler.cancel("timer");
        System.out.println("\"timer\" has been canceled");
    }

}



```
