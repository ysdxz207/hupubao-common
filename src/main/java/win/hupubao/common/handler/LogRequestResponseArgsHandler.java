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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import win.hupubao.common.annotations.LogRequestResponseArgs;
import win.hupubao.common.utils.LoggerUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 *
 * @author Moses
 * @date 2018-06-11 14:05:53
 * 请求，返回参数日志记录工具
 *
 */
@Aspect
public class LogRequestResponseArgsHandler {

    public LogRequestResponseArgsHandler() {
    }


    /**
     * 请求日志记录
     * @param joinPoint
     * @param logRequestResponseArgs
     */
    @Before("@annotation(logRequestResponseArgs)")
    public void before(JoinPoint joinPoint,
                       LogRequestResponseArgs logRequestResponseArgs) {
        try {
            doLog(joinPoint, logRequestResponseArgs, "receive", null);
        } catch (Exception e) {
            LoggerUtils.error("[LogRequestResponseArgsHandler before]记录日志异常：", e);
        }
    }

    /**
     * 响应日志记录
     * @param joinPoint
     * @param logRequestResponseArgs
     */
    @AfterReturning(pointcut = "@annotation(logRequestResponseArgs)", returning = "returnValue")
    public void AfterReturning(JoinPoint joinPoint,
                               LogRequestResponseArgs logRequestResponseArgs,
                               Object returnValue) {
        try {
            doLog(joinPoint, logRequestResponseArgs, "feedback", returnValue);
        } catch (Exception e) {
            LoggerUtils.error("[LogRequestResponseArgsHandler after]记录日志异常：", e);
        }
    }

    /**
     * 公共记录日志方法
     * @param joinPoint
     * @param logRequestResponseArgs
     * @param tag
     */
    private void doLog(JoinPoint joinPoint,
                       LogRequestResponseArgs logRequestResponseArgs,
                       String tag,
                       Object returnValue) {
        Method targetMethod = getMethod(joinPoint);
        StringBuilder sb = new StringBuilder();
        String title = logRequestResponseArgs.value();

        if (StringUtils.isNotBlank(title)) {
            sb.append(title);
        } else {
            sb.append(getDefaultTitle(targetMethod));
        }

        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = null;
        HttpServletResponse response = null;
        for (Object obj : args) {
            if (obj instanceof HttpServletRequest) {
                request = (HttpServletRequest) obj;
            }
            if (obj instanceof HttpServletResponse) {
                response = (HttpServletResponse) obj;
            }
        }

        if (request == null
                || response == null) {
            throw new RuntimeException("使用[LogRequestResponseArgs]注解的方法必须有[HttpServletRequest和HttpServletResponse]参数:" + targetMethod.getName());
        }


        sb.append("[");
        sb.append(tag);
        sb.append("]");
        sb.append(":");
        sb.append("receive".equalsIgnoreCase(tag) ? getArgsString(request) : JSON.toJSONString(returnValue));

        LoggerUtils.info(joinPoint.getTarget().getClass(), sb.toString());
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
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    private String getArgsString(HttpServletRequest request) {
        try {
            JSONObject jsonArgs = new JSONObject();
            Enumeration<String> args = request.getParameterNames();

            while (args.hasMoreElements()) {
                String key = args.nextElement();
                jsonArgs.put(key, request.getParameter(key));
            }

            return jsonArgs.toJSONString();
        } catch (Exception e) {
            return "";
        }
    }

}