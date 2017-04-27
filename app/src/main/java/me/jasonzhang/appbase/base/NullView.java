package me.jasonzhang.appbase.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Collections.unmodifiableMap;

/**
 * Author: Jifeng Zhang
 * Email : jifengzhang.barlow@gmail.com
 * Date  : 2017/4/27
 * Desc  :空View，用于Presenter中getView为空时
 */

public class NullView {
    private static final InvocationHandler DEFAULT_VALUE = new DefaultValueInvocationHandler();

    private NullView() {
    }

    @SuppressWarnings("unchecked")
    static <T> T of(Class<T> interfaceClass) {
        return (T) newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, DEFAULT_VALUE);
    }

    private static class DefaultValueInvocationHandler implements InvocationHandler {
        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return NullViewDefaults.defaultValue(method.getReturnType());
        }
    }

    private static class NullViewDefaults {
        static final Map<Class<?>, Object> DEFAULTS = unmodifiableMap(new HashMap<Class<?>, Object>() {
            {
                put(Boolean.TYPE, false);
                put(Byte.TYPE, (byte) 0);
                put(Character.TYPE, '\000');
                put(Double.TYPE, 0.0d);
                put(Float.TYPE, 0.0f);
                put(Integer.TYPE, 0);
                put(Long.TYPE, 0L);
                put(Short.TYPE, (short) 0);
            }
        });

        private NullViewDefaults() {
        }

        @SuppressWarnings("unchecked")
        private static <T> T defaultValue(Class<T> type) {
            return (T) DEFAULTS.get(type);
        }
    }
}
