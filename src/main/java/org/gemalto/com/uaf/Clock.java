package org.gemalto.com.uaf;

/**
 * Created by drurenia on 2/14/2018.
 */
public class Clock {

    private long start;

    private boolean started;

    public Clock start() {
        if (started) {
            throw new IllegalStateException("Clock was already started.");
        }
        this.start = System.currentTimeMillis();
        this.started = true;
        return this;
    }

    public long stopAndGetTotal() {
        final long total = System.currentTimeMillis() - start;
        started = false;
        start = 0L;
        return total;
    }
}
