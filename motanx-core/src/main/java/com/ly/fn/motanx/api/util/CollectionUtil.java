package com.ly.fn.motanx.api.util;

import java.util.Collection;

public class CollectionUtil {

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

}
