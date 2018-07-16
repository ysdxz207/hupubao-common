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

public enum SystemError implements Error {
    SIGN_ERROR("SIGN_ERROR", "Sign error."),
    PARAMETER_ERROR("PARAMETER_ERROR", "Parameter error."),
    SYSTEM_ERROR("SYSTEM_ERROR", "System internal error."),
    SYSTEM_BISINESS_ERROR("SYSTEM_BUSINESS_ERROR", "System business error."),
    OPERATION_FREQUENTLY_ERROR("OPERATION_FREQUENTLY_ERROR", "The system is busy now,please try it later.");

    SystemError(String error_code, String error_msg) {
        this.error_code = error_code;
        this.error_msg = error_msg;
    }

    @Override
    public String getErrorCode() {
        return this.error_code;
    }

    @Override
    public String getErrorMsg() {
        return this.error_msg;
    }
    public String error_code;
    public String error_msg;
}