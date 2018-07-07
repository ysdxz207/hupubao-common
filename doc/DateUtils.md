**日期工具**

- 因为apache已经有了格式化、解析、获取最近天数的时间等工具，这里就不再重复造轮子了。
只提供了 **获取某时间的零点时间** 及 **获取某月第一天或最后一天时间(可选零点)** 工具

- 使用方法

```java

import org.apache.commons.lang3.time.DateFormatUtils;
import win.hupubao.common.utils.DateUtils;

import java.util.Date;

public class TestDateUtils {

    private static final String FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {

        //获取今天零点日期
        Date todayZeroClock = DateUtils.getZeroClockByDate(new Date());
        System.out.println(DateFormatUtils.format(todayZeroClock,
                FORMAT_PATTERN));

        //获取这月第一天零点日期
        System.out.println(DateFormatUtils.format(DateUtils.getMonthFirstOrLastDay(0,
                false,
                true),
                FORMAT_PATTERN));
        
    }
}

```
