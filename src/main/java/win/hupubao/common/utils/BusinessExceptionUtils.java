package win.hupubao.common.utils;

import win.hupubao.common.exception.BusinessException;

/**
 *
 * @author ysdxz207
 * @date 2019-01-09
 */
public class BusinessExceptionUtils {

    /**
     * 递归获取最底层BusinessException
     * @param e
     * @return
     */
    public static BusinessException getBusinessException(Throwable e) {
        if (e == null) {
            return null;
        }

        if (e instanceof BusinessException && e.getCause() == null) {
            return (BusinessException) e;
        }

        return getBusinessException(e.getCause());
    }

}
