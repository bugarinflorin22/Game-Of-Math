package org.fmod;

import android.media.AudioRecord;
import android.util.Log;
import java.nio.ByteBuffer;

final class a implements Runnable {
    private final FMODAudioDevice a;
    private final ByteBuffer b;
    private final int c;
    private final int d;
    private final int e = 2;
    private volatile Thread f;
    private volatile boolean g;
    private AudioRecord h;
    private boolean i;

    a(FMODAudioDevice fMODAudioDevice, int i2, int i3) {
        this.a = fMODAudioDevice;
        this.c = i2;
        this.d = i3;
        this.b = ByteBuffer.allocateDirect(AudioRecord.getMinBufferSize(i2, i3, 2));
    }

    private void d() {
        AudioRecord audioRecord = this.h;
        if (audioRecord != null) {
            if (audioRecord.getState() == 1) {
                this.h.stop();
            }
            this.h.release();
            this.h = null;
        }
        this.b.position(0);
        this.i = false;
    }

    public final int a() {
        return this.b.capacity();
    }

    public final void b() {
        if (this.f != null) {
            c();
        }
        this.g = true;
        this.f = new Thread(this);
        this.f.start();
    }

    public final void c() {
        while (this.f != null) {
            this.g = false;
            try {
                this.f.join();
                this.f = null;
            } catch (InterruptedException unused) {
            }
        }
    }

    public final void run() {
        int i2 = 3;
        while (this.g) {
            if (!this.i && i2 > 0) {
                d();
                this.h = new AudioRecord(1, this.c, this.d, this.e, this.b.capacity());
                boolean z = true;
                if (this.h.getState() != 1) {
                    z = false;
                }
                this.i = z;
                if (this.i) {
                    this.b.position(0);
                    this.h.startRecording();
                    i2 = 3;
                } else {
                    Log.e("FMOD", "AudioRecord failed to initialize (status " + this.h.getState() + ")");
                    i2 += -1;
                    d();
                }
            }
            if (this.i && this.h.getRecordingState() == 3) {
                AudioRecord audioRecord = this.h;
                ByteBuffer byteBuffer = this.b;
                this.a.fmodProcessMicData(this.b, audioRecord.read(byteBuffer, byteBuffer.capacity()));
                this.b.position(0);
            }
        }
        d();
    }
}
