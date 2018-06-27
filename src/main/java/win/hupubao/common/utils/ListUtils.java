package win.hupubao.common.utils;

import java.util.Collection;
import java.util.List;

/**
 * @author Moses
 * @date 2017-08-30
 */
public class ListUtils {

    /**
     * list包含collection中任意一个
     * @param list
     * @param collection
     * @return
     */
    public static boolean containsAny(List<?> list,
                                      Collection<?> collection){
        if (list == null
                || list.isEmpty()) {
            return false;
        }

        if (!collection.getClass().equals(list.get(0).getClass())){
            return false;
        }

        for (Object object :
                list) {
            if (collection.contains(object)) {
                return true;
            }
        }
        return false;
    }
}
