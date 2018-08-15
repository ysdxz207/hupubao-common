package win.hupubao.common.aop.adaper;

import javax.servlet.http.HttpServletRequest;

public class RequestLimitAdapter {

    public String getUniqueKey(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
