package win.hupubao.common.exception;

/**
 * @author Moses
 * @date 2017-09-02
 */
public class JedisConfigException extends RuntimeException {
    public JedisConfigException() {
    }

    public JedisConfigException(String message) {
        super(message);
    }
}
