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

package win.hupubao.common.exception;

/**
 * @author Moses
 * @date 2017-09-02
 */
public class JedisConfigException extends RuntimeException {
    private static final long serialVersionUID = 7232468717252903957L;

    public JedisConfigException() {
    }

    public JedisConfigException(String message) {
        super(message);
    }
}
