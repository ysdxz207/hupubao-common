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

package win.hupubao.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import win.hupubao.common.utils.ExceptionEmailSender;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ysdxz207
 * @date 2018-07-10
 * 异常拦截并发送邮件
 */
@Aspect
public class ExceptionAspect {
    private ExceptionEmailSender exceptionEmailSender;
    private List<String> exceptionClassList = new ArrayList<>();

    /**
     * 需要有构造方法
     * 否则会报Caused by: java.lang.NoSuchMethodError xxx  method <init>()V not found
     */
    public ExceptionAspect() {
    }

    public ExceptionAspect(ExceptionEmailSender exceptionEmailSender,
                           List<String> exceptionClassList) {
        this.exceptionEmailSender = exceptionEmailSender;
        this.exceptionClassList = exceptionClassList;
    }



    /**
     * 异常日志记录
     * @param joinPoint
     * @param throwable
     */
    @AfterThrowing(pointcut = "execution(* *(..)) && !execution(* win.hupubao.common..*(..))", throwing = "throwable")
    public void afterThrowing(JoinPoint joinPoint,
                              Throwable throwable) {
        String exceptionClassName = throwable.getClass().getName();
        if (exceptionClassList.contains(exceptionClassName)) {
            exceptionEmailSender.sendException(throwable);
        }
    }

}