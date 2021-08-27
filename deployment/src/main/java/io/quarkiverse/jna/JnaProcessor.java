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
import io.quarkus.deployment.builditem.nativeimage.JniRuntimeAccessBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourcePatternsBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageSystemPropertyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

public class JnaProcessor {

    private static final Logger LOGGER = Logger.getLogger(JnaProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("jna");
    }

    @BuildStep
    NativeImageSystemPropertyBuildItem removeAwt() {
        return new NativeImageSystemPropertyBuildItem("java.awt.headless", "false");
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
                        //LOGGER.warn("add proxy:" + className);
                        proxiesCreated.add(className.toString());
                    }
                });

    }

    private void produceRecursiveRuntimes(IndexView index,
            DotName interfaceDN,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInits,
            Set<String> runtimeItemsCreated) {
        index.getKnownDirectImplementors(interfaceDN).stream()
                .filter(classinfo -> Modifier.isInterface(classinfo.flags()))
                .map(ClassInfo::name)
                .forEach((className) -> {
                    if (!runtimeItemsCreated.contains(className.toString())) {
                        runtimeInits.produce(new RuntimeInitializedClassBuildItem(className.toString()));
                        produceRecursiveRuntimes(index, className, runtimeInits, runtimeItemsCreated);
                        //LOGGER.warn("add proxy:" + className);
                        runtimeItemsCreated.add(className.toString());
                    }
                });
    }

    @BuildStep
    void addProxies(CombinedIndexBuildItem combinedIndexBuildItem,
            BuildProducer<NativeImageProxyDefinitionBuildItem> proxies) {
        IndexView index = combinedIndexBuildItem.getIndex();
        Set<String> proxiesCreated = new HashSet<>();
        // getAllKnownDirectImplementors skip interface, so I have to do it myself.
        //produceRecursiveProxies(index, DotName.createSimple("com.sun.jna.Library"), proxies, proxiesCreated);
        //produceRecursiveProxies(index, DotName.createSimple("com.sun.jna.Callback"), proxies, proxiesCreated);
        proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.Library"));
        proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.Callback"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.COM.util.IDispatch"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.DdemlUtil$IDdeClient"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.DdemlUtil$IDdeConnectionList"));
        //proxies.produce(new NativeImageProxyDefinitionBuildItem("com.sun.jna.platform.win32.DdemlUtil$IDdeConnection"));
    }

    @BuildStep
    public void runtimeInitializedClasses(CombinedIndexBuildItem combinedIndexBuildItem,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInits) {
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.font.FontManagerNativeLibrary"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.font.SunLayoutEngine"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.SurfaceData"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.pipe.Region"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.font.SunFontManager"));
        //runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.Platform"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.NativeLong"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.pipe.SpanClipRenderer"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.font.StrikeCache"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.xr.XRBackendNative"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.AWTEvent"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.AWTKeyStroke"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Color"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Component"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Cursor"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Dimension"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Event"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Font"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.FontMetrics"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.image.BufferedImage"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.image.ColorModel"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.image.DataBuffer"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.image.Raster"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.image.SampleModel"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Insets"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.KeyboardFocusManager"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.LightweightDispatcher"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.MenuComponent"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Rectangle"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.Toolkit"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("java.awt.TrayIcon"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.DebugGraphics"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.basic.BasicGraphicsUtils"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalBorders$ButtonBorder"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalBorders$MenuBarBorder"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalBorders$MenuItemBorder"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalBorders$PopupMenuBorder"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalBumps"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalIconFactory"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.plaf.metal.MetalTheme"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.RepaintManager"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.awt.im.InputMethodContext"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.awt.image.ToolkitImage"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.Disposer"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.pipe.ShapeSpanIterator"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("sun.java2d.SunGraphics2D"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("javax.swing.ImageIcon"));
        //runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.platform.WindowUtils$Holder"));
        //runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.platform.KeyboardUtils"));
        //runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.platform.FileUtils$Holder"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.Native"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.NativeLibrary"));
        runtimeInits.produce(new RuntimeInitializedClassBuildItem("com.sun.jna.Structure$FFIType"));
        Set<String> runtimeItemsCreated = new HashSet<>();
        IndexView index = combinedIndexBuildItem.getIndex();
        produceRecursiveRuntimes(index, DotName.createSimple("com.sun.jna.Library"), runtimeInits, runtimeItemsCreated);
    }

    @BuildStep
    public void addResources(BuildProducer<NativeImageResourcePatternsBuildItem> resources) {
        resources.produce(NativeImageResourcePatternsBuildItem.builder()
                .includePattern("\\\\QMETA-INF/services/jdk.vm.ci.hotspot.HotSpotJVMCIBackendFactory\\\\E")
                .includePattern("\\\\QMETA-INF/services/jdk.vm.ci.services.JVMCIServiceLocator\\\\E")
                .includePattern("\\\\Qcom/sun/jna/linux-x86-64/libjnidispatch.so\\\\E")
                .build());
    }

    @BuildStep
    public void addJniruntimeAccess(BuildProducer<JniRuntimeAccessBuildItem> jnis) {
        jnis.produce(new JniRuntimeAccessBuildItem(true, true, false, false,
                "com.sun.jna.Native",
                "java.lang.Class",
                "java.lang.reflect.Method",
                "java.lang.String",
                "java.nio.Buffer",
                "java.nio.ByteBuffer",
                "java.nio.CharBuffer",
                "java.nio.ShortBuffer",
                "java.nio.IntBuffer",
                "java.nio.LongBuffer",
                "java.nio.FloatBuffer",
                "java.nio.DoubleBuffer",
                "com.sun.jna.Structure$ByValue",
                "com.sun.jna.WString",
                "com.sun.jna.NativeMapped",
                "com.sun.jna.IntegerType",
                "com.sun.jna.PointerType",
                "com.sun.jna.JNIEnv",
                "com.sun.jna.Native$ffi_callback",
                "com.sun.jna.FromNativeConverter",
                "com.sun.jna.Callback",
                "com.sun.jna.CallbackReference$AttachOptions",
                "com.sun.jna.CallbackReference",
                "com.sun.jna.Structure$FFIType",
                "com.sun.jna.NativeLong",
                "com.sun.jna.ptr.PointerByReference"));
        jnis.produce(new JniRuntimeAccessBuildItem(true, true, true, true,
                "java.lang.Void",
                "java.lang.Boolean",
                "java.lang.Byte",
                "java.lang.Character",
                "java.lang.Short",
                "java.lang.Integer",
                "java.lang.Long",
                "java.lang.Float",
                "java.lang.Double",
                "com.sun.jna.Pointer",
                "com.sun.jna.Structure",
                "com.sun.jna.IntegerType",
                "com.sun.jna.PointerType",
                "com.sun.jna.Structure$FFIType$FFITypes"));
    }

    @BuildStep
    public void addReflections(CombinedIndexBuildItem combinedIndexBuildItem,
            BuildProducer<ReflectiveClassBuildItem> reflectiveItems) {
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "java.lang.reflect.Method"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "java.nio.Buffer"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.Native"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "java.lang.Object"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "java.lang.Throwable"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.ptr.ByReference"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.CallbackReference"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.Klass"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.Structure"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.NativeLong"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.ptr.PointerByReference"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "com.sun.jna.ptr.IntByReference"));
        reflectiveItems.produce(new ReflectiveClassBuildItem(true, true, "java.util.Base64$Decoder"));

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
