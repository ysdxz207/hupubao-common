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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import win.hupubao.common.annotations.LogReqResArgs;
import win.hupubao.common.utils.LoggerUtils;
import win.hupubao.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author ysdxz207
 * @date 2018-06-11 14:05:53
 * 请求，返回参数日志记录工具
 *
 */
@Aspect
public class LogReqResArgsAspect {
    /**
     * 需要有构造方法
     * 否则会报Caused by: java.lang.NoSuchMethodError xxx  method <init>()V not found
     */
    public LogReqResArgsAspect() {
    }


    /**
     * 请求日志记录
     * @param joinPoint
     * @param logReqResArgs
     */
    @Before("execution(* *(..)) && @annotation(logReqResArgs)")
    public void before(JoinPoint joinPoint,
                       LogReqResArgs logReqResArgs) {

        HttpServletRequest request = getHttpServletRequest(joinPoint);
        if (request != null) {
            doLog(joinPoint, logReqResArgs, request);
        } else {
            LoggerUtils.warn(getClass(), "Method [" + getMethod(joinPoint).getName() + "] with annotation [LogReqResArgs] should have HttpServletRequest type parameters.");
        }
    }

    /**
     * 响应日志记录
     * @param joinPoint
     * @param logReqResArgs
     */
    @AfterReturning(pointcut = "execution(* *(..)) && @annotation(logReqResArgs)", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint,
                               LogReqResArgs logReqResArgs,
                               Object returnValue) {
        doLog(joinPoint, logReqResArgs, returnValue);
    }

    /**
     * 异常日志记录
     * @param joinPoint
     * @param logReqResArgs
     * @param throwable
     */
    @AfterThrowing(pointcut = "execution(* *(..)) && @annotation(logReqResArgs)", throwing = "throwable")
    public void afterThrowing(JoinPoint joinPoint,
                              LogReqResArgs logReqResArgs,
                              Throwable throwable) {
        if (logReqResArgs.logException()) {
            doLog(joinPoint, logReqResArgs, throwable);
        }
    }

    /**
     * 公共记录日志方法
     * @param joinPoint
     * @param logReqResArgs
     * @param info
     */
    private void doLog(JoinPoint joinPoint,
                       LogReqResArgs logReqResArgs,
                       Object info) {
        Method targetMethod = getMethod(joinPoint);
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(StringUtils.isNotBlank(logReqResArgs.title()) ? logReqResArgs.title() : getDefaultTitle(targetMethod));
        sb.append("]");
        if (info instanceof Throwable) {
            sb.append(logReqResArgs.titleException());
            LoggerUtils.info(targetMethod.getDeclaringClass(), sb.toString(), (Throwable) info);
            return;
        }
        if (info instanceof HttpServletRequest){
            sb.append(logReqResArgs.titleRequest());
            sb.append(JSON.toJSONString(getRequestArgs((HttpServletRequest) info)));
        } else {
            if (logReqResArgs.logResponseWithRequest()) {
                HttpServletRequest request = getHttpServletRequest(joinPoint);
                if (request != null) {
                    sb.append(logReqResArgs.titleRequest());
                    sb.append(JSON.toJSONString(getRequestArgs(request)));
                    sb.append("\n");
                }
            }
            sb.append(logReqResArgs.titleResponse());
            sb.append(JSON.toJSONString(info));
        }
        LoggerUtils.info(targetMethod.getDeclaringClass(), sb.toString());
    }


    /**
     * 获取默认日志头
     * @param targetMethod
     * @return
     */
    private String getDefaultTitle(Method targetMethod) {

        Annotation [] annotations = targetMethod.getAnnotations();

        for (Annotation annotation : annotations) {
            if ("org.springframework.web.bind.annotation.RequestMapping".equals(annotation.annotationType().getName())) {
                try {
                    return (String) annotation.annotationType().getMethod("value").invoke(annotation);
                } catch (Exception ignored) {
                }
            }
        }

        return targetMethod.getName();
    }

    /**
     * 获取注解所在方法
     * @param joinPoint
     * @return
     */
    private Method getMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        return methodSignature.getMethod();
    }


    /**
     * 获取参数JSON字符串
     * 不支持raw类型
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    private JSONObject getRequestArgs(HttpServletRequest request) {
        try {
            JSONObject jsonArgs = new JSONObject();
            Enumeration<String> args = request.getParameterNames();

            while (args.hasMoreElements()) {
                String key = args.nextElement();
                jsonArgs.put(key, request.getParameter(key));
            }

            return jsonArgs;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取HttpServletRequest
     * @param joinPoint
     * @return
     */
    private HttpServletRequest getHttpServletRequest(JoinPoint joinPoint) {
        Optional<Object> optionalHttpServletRequest = Arrays.stream(joinPoint.getArgs()).filter(o -> o instanceof HttpServletRequest).findFirst();
        if (optionalHttpServletRequest.isPresent()) {
            return (HttpServletRequest) optionalHttpServletRequest.get();
        }
        LoggerUtils.warn(getClass(), "Method [" + getMethod(joinPoint).getName() + "] with annotation [LogReqResArgs] should have HttpServletRequest type parameters.");
        return null;
    }

    class LogReqResArgsInvalidParameterException extends RuntimeException {

        private static final long serialVersionUID = -1595991881883221808L;

        public LogReqResArgsInvalidParameterException() {
        }

        public LogReqResArgsInvalidParameterException(String message) {
            super(message);
        }
    }
}