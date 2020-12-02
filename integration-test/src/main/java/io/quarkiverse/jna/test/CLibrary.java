package io.quarkiverse.jna.test;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

// This is the standard, stable way of mapping, which supports extensive
// customization and mapping of Java to native types.
public interface CLibrary extends Library {
    CLibrary INSTANCE = (CLibrary) Native.load((Platform.isWindows() ? "msvcrt" : "c"),
            CLibrary.class);

    int atoi(String value);
}