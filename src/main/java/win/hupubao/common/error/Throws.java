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

/**
 * 错误异常抛出
 *
 * @author L.feihong
 * @date 2017-03-07
 */
public class Throws {

    public static void throwError(String errorCode, String message) {
        throw new RuntimeException(errorCode, new Exception(message));
    }

    public static void throwError(String message) {
        throwError(ResponseBase.ERROR_CODE_FAIL, message);
    }

    public static void throwError(Error error) {
        throwError(error.getErrorCode(), error.getErrorMsg());
    }
}
