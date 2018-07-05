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

package win.hupubao.common.annotations;

import java.lang.annotation.*;

/**
 *
 * @author Moses
 * @date 2018-06-08 17:48:33
 * 是否记录请求及返回参数日志功能注解
 * 有此注解则记录日志
 * 使用条件：有HttpServletRequest和HttpServletResponse参数，且有返回值
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogRequestResponseArgs {
    /**
     * 日志头，默认值为RequestMapping的路径
     * @return
     */
    public String value() default "";
    public boolean logException() default true;
}
