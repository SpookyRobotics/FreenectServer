package com.spookybox.util;

import java.util.Optional;

public class ThreadUtils {
    private static final long JOIN_TIMEOUT = 2000;

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void joinThread(Optional<Thread> thread) {
        thread.ifPresent(ThreadUtils::joinThread);
    }

    private static void joinThread(Thread thread){
        try {
            thread.join(JOIN_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Interrupted waiting for thread to join: "+thread);

        }
    }
}
