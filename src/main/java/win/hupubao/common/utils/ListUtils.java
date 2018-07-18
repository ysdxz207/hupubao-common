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

import java.util.Collection;
import java.util.List;

/**
 * @author W.feihong
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
