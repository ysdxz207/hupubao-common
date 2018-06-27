package win.hupubao.common.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 获取最近n天日期，n可为负数
     * @param days
     * @return
     */
    public static Date getNowDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24 * days);
        return calendar.getTime();
    }

    /**
     * 获取当天零点日期毫秒
     * @return
     */
    public static Long getTodayZeroMiliseconds() {
        Long daySeconds = 60 * 60 * 1000L;
        Long nowSeconds = System.currentTimeMillis();
        return nowSeconds - nowSeconds % daySeconds;
    }

}
