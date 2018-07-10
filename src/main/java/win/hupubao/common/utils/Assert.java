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

package win.hupubao.common.utils;

/**
 * @author L.feihong
 * @date 2017-08-24
 */
public class Assert {
    public Assert() {
    }

    public static void check(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void check(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(message, args));
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(CharSequence s, String name) {
        if (StringUtils.isEmpty(s)) {
            throw new IllegalStateException(name + " is empty");
        }
    }

    public static void notBlank(CharSequence s, String name) {
        if (StringUtils.isBlank(s)) {
            throw new IllegalStateException(name + " is blank");
        }
    }

    public static void hasText(Object object, String message) {
        if (StringUtils.isBlank(object)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void equals(Object obj1, Object obj2, String message) {
        if (!obj1.equals(obj2)) {
            throw new IllegalArgumentException(message);
        }
    }

}
