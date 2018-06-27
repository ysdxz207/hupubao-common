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
