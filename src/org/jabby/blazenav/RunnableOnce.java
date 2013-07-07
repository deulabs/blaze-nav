package org.jabby.blazenav;

abstract class RunnableOnce implements Runnable {
    boolean ran = false;

    @Override public final void run() {
        if (!ran) {
            runOnce();
            ran = true;
        }
    }

    protected abstract void runOnce();
}
