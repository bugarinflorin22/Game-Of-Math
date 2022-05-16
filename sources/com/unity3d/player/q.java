package com.unity3d.player;

import android.app.Activity;
import android.content.Context;
import com.unity3d.player.p;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class q {
    /* access modifiers changed from: private */
    public UnityPlayer a = null;
    /* access modifiers changed from: private */
    public Context b = null;
    private a c;
    /* access modifiers changed from: private */
    public final Semaphore d = new Semaphore(0);
    /* access modifiers changed from: private */
    public final Lock e = new ReentrantLock();
    /* access modifiers changed from: private */
    public p f = null;
    /* access modifiers changed from: private */
    public int g = 2;
    private boolean h = false;
    /* access modifiers changed from: private */
    public boolean i = false;

    public interface a {
        void a();
    }

    q(UnityPlayer unityPlayer) {
        this.a = unityPlayer;
    }

    /* access modifiers changed from: private */
    public void d() {
        p pVar = this.f;
        if (pVar != null) {
            this.a.removeViewFromPlayer(pVar);
            this.i = false;
            this.f.destroyPlayer();
            this.f = null;
            a aVar = this.c;
            if (aVar != null) {
                aVar.a();
            }
        }
    }

    public final void a() {
        this.e.lock();
        p pVar = this.f;
        if (pVar != null) {
            if (this.g == 0) {
                pVar.CancelOnPrepare();
            } else if (this.i) {
                this.h = pVar.a();
                if (!this.h) {
                    this.f.pause();
                }
            }
        }
        this.e.unlock();
    }

    public final boolean a(Context context, String str, int i2, int i3, int i4, boolean z, long j, long j2, a aVar) {
        this.e.lock();
        this.c = aVar;
        this.b = context;
        this.d.drainPermits();
        this.g = 2;
        final String str2 = str;
        final int i5 = i2;
        final int i6 = i3;
        final int i7 = i4;
        final boolean z2 = z;
        final long j3 = j;
        final long j4 = j2;
        runOnUiThread(new Runnable() {
            public final void run() {
                if (q.this.f != null) {
                    g.Log(5, "Video already playing");
                    int unused = q.this.g = 2;
                    q.this.d.release();
                    return;
                }
                q qVar = q.this;
                p unused2 = qVar.f = new p(qVar.b, str2, i5, i6, i7, z2, j3, j4, new p.a() {
                    public final void a(int i) {
                        q.this.e.lock();
                        int unused = q.this.g = i;
                        if (i == 3 && q.this.i) {
                            q.this.runOnUiThread(new Runnable() {
                                public final void run() {
                                    q.this.d();
                                    q.this.a.resume();
                                }
                            });
                        }
                        if (i != 0) {
                            q.this.d.release();
                        }
                        q.this.e.unlock();
                    }
                });
                if (q.this.f != null) {
                    q.this.a.addView(q.this.f);
                }
            }
        });
        boolean z3 = false;
        try {
            this.e.unlock();
            this.d.acquire();
            this.e.lock();
            if (this.g != 2) {
                z3 = true;
            }
        } catch (InterruptedException unused) {
        }
        runOnUiThread(new Runnable() {
            public final void run() {
                q.this.a.pause();
            }
        });
        runOnUiThread((!z3 || this.g == 3) ? new Runnable() {
            public final void run() {
                q.this.d();
                q.this.a.resume();
            }
        } : new Runnable() {
            public final void run() {
                if (q.this.f != null) {
                    q.this.a.addViewToPlayer(q.this.f, true);
                    boolean unused = q.this.i = true;
                    q.this.f.requestFocus();
                }
            }
        });
        this.e.unlock();
        return z3;
    }

    public final void b() {
        this.e.lock();
        p pVar = this.f;
        if (pVar != null && this.i && !this.h) {
            pVar.start();
        }
        this.e.unlock();
    }

    public final void c() {
        this.e.lock();
        p pVar = this.f;
        if (pVar != null) {
            pVar.updateVideoLayout();
        }
        this.e.unlock();
    }

    /* access modifiers changed from: protected */
    public final void runOnUiThread(Runnable runnable) {
        Context context = this.b;
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(runnable);
        } else {
            g.Log(5, "Not running from an Activity; Ignoring execution request...");
        }
    }
}
