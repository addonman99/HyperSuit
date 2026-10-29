package com.voidjam.hypersuit.client;

public final class SlowMoClientState {
    private static float targetTps = 10.0F;
    private static int revision = 0;
    private SlowMoClientState() {}
    public static float getTps() { return targetTps; }
    public static int getRevision() { return revision; }
    public static void setTps(float tps) {
        targetTps = Math.max(1.0F, Math.min(20.0F, tps));
        revision++;
    }
}
