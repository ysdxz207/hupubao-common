package win.hupubao.common.utils;

/**
 * @author Moses
 * @date 2017-08-24
 */
public class Assert {
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
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
