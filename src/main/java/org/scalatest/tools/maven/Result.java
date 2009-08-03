package org.scalatest.tools.maven;

import java.util.concurrent.atomic.AtomicBoolean;


public class Result {

    private static final AtomicBoolean fail = new AtomicBoolean(false);

    public static void fail() {
        fail.set(true);
    }

    public static boolean isFail() {
        return fail.get();
    }
}
