package org.fmod;

import android.media.AudioTrack;
import android.util.Log;
import java.nio.ByteBuffer;

public class FMODAudioDevice implements Runnable {
    private static int h = 0;
    private static int i = 1;
    private static int j = 2;
    private static int k = 3;
    private volatile Thread a = null;
    private volatile boolean b = false;
    private AudioTrack c = null;
    private boolean d = false;
    private ByteBuffer e = null;
    private byte[] f = null;
    private volatile a g;

    private native int fmodGetInfo(int i2);

    private native int fmodProcess(ByteBuffer byteBuffer);

    private void releaseAudioTrack() {
        AudioTrack audioTrack = this.c;
        if (audioTrack != null) {
            if (audioTrack.getState() == 1) {
                this.c.stop();
            }
            this.c.release();
            this.c = null;
        }
        this.e = null;
        this.f = null;
        this.d = false;
    }

    public synchronized void close() {
        stop();
    }

    /* access modifiers changed from: package-private */
    public native int fmodProcessMicData(ByteBuffer byteBuffer, int i2);

    public boolean isRunning() {
        return this.a != null && this.a.isAlive();
    }

    public void run() {
        int i2 = 3;
        while (this.b) {
            if (!this.d && i2 > 0) {
                releaseAudioTrack();
                int fmodGetInfo = fmodGetInfo(h);
                int round = Math.round(((float) AudioTrack.getMinBufferSize(fmodGetInfo, 3, 2)) * 1.1f) & -4;
                int fmodGetInfo2 = fmodGetInfo(i);
                int fmodGetInfo3 = fmodGetInfo(j) * fmodGetInfo2 * 4;
                this.c = new AudioTrack(3, fmodGetInfo, 3, 2, fmodGetInfo3 > round ? fmodGetInfo3 : round, 1);
                this.d = this.c.getState() == 1;
                if (this.d) {
                    this.e = ByteBuffer.allocateDirect(fmodGetInfo2 * 2 * 2);
                    this.f = new byte[this.e.capacity()];
                    this.c.play();
                    i2 = 3;
                } else {
                    Log.e("FMOD", "AudioTrack failed to initialize (status " + this.c.getState() + ")");
                    releaseAudioTrack();
                    i2 += -1;
                }
            }
            if (this.d) {
                if (fmodGetInfo(k) == 1) {
                    fmodProcess(this.e);
                    ByteBuffer byteBuffer = this.e;
                    byteBuffer.get(this.f, 0, byteBuffer.capacity());
                    this.c.write(this.f, 0, this.e.capacity());
                    this.e.position(0);
                } else {
                    releaseAudioTrack();
                }
            }
        }
        releaseAudioTrack();
    }

    public synchronized void start() {
        if (this.a != null) {
            stop();
        }
        this.a = new Thread(this, "FMODAudioDevice");
        this.a.setPriority(10);
        this.b = true;
        this.a.start();
        if (this.g != null) {
            this.g.b();
        }
    }

    public synchronized int startAudioRecord(int i2, int i3, int i4) {
        if (this.g == null) {
            this.g = new a(this, i2, i3);
            this.g.b();
        }
        return this.g.a();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:1:0x0001 */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0001 A[LOOP:0: B:1:0x0001->B:16:0x0001, LOOP_START, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void stop() {
        /*
            r1 = this;
            monitor-enter(r1)
        L_0x0001:
            java.lang.Thread r0 = r1.a     // Catch:{ all -> 0x001c }
            if (r0 == 0) goto L_0x0011
            r0 = 0
            r1.b = r0     // Catch:{ all -> 0x001c }
            java.lang.Thread r0 = r1.a     // Catch:{ InterruptedException -> 0x0001 }
            r0.join()     // Catch:{ InterruptedException -> 0x0001 }
            r0 = 0
            r1.a = r0     // Catch:{ InterruptedException -> 0x0001 }
            goto L_0x0001
        L_0x0011:
            org.fmod.a r0 = r1.g     // Catch:{ all -> 0x001c }
            if (r0 == 0) goto L_0x001a
            org.fmod.a r0 = r1.g     // Catch:{ all -> 0x001c }
            r0.c()     // Catch:{ all -> 0x001c }
        L_0x001a:
            monitor-exit(r1)
            return
        L_0x001c:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.fmod.FMODAudioDevice.stop():void");
    }

    public synchronized void stopAudioRecord() {
        if (this.g != null) {
            this.g.c();
            this.g = null;
        }
    }
}
