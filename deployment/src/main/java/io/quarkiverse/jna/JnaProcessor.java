package io.quarkiverse.jna;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

public class JnaProcessor {

    private static final Logger LOGGER = Logger.getLogger(JnaProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("jna");
    }

    void produceRecursiveProxies(IndexView index,
            DotName interfaceDN,
            BuildProducer<NativeImageProxyDefinitionBuildItem> proxies, Set<String> proxiesCreated) {
        index.getKnownDirectImplementors(interfaceDN).stream()
                .filter(classinfo -> Modifier.isInterface(classinfo.flags()))
                .map(ClassInfo::name)
                .forEach((className) -> {
                    if (!proxiesCreated.contains(className.toString())) {
                        proxies.produce(new NativeImageProxyDefinitionBuildItem(className.toString()));
                        produceRecursiveProxies(index, className, proxies, proxiesCreated);
                        LOGGER.warn("add proxy:" + className);
                        proxiesCreated.add(className.toString());
                    }
                });

    }

    @BuildStep
    void addProxies(CombinedIndexBuildItem combinedIndexBuildItem,
            BuildProducer<NativeImageProxyDefinitionBuildItem> proxies) {
        IndexView index = combinedIndexBuildItem.getIndex();
        Set<String> proxiesCreated = new HashSet<>();
        // getAllKnownDirectImplementors skip interface, so I have to do it myself.
        produceRecursiveProxies(index, DotName.createSimple("com.sun.jna.Library"), proxies, proxiesCreated);
        produceRecursiveProxies(index, DotName.createSimple("com.sun.jna.Callback"), proxies, proxiesCreated);
        proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.Library"));
        proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.Callback"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.COM.util.IDispatch"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.DdemlUtil$IDdeClient"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.DdemlUtil$IDdeConnectionList"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.DdemlUtil$IDdeConnection"));
    }

    @BuildStep
    public void runtimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInits) {
        runtimeInits.produce(new RuntimeInitializedClassBuildItem(""));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem(""));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem(""));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem(""));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem(""));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem(""));
    }

    @BuildStep
    public void addReflections(CombinedIndexBuildItem combinedIndexBuildItem,
            BuildProducer<ReflectiveClassBuildItem> reflectiveItems) {
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.invoke.MethodHandles$Lookup"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.invoke.MethodHandles"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.invoke.MethodHandle"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.invoke.MethodType"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.reflect.Method"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.nio.Buffer"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "com.sun.jna.Native"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.Object"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.lang.Throwable"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "com.sun.jna.ptr.ByReference"));

        IndexView index = combinedIndexBuildItem.getIndex();
        Set<ClassInfo> classInfos = index.getKnownClasses().stream()
                .filter(classInfo -> classInfo.methods().stream()
                        .anyMatch(methodInfo -> Modifier.isNative(methodInfo.flags())))
                .collect(Collectors.toSet());
        for (ClassInfo classInfo : classInfos) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }

        for (ClassInfo classInfo : index.getAllKnownImplementors(DotName.createSimple("com.sun.jna.NativeMapped"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }
        for (ClassInfo classInfo : index
                .getAllKnownImplementors(DotName.createSimple("com.sun.jna.platform.win32.COM.util.IComEnum"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }
        for (ClassInfo classInfo : index.getAllKnownImplementors(DotName.createSimple("java.awt.GraphicsConfiguration"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.awt.Window"));
        for (ClassInfo classInfo : index.getAllKnownSubclasses(DotName.createSimple("java.awt.Window"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, "java.awt.peer.ComponentPeer"));
        for (ClassInfo classInfo : index.getAllKnownSubclasses(DotName.createSimple("java.awt.peer.ComponentPeer"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }
        for (ClassInfo classInfo : index.getAllKnownImplementors(DotName.createSimple("com.sun.jna.CallbackProxy"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }
        for (ClassInfo classInfo : index.getAllKnownImplementors(DotName.createSimple("com.sun.jna.Structure"))) {
            reflectiveItems.produce(new ReflectiveClassBuildItem(true, false, classInfo.name().toString()));
        }

    }
}
