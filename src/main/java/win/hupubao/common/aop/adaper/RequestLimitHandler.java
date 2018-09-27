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


package win.hupubao.common.aop.adaper;

import win.hupubao.common.error.SystemError;
import win.hupubao.common.error.Throws;

public class RequestLimitHandler {
    public Object handle(long interval,
                         boolean updated,
                         long limitTimeLast,
                         Object ... args) {
        Throws.throwError(SystemError.REQUEST_FREQUENTLY_ERROR);
        return null;
    }
}