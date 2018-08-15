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

import win.hupubao.common.aop.adaper.RequestLimitAdapter;
import win.hupubao.common.aop.adaper.RequestLimitHandler;

import java.lang.annotation.*;

/**
 *
 * @author W.feihong
 * @date 2018-08-06
 * 请求间隔控制
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestLimit {
    /**
     * 请求间隔,-1或0不限制
     * @return
     */
    public long interval() default -1;

    /**
     * 请求控制处理器
     * @return
     */
    public Class<? extends RequestLimitHandler> handler() default RequestLimitHandler.class;

    /**
     * 请求控制适配器，用户获取用户唯一标识
     * @return
     */
    public Class<? extends RequestLimitAdapter> adapter() default RequestLimitAdapter.class;

    /**
     * 每次请求后是否重新限制
     * @return
     */
    public boolean updated() default false;
}
