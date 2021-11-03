package com.xhhold.plugin;

import com.intellij.openapi.util.SystemInfo;

public class WinRTMediaSessionManager {
    static {
        if (SystemInfo.isWindows) {
            System.load("");
        }
    }

    public native static void init();
}
