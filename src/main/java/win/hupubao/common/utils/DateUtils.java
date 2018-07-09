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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Moses
 * @date 2018-07-09
 */
public class DateUtils {

    public enum MonthDay {
        FIRST_DAY,//第一天
        LAST_DAY,//最后一天
        NOW//现在的时间
    }

    /**
     * 获取最近months月的某天
     * @param months 最近月数，可为负数
     * @param monthDay 某天，参考:win.hupubao.common.utils.DateUtils.MonthDay
     * @return 最近月数的某天
     */
    public static Date getMonthDay(int months,
                                   MonthDay monthDay) {

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);

        switch (monthDay) {
            case NOW:
                break;
            case FIRST_DAY:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case LAST_DAY:
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
        }
        return calendar.getTime();
    }

    /**
     * 获取某日期的零点日期
     * @param date 日期参数
     * @return 零点日期
     */
    public static Date getZeroClockByDate(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

}
