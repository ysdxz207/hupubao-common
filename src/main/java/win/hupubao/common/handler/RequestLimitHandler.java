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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import win.hupubao.common.annotations.RequestLimit;

/**
 *
 * @author W.feihong
 * @date 2018-08-06
 * 访问频率检查
 */
@Aspect
public class RequestLimitHandler {

    /**
     * 需要有构造方法
     * 否则会报Caused by: java.lang.NoSuchMethodError xxx  method <init>()V not found
     */
    public RequestLimitHandler() {
    }

    @Before("execution(* *(..)) && @annotation(requestLimit)")
    public void before(JoinPoint joinPoint,
                         RequestLimit requestLimit) {
        long interval = requestLimit.interval();
        if (interval > 0) {
            

        }
    }

}