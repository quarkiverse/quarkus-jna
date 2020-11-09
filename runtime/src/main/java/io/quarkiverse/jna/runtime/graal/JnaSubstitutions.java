package io.quarkiverse.jna.runtime.graal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "com.sun.jna.ELFAnalyser")
final class Target_com_sun_jna_ELFAnalyser {
    @Alias
    public static Target_com_sun_jna_ELFAnalyser analyse(String filename) throws IOException {
        return null;
    }

    @Alias
    public boolean isArmHardFloat() {
        return true;
    }
}

@TargetClass(className = "com.sun.jna.Platform")
final class Target_com_sun_jna_Platform {

    //@Alias
    //private static Logger LOG = null;

    @Substitute()
    static boolean isSoftFloat() {
        try {
            File self = new File("/proc/self/exe");
            if (self.exists()) {
                Target_com_sun_jna_ELFAnalyser ahfd = Target_com_sun_jna_ELFAnalyser.analyse(self.getCanonicalPath());
                return !ahfd.isArmHardFloat();
            }
        } catch (IOException ex) {
            // comment logger to avoid static init of thread
            // asume hardfloat
            //Logger.getLogger(Platform.class.getName()).log(Level.INFO, "Failed to read '/proc/self/exe' or the target binary.", ex);
        } catch (SecurityException ex) {
            // comment logger to avoid static init of thread
            // asume hardfloat
            //Logger.getLogger(Platform.class.getName()).log(Level.INFO, "SecurityException while analysing '/proc/self/exe' or the target binary.", ex);
        }
        return false;
    }
}

@TargetClass(className = "com.sun.jna.internal.ReflectionUtils")
final class Target_com_sun_jna_internal_ReflectionUtils {

    //@Alias
    //private static Logger LOG = null;

    @Substitute()
    public static Object getMethodHandle(Method method) throws Exception {
        return method;
    }

    @Substitute()
    public static Object invokeDefaultMethod(Object target, Object methodHandle, Object... args) throws Throwable {
        return ((Method) methodHandle).invoke(target, args);
    }

    @Substitute()
    private static Class lookupClass(String name) {
        if (name == "java.lang.invoke.MethodHandle"
                || name == "java.lang.invoke.MethodHandles"
                || name == "java.lang.invoke.MethodHandles$Lookup"
                || name == "java.lang.invoke.MethodType")
            return null;

        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    @Substitute
    private static Method lookupMethod(Class clazz, String methodName, Class... arguments) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getMethod(methodName, arguments);
        } catch (Exception ex) {
            return null;
        }
    }
}

public class JnaSubstitutions {

}
