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
