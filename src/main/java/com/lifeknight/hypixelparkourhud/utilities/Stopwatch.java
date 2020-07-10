package com.lifeknight.hypixelparkourhud.utilities;

public class Stopwatch {
    private long startTime;
    private boolean active = false;
    public Stopwatch() {}

    public void start() {
        startTime = System.currentTimeMillis();
        active = true;
    }

    public void end() {
        active = false;
    }

    public long getTotalMilliseconds() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean isRunning() {
        return active;
    }
}
