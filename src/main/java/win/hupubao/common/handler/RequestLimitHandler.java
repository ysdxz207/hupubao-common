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

package win.hupubao.common.handler;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import win.hupubao.common.annotations.RequestLimit;
import win.hupubao.common.handler.adaper.RequestLimitAdapter;
import win.hupubao.common.utils.IPUtils;
import win.hupubao.common.utils.LoggerUtils;
import win.hupubao.common.utils.MacUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author W.feihong
 * @date 2018-08-06
 * 访问频率检查
 */
@Aspect
public class RequestLimitHandler {
    private static final ExpiringMap<String, Long> REQUEST_MAP = ExpiringMap.builder()
            .variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    /**
     * 需要有构造方法
     * 否则会报Caused by: java.lang.NoSuchMethodError xxx  method &lt;init&gt;()V not found
     */
    public RequestLimitHandler() {
    }

    @Around("execution(* *(..)) && @annotation(requestLimit)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint,
                         RequestLimit requestLimit) throws Throwable {
        long limitInterval = requestLimit.interval();
        if (limitInterval > 0) {
            Object[] args = proceedingJoinPoint.getArgs();

            HttpServletRequest request = null;
            for (Object obj : args) {
                if (obj instanceof HttpServletRequest) {
                    request = (HttpServletRequest) obj;
                }
            }
            if (request == null) {
                LoggerUtils.warn(getClass(), "Method with annotation [RequestLimit] should have HttpServletRequest type parameter.");
                return proceedingJoinPoint.proceed();
            }

            String key = request.getSession().getId();

            long currentTime = System.currentTimeMillis();
            boolean limit = REQUEST_MAP.containsKey(key);
            long lastRequestTime = limit ? REQUEST_MAP.get(key) : 0;
            long currentInterval = currentTime - lastRequestTime;


            if (limit) {
                long limitTimeLast = limitInterval - currentInterval;
                if (requestLimit.updated()) {
                    REQUEST_MAP.put(key, currentTime, limitInterval, TimeUnit.MILLISECONDS);
                    limitTimeLast = limitInterval;
                }

                Class<? extends RequestLimitAdapter> clazz = requestLimit.adapter();
                try {

                    RequestLimitAdapter requestLimitAdapter = clazz.newInstance();
                    return requestLimitAdapter.handle(limitInterval, requestLimit.updated(), limitTimeLast, args);
                } catch (InstantiationException e) {
                    LoggerUtils.warn("Can not execute request limit handler [{}].", clazz.getName());
                }

            }
            REQUEST_MAP.put(key, currentTime, limitInterval, TimeUnit.MILLISECONDS);

        }
        return proceedingJoinPoint.proceed();
    }

}