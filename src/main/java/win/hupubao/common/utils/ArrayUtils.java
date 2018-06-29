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

import java.util.stream.Stream;

public class ArrayUtils {
	
	/**
	 * 将英文逗号分隔的字符串转为Long类型数组;1,2,3,4=>[1,2,3,4]
	 * @param str
	 * @return
	 */
	public static long[] parseToLongArray(String str) {
		if (StringUtils.isBlank(str)) {
			return new long[0];
		}
		return Stream.of(str.split(",")).mapToLong(Long::valueOf).toArray();
	}

}
