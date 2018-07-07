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

public class DateUtils {


    /**
     * 获取最近n月的第一天日期，n可为负数
     * @param months
     * @return
     */
    public static Date getMonthFirstOrLastDay(int months,
                                              boolean isLastDay,
                                              boolean isZeroClock) {

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);

        if (isLastDay) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }

        if (isZeroClock) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        return calendar.getTime();
    }

    /**
     * 获取当天零点日期
     *
     * @param date
     * @return
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
