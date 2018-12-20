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

package win.hupubao.common.error;

import win.hupubao.common.beans.ResponseBase;
import win.hupubao.common.exception.BusinessException;
import win.hupubao.common.utils.StringUtils;

/**
 * 错误异常抛出
 *
 * @author ysdxz207
 * @date 2017-03-07
 */
public class Throws {

    public static void throwError(String code, String message) {
        throw new BusinessException(code, message);
    }

    public static void throwError(String message) {
        throwError(ResponseBase.RESPONSE_CODE_FAIL, message);
    }

    public static void throwError(ErrorInfo error) {
        throwError(error.getErrorCode(), error.getErrorMsg());
    }

    public static void throwError(ErrorInfo error, String message) {
        throwError(error.getErrorCode(), StringUtils.isNotBlank(message) ? message : error.getErrorMsg());
    }
}
