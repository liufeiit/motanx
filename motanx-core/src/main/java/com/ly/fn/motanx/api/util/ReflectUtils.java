package com.ly.fn.motanx.api.util;

import java.util.Arrays;
import java.util.Set;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月13日 上午10:42:25
 */
public class ReflectUtils {

    public static void findInterfaces(Class<?> clazz, Set<Class<?>> interfaces) {
        Class<?>[] bootInterfaces = clazz.getInterfaces();
        if (bootInterfaces == null || bootInterfaces.length <= 0) {
            return;
        }
        interfaces.addAll(Arrays.asList(bootInterfaces));
        for (Class<?> bootInterface : bootInterfaces) {
            findInterfaces(bootInterface, interfaces);
        }
    }
}