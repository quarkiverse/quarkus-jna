package io.quarkiverse.jna.runtime.graal;

import java.lang.reflect.Method;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "com.sun.jna.internal.ReflectionUtils")
final class Target_com_sun_jna_internal_ReflectionUtils {

    @Substitute()
    public static Object getMethodHandle(Method method) throws Exception {
        return method;
    }

    @Substitute()
    public static Object invokeDefaultMethod(Object target, Object methodHandle, Object... args) throws Throwable {
        return ((Method) methodHandle).invoke(target, args);
    }
}

public class JnaSubstitutions {

}
