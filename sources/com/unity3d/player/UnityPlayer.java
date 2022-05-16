package com.unity3d.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import com.FlorinCompany.Gom.BuildConfig;
import com.unity3d.player.l;
import com.unity3d.player.q;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class UnityPlayer extends FrameLayout implements f {
    public static Activity currentActivity;
    e a = new e(this, (byte) 0);
    k b = null;
    /* access modifiers changed from: private */
    public int c = -1;
    /* access modifiers changed from: private */
    public boolean d = false;
    private boolean e = true;
    private n f = new n();
    private final ConcurrentLinkedQueue g = new ConcurrentLinkedQueue();
    private BroadcastReceiver h = null;
    private boolean i = false;
    private c j = new c(this, (byte) 0);
    private TelephonyManager k;
    private ClipboardManager l;
    /* access modifiers changed from: private */
    public l m;
    private GoogleARCoreApi n = null;
    private a o = new a();
    private Camera2Wrapper p = null;
    /* access modifiers changed from: private */
    public Context q;
    /* access modifiers changed from: private */
    public SurfaceView r;
    /* access modifiers changed from: private */
    public boolean s;
    /* access modifiers changed from: private */
    public q t;

    class a implements SensorEventListener {
        a() {
        }

        public final void onAccuracyChanged(Sensor sensor, int i) {
        }

        public final void onSensorChanged(SensorEvent sensorEvent) {
        }
    }

    enum b {
        ;

        static {
            d = new int[]{a, b, c};
        }
    }

    private class c extends PhoneStateListener {
        private c() {
        }

        /* synthetic */ c(UnityPlayer unityPlayer, byte b) {
            this();
        }

        public final void onCallStateChanged(int i, String str) {
            UnityPlayer unityPlayer = UnityPlayer.this;
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            unityPlayer.nativeMuteMasterAudio(z);
        }
    }

    enum d {
        PAUSE,
        RESUME,
        QUIT,
        SURFACE_LOST,
        SURFACE_ACQUIRED,
        FOCUS_LOST,
        FOCUS_GAINED,
        NEXT_FRAME
    }

    private class e extends Thread {
        Handler a;
        boolean b;
        boolean c;
        int d;
        int e;

        private e() {
            this.b = false;
            this.c = false;
            this.d = b.b;
            this.e = 5;
        }

        /* synthetic */ e(UnityPlayer unityPlayer, byte b2) {
            this();
        }

        private void a(d dVar) {
            Handler handler = this.a;
            if (handler != null) {
                Message.obtain(handler, 2269, dVar).sendToTarget();
            }
        }

        public final void a() {
            a(d.QUIT);
        }

        public final void a(Runnable runnable) {
            if (this.a != null) {
                a(d.PAUSE);
                Message.obtain(this.a, runnable).sendToTarget();
            }
        }

        public final void b() {
            a(d.RESUME);
        }

        public final void b(Runnable runnable) {
            if (this.a != null) {
                a(d.SURFACE_LOST);
                Message.obtain(this.a, runnable).sendToTarget();
            }
        }

        public final void c() {
            a(d.FOCUS_GAINED);
        }

        public final void c(Runnable runnable) {
            Handler handler = this.a;
            if (handler != null) {
                Message.obtain(handler, runnable).sendToTarget();
                a(d.SURFACE_ACQUIRED);
            }
        }

        public final void d() {
            a(d.FOCUS_LOST);
        }

        public final void d(Runnable runnable) {
            Handler handler = this.a;
            if (handler != null) {
                Message.obtain(handler, runnable).sendToTarget();
            }
        }

        public final void run() {
            setName("UnityMain");
            Looper.prepare();
            this.a = new Handler(new Handler.Callback() {
                private void a() {
                    if (e.this.d == b.c && e.this.c) {
                        UnityPlayer.this.nativeFocusChanged(true);
                        e.this.d = b.a;
                    }
                }

                public final boolean handleMessage(Message message) {
                    if (message.what != 2269) {
                        return false;
                    }
                    d dVar = (d) message.obj;
                    if (dVar == d.NEXT_FRAME) {
                        return true;
                    }
                    if (dVar == d.QUIT) {
                        Looper.myLooper().quit();
                    } else if (dVar == d.RESUME) {
                        e.this.b = true;
                    } else if (dVar == d.PAUSE) {
                        e.this.b = false;
                    } else if (dVar == d.SURFACE_LOST) {
                        e.this.c = false;
                    } else {
                        if (dVar == d.SURFACE_ACQUIRED) {
                            e.this.c = true;
                        } else if (dVar == d.FOCUS_LOST) {
                            if (e.this.d == b.a) {
                                UnityPlayer.this.nativeFocusChanged(false);
                            }
                            e.this.d = b.b;
                        } else if (dVar == d.FOCUS_GAINED) {
                            e.this.d = b.c;
                        }
                        a();
                    }
                    return true;
                }
            });
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                public final boolean queueIdle() {
                    UnityPlayer.this.executeGLThreadJobs();
                    if (!e.this.b || !e.this.c) {
                        return true;
                    }
                    if (e.this.e >= 0) {
                        if (e.this.e == 0 && UnityPlayer.this.k()) {
                            UnityPlayer.this.a();
                        }
                        e.this.e--;
                    }
                    if (!UnityPlayer.this.isFinishing() && !UnityPlayer.this.nativeRender()) {
                        UnityPlayer.this.e();
                    }
                    Message.obtain(e.this.a, 2269, d.NEXT_FRAME).sendToTarget();
                    return true;
                }
            });
            Looper.loop();
        }
    }

    private abstract class f implements Runnable {
        private f() {
        }

        /* synthetic */ f(UnityPlayer unityPlayer, byte b) {
            this();
        }

        public abstract void a();

        public final void run() {
            if (!UnityPlayer.this.isFinishing()) {
                a();
            }
        }
    }

    static {
        new m().a();
        try {
            System.loadLibrary("main");
        } catch (UnsatisfiedLinkError e2) {
            g.Log(6, "Failed to load 'libmain.so', the application will terminate.");
            throw e2;
        }
    }

    public UnityPlayer(Context context) {
        super(context);
        if (context instanceof Activity) {
            currentActivity = (Activity) context;
            this.c = currentActivity.getRequestedOrientation();
        }
        a(currentActivity);
        this.q = context;
        if (currentActivity != null && k()) {
            this.m = new l(this.q, l.a.a()[getSplashMode()]);
            addView(this.m);
        }
        a(this.q.getApplicationInfo());
        if (!n.c()) {
            AlertDialog create = new AlertDialog.Builder(this.q).setTitle("Failure to initialize!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    UnityPlayer.this.e();
                }
            }).setMessage("Your hardware does not support this application, sorry!").create();
            create.setCancelable(false);
            create.show();
            return;
        }
        initJni(context);
        this.f.c(true);
        this.r = c();
        this.r.setContentDescription(a(context));
        addView(this.r);
        bringChildToFront(this.m);
        this.s = false;
        nativeInitWebRequest(UnityWebRequest.class);
        m();
        this.k = (TelephonyManager) this.q.getSystemService("phone");
        this.l = (ClipboardManager) this.q.getSystemService("clipboard");
        this.p = new Camera2Wrapper(this.q);
        this.a.start();
    }

    public static void UnitySendMessage(String str, String str2, String str3) {
        if (!n.c()) {
            g.Log(5, "Native libraries not loaded - dropping message for " + str + "." + str2);
            return;
        }
        try {
            nativeUnitySendMessage(str, str2, str3.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException unused) {
        }
    }

    private static String a(Context context) {
        return context.getResources().getString(context.getResources().getIdentifier("game_view_content_description", "string", context.getPackageName()));
    }

    /* access modifiers changed from: private */
    public void a() {
        a((Runnable) new Runnable() {
            public final void run() {
                UnityPlayer unityPlayer = UnityPlayer.this;
                unityPlayer.removeView(unityPlayer.m);
                l unused = UnityPlayer.this.m = null;
            }
        });
    }

    /* access modifiers changed from: private */
    public void a(int i2, Surface surface) {
        if (!this.d) {
            b(0, surface);
        }
    }

    private static void a(Activity activity) {
        View decorView;
        if (activity != null && activity.getIntent().getBooleanExtra("android.intent.extra.VR_LAUNCH", false) && activity.getWindow() != null && (decorView = activity.getWindow().getDecorView()) != null) {
            decorView.setSystemUiVisibility(7);
        }
    }

    private static void a(ApplicationInfo applicationInfo) {
        if (NativeLoader.load(applicationInfo.nativeLibraryDir)) {
            n.a();
        } else {
            g.Log(6, "NativeLoader.load failure, Unity libraries were not loaded.");
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.view.View r5, android.view.View r6) {
        /*
            r4 = this;
            com.unity3d.player.n r0 = r4.f
            boolean r0 = r0.d()
            r1 = 0
            if (r0 != 0) goto L_0x000e
            r4.pause()
            r0 = 1
            goto L_0x000f
        L_0x000e:
            r0 = 0
        L_0x000f:
            if (r5 == 0) goto L_0x0030
            android.view.ViewParent r2 = r5.getParent()
            boolean r3 = r2 instanceof com.unity3d.player.UnityPlayer
            if (r3 == 0) goto L_0x001e
            r3 = r2
            com.unity3d.player.UnityPlayer r3 = (com.unity3d.player.UnityPlayer) r3
            if (r3 == r4) goto L_0x0030
        L_0x001e:
            boolean r3 = r2 instanceof android.view.ViewGroup
            if (r3 == 0) goto L_0x0027
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            r2.removeView(r5)
        L_0x0027:
            r4.addView(r5)
            r4.bringChildToFront(r5)
            r5.setVisibility(r1)
        L_0x0030:
            if (r6 == 0) goto L_0x0040
            android.view.ViewParent r5 = r6.getParent()
            if (r5 != r4) goto L_0x0040
            r5 = 8
            r6.setVisibility(r5)
            r4.removeView(r6)
        L_0x0040:
            if (r0 == 0) goto L_0x0045
            r4.resume()
        L_0x0045:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unity3d.player.UnityPlayer.a(android.view.View, android.view.View):void");
    }

    private void a(f fVar) {
        if (!isFinishing()) {
            b((Runnable) fVar);
        }
    }

    private void b(Runnable runnable) {
        if (n.c()) {
            if (Thread.currentThread() == this.a) {
                runnable.run();
            } else {
                this.g.add(runnable);
            }
        }
    }

    private static boolean b() {
        if (currentActivity == null) {
            return false;
        }
        TypedValue typedValue = new TypedValue();
        return currentActivity.getTheme().resolveAttribute(16842840, typedValue, true) && typedValue.type == 18 && typedValue.data == 1;
    }

    private boolean b(final int i2, final Surface surface) {
        if (!n.c() || !this.f.e()) {
            return false;
        }
        final Semaphore semaphore = new Semaphore(0);
        AnonymousClass20 r1 = new Runnable() {
            public final void run() {
                UnityPlayer.this.nativeRecreateGfxState(i2, surface);
                semaphore.release();
            }
        };
        if (i2 != 0) {
            r1.run();
        } else if (surface == null) {
            this.a.b(r1);
        } else {
            this.a.c(r1);
        }
        if (surface != null || i2 != 0) {
            return true;
        }
        try {
            if (semaphore.tryAcquire(4, TimeUnit.SECONDS)) {
                return true;
            }
            g.Log(5, "Timeout while trying detaching primary window.");
            return true;
        } catch (InterruptedException unused) {
            g.Log(5, "UI thread got interrupted while trying to detach the primary window from the Unity Engine.");
            return true;
        }
    }

    /* access modifiers changed from: private */
    public SurfaceView c() {
        SurfaceView surfaceView = new SurfaceView(this.q);
        if (b()) {
            surfaceView.getHolder().setFormat(-3);
            surfaceView.setZOrderOnTop(true);
        } else {
            surfaceView.getHolder().setFormat(-1);
        }
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public final void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                UnityPlayer.this.a(0, surfaceHolder.getSurface());
                UnityPlayer.this.d();
            }

            public final void surfaceCreated(SurfaceHolder surfaceHolder) {
                UnityPlayer.this.a(0, surfaceHolder.getSurface());
            }

            public final void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                UnityPlayer.this.a(0, (Surface) null);
            }
        });
        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        return surfaceView;
    }

    /* access modifiers changed from: private */
    public void d() {
        if (n.c() && this.f.e()) {
            this.a.d(new Runnable() {
                public final void run() {
                    UnityPlayer.this.nativeSendSurfaceChangedEvent();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        Context context = this.q;
        if ((context instanceof Activity) && !((Activity) context).isFinishing()) {
            ((Activity) this.q).finish();
        }
    }

    private void f() {
        reportSoftInputStr((String) null, 1, true);
        if (this.f.g()) {
            if (n.c()) {
                final Semaphore semaphore = new Semaphore(0);
                this.a.a(isFinishing() ? new Runnable() {
                    public final void run() {
                        UnityPlayer.this.g();
                        semaphore.release();
                    }
                } : new Runnable() {
                    public final void run() {
                        if (UnityPlayer.this.nativePause()) {
                            boolean unused = UnityPlayer.this.s = true;
                            UnityPlayer.this.g();
                            semaphore.release(2);
                            return;
                        }
                        semaphore.release();
                    }
                });
                try {
                    if (!semaphore.tryAcquire(4, TimeUnit.SECONDS)) {
                        g.Log(5, "Timeout while trying to pause the Unity Engine.");
                    }
                } catch (InterruptedException unused) {
                    g.Log(5, "UI thread got interrupted while trying to pause the Unity Engine.");
                }
                if (semaphore.drainPermits() > 0) {
                    destroy();
                }
            }
            this.f.d(false);
            this.f.b(true);
            if (this.i) {
                this.k.listen(this.j, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void g() {
        nativeDone();
        this.f.c(false);
    }

    private void h() {
        if (this.f.f()) {
            this.f.d(true);
            b((Runnable) new Runnable() {
                public final void run() {
                    UnityPlayer.this.nativeResume();
                }
            });
            this.a.b();
        }
    }

    private static void i() {
        if (n.c()) {
            if (NativeLoader.unload()) {
                n.b();
                return;
            }
            throw new UnsatisfiedLinkError("Unable to unload libraries from libmain.so");
        }
    }

    private final native void initJni(Context context);

    private ApplicationInfo j() {
        return this.q.getPackageManager().getApplicationInfo(this.q.getPackageName(), 128);
    }

    /* access modifiers changed from: private */
    public boolean k() {
        try {
            return j().metaData.getBoolean("unity.splash-enable");
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean l() {
        try {
            return j().metaData.getBoolean("unity.tango-enable");
        } catch (Exception unused) {
            return false;
        }
    }

    protected static boolean loadLibraryStatic(String str) {
        StringBuilder sb;
        try {
            System.loadLibrary(str);
            return true;
        } catch (UnsatisfiedLinkError unused) {
            sb = new StringBuilder("Unable to find ");
            sb.append(str);
            g.Log(6, sb.toString());
            return false;
        } catch (Exception e2) {
            sb = new StringBuilder("Unknown error ");
            sb.append(e2);
            g.Log(6, sb.toString());
            return false;
        }
    }

    private void m() {
        Context context = this.q;
        if (context instanceof Activity) {
            ((Activity) context).getWindow().setFlags(1024, 1024);
        }
    }

    private final native void nativeDone();

    /* access modifiers changed from: private */
    public final native void nativeFocusChanged(boolean z);

    private final native void nativeInitWebRequest(Class cls);

    private final native boolean nativeInjectEvent(InputEvent inputEvent);

    /* access modifiers changed from: private */
    public final native boolean nativeIsAutorotationOn();

    /* access modifiers changed from: private */
    public final native void nativeLowMemory();

    /* access modifiers changed from: private */
    public final native void nativeMuteMasterAudio(boolean z);

    /* access modifiers changed from: private */
    public final native boolean nativePause();

    /* access modifiers changed from: private */
    public final native void nativeRecreateGfxState(int i2, Surface surface);

    /* access modifiers changed from: private */
    public final native boolean nativeRender();

    private final native void nativeRestartActivityIndicator();

    /* access modifiers changed from: private */
    public final native void nativeResume();

    /* access modifiers changed from: private */
    public final native void nativeSendSurfaceChangedEvent();

    /* access modifiers changed from: private */
    public final native void nativeSetInputSelection(int i2, int i3);

    /* access modifiers changed from: private */
    public final native void nativeSetInputString(String str);

    /* access modifiers changed from: private */
    public final native void nativeSoftInputCanceled();

    /* access modifiers changed from: private */
    public final native void nativeSoftInputClosed();

    private final native void nativeSoftInputLostFocus();

    private static native void nativeUnitySendMessage(String str, String str2, byte[] bArr);

    /* access modifiers changed from: package-private */
    public final void a(Runnable runnable) {
        Context context = this.q;
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(runnable);
        } else {
            g.Log(5, "Not running Unity from an Activity; ignored...");
        }
    }

    /* access modifiers changed from: protected */
    public void addPhoneCallListener() {
        this.i = true;
        this.k.listen(this.j, 32);
    }

    public boolean addViewToPlayer(View view, boolean z) {
        a(view, (View) z ? this.r : null);
        boolean z2 = true;
        boolean z3 = view.getParent() == this;
        boolean z4 = z && this.r.getParent() == null;
        boolean z5 = this.r.getParent() == this;
        if (!z3 || (!z4 && !z5)) {
            z2 = false;
        }
        if (!z2) {
            if (!z3) {
                g.Log(6, "addViewToPlayer: Failure adding view to hierarchy");
            }
            if (!z4 && !z5) {
                g.Log(6, "addViewToPlayer: Failure removing old view from hierarchy");
            }
        }
        return z2;
    }

    public void configurationChanged(Configuration configuration) {
        SurfaceView surfaceView = this.r;
        if (surfaceView instanceof SurfaceView) {
            surfaceView.getHolder().setSizeFromLayout();
        }
        q qVar = this.t;
        if (qVar != null) {
            qVar.c();
        }
        GoogleVrProxy b2 = GoogleVrApi.b();
        if (b2 != null) {
            b2.c();
        }
    }

    public void destroy() {
        if (GoogleVrApi.b() != null) {
            GoogleVrApi.a();
        }
        Camera2Wrapper camera2Wrapper = this.p;
        if (camera2Wrapper != null) {
            camera2Wrapper.a();
            this.p = null;
        }
        this.s = true;
        if (!this.f.d()) {
            pause();
        }
        this.a.a();
        try {
            this.a.join(4000);
        } catch (InterruptedException unused) {
            this.a.interrupt();
        }
        BroadcastReceiver broadcastReceiver = this.h;
        if (broadcastReceiver != null) {
            this.q.unregisterReceiver(broadcastReceiver);
        }
        this.h = null;
        if (n.c()) {
            removeAllViews();
        }
        kill();
        i();
    }

    /* access modifiers changed from: protected */
    public void disableLogger() {
        g.a = true;
    }

    public boolean displayChanged(int i2, Surface surface) {
        if (i2 == 0) {
            this.d = surface != null;
            a((Runnable) new Runnable() {
                public final void run() {
                    if (UnityPlayer.this.d) {
                        UnityPlayer unityPlayer = UnityPlayer.this;
                        unityPlayer.removeView(unityPlayer.r);
                        return;
                    }
                    UnityPlayer unityPlayer2 = UnityPlayer.this;
                    unityPlayer2.addView(unityPlayer2.r);
                }
            });
        }
        return b(i2, surface);
    }

    /* access modifiers changed from: protected */
    public void executeGLThreadJobs() {
        while (true) {
            Runnable runnable = (Runnable) this.g.poll();
            if (runnable != null) {
                runnable.run();
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public String getClipboardText() {
        ClipData primaryClip = this.l.getPrimaryClip();
        return primaryClip != null ? primaryClip.getItemAt(0).coerceToText(this.q).toString() : BuildConfig.FLAVOR;
    }

    public Bundle getSettings() {
        return Bundle.EMPTY;
    }

    /* access modifiers changed from: protected */
    public int getSplashMode() {
        try {
            return j().metaData.getInt("unity.splash-mode");
        } catch (Exception unused) {
            return 0;
        }
    }

    public View getView() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void hideSoftInput() {
        final AnonymousClass5 r0 = new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null) {
                    UnityPlayer.this.b.dismiss();
                    UnityPlayer.this.b = null;
                }
            }
        };
        if (j.b) {
            a((f) new f() {
                public final void a() {
                    UnityPlayer.this.a(r0);
                }
            });
        } else {
            a((Runnable) r0);
        }
    }

    public void init(int i2, boolean z) {
    }

    /* access modifiers changed from: protected */
    public boolean initializeGoogleAr() {
        if (this.n != null || currentActivity == null || !l()) {
            return false;
        }
        this.n = new GoogleARCoreApi();
        this.n.initializeARCore(currentActivity);
        if (this.f.d()) {
            return false;
        }
        this.n.resumeARCore();
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean initializeGoogleVr() {
        final GoogleVrProxy b2 = GoogleVrApi.b();
        if (b2 == null) {
            GoogleVrApi.a(this);
            b2 = GoogleVrApi.b();
            if (b2 == null) {
                g.Log(6, "Unable to create Google VR subsystem.");
                return false;
            }
        }
        final Semaphore semaphore = new Semaphore(0);
        final AnonymousClass13 r3 = new Runnable() {
            public final void run() {
                UnityPlayer.this.injectEvent(new KeyEvent(0, 4));
                UnityPlayer.this.injectEvent(new KeyEvent(1, 4));
            }
        };
        a((Runnable) new Runnable() {
            public final void run() {
                if (!b2.a(UnityPlayer.currentActivity, UnityPlayer.this.q, UnityPlayer.this.c(), r3)) {
                    g.Log(6, "Unable to initialize Google VR subsystem.");
                }
                if (UnityPlayer.currentActivity != null) {
                    b2.a(UnityPlayer.currentActivity.getIntent());
                }
                semaphore.release();
            }
        });
        try {
            if (semaphore.tryAcquire(4, TimeUnit.SECONDS)) {
                return b2.a();
            }
            g.Log(5, "Timeout while trying to initialize Google VR.");
            return false;
        } catch (InterruptedException e2) {
            g.Log(5, "UI thread was interrupted while initializing Google VR. " + e2.getLocalizedMessage());
            return false;
        }
    }

    public boolean injectEvent(InputEvent inputEvent) {
        if (!n.c()) {
            return false;
        }
        return nativeInjectEvent(inputEvent);
    }

    /* access modifiers changed from: protected */
    public boolean isFinishing() {
        if (!this.s) {
            Context context = this.q;
            boolean z = (context instanceof Activity) && ((Activity) context).isFinishing();
            this.s = z;
            return z;
        }
    }

    /* access modifiers changed from: protected */
    public void kill() {
        Process.killProcess(Process.myPid());
    }

    /* access modifiers changed from: protected */
    public boolean loadLibrary(String str) {
        return loadLibraryStatic(str);
    }

    public void lowMemory() {
        if (n.c()) {
            b((Runnable) new Runnable() {
                public final void run() {
                    UnityPlayer.this.nativeLowMemory();
                }
            });
        }
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return injectEvent(motionEvent);
    }

    public boolean onKeyDown(int i2, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onKeyLongPress(int i2, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onKeyMultiple(int i2, int i3, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onKeyUp(int i2, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return injectEvent(motionEvent);
    }

    public void pause() {
        GoogleARCoreApi googleARCoreApi = this.n;
        if (googleARCoreApi != null) {
            googleARCoreApi.pauseARCore();
        }
        q qVar = this.t;
        if (qVar != null) {
            qVar.a();
        }
        GoogleVrProxy b2 = GoogleVrApi.b();
        if (b2 != null) {
            b2.pauseGvrLayout();
        }
        f();
    }

    public void quit() {
        destroy();
    }

    public void removeViewFromPlayer(View view) {
        a((View) this.r, view);
        boolean z = true;
        boolean z2 = view.getParent() == null;
        boolean z3 = this.r.getParent() == this;
        if (!z2 || !z3) {
            z = false;
        }
        if (!z) {
            if (!z2) {
                g.Log(6, "removeViewFromPlayer: Failure removing view from hierarchy");
            }
            if (!z3) {
                g.Log(6, "removeVireFromPlayer: Failure agging old view to hierarchy");
            }
        }
    }

    public void reportError(String str, String str2) {
        g.Log(6, str + ": " + str2);
    }

    /* access modifiers changed from: protected */
    public void reportSoftInputSelection(final int i2, final int i3) {
        a((f) new f() {
            public final void a() {
                UnityPlayer.this.nativeSetInputSelection(i2, i3);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void reportSoftInputStr(final String str, final int i2, final boolean z) {
        if (i2 == 1) {
            hideSoftInput();
        }
        a((f) new f() {
            public final void a() {
                if (z) {
                    UnityPlayer.this.nativeSoftInputCanceled();
                } else {
                    String str = str;
                    if (str != null) {
                        UnityPlayer.this.nativeSetInputString(str);
                    }
                }
                if (i2 == 1) {
                    UnityPlayer.this.nativeSoftInputClosed();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void requestUserAuthorization(String str) {
        if (j.c && str != null && !str.isEmpty() && currentActivity != null) {
            j.d.a(currentActivity, str);
        }
    }

    public void resume() {
        GoogleARCoreApi googleARCoreApi = this.n;
        if (googleARCoreApi != null) {
            googleARCoreApi.resumeARCore();
        }
        this.f.b(false);
        q qVar = this.t;
        if (qVar != null) {
            qVar.b();
        }
        h();
        nativeRestartActivityIndicator();
        GoogleVrProxy b2 = GoogleVrApi.b();
        if (b2 != null) {
            b2.b();
        }
    }

    /* access modifiers changed from: protected */
    public void setCharacterLimit(final int i2) {
        a((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null) {
                    UnityPlayer.this.b.a(i2);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setClipboardText(String str) {
        this.l.setPrimaryClip(ClipData.newPlainText("Text", str));
    }

    /* access modifiers changed from: protected */
    public void setHideInputField(final boolean z) {
        a((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null) {
                    UnityPlayer.this.b.a(z);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setSelection(final int i2, final int i3) {
        a((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null) {
                    UnityPlayer.this.b.a(i2, i3);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void setSoftInputStr(final String str) {
        a((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null && str != null) {
                    UnityPlayer.this.b.a(str);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void showSoftInput(String str, int i2, boolean z, boolean z2, boolean z3, boolean z4, String str2, int i3, boolean z5) {
        final String str3 = str;
        final int i4 = i2;
        final boolean z6 = z;
        final boolean z7 = z2;
        final boolean z8 = z3;
        final boolean z9 = z4;
        final String str4 = str2;
        final int i5 = i3;
        final boolean z10 = z5;
        a((Runnable) new Runnable() {
            public final void run() {
                UnityPlayer unityPlayer = UnityPlayer.this;
                unityPlayer.b = new k(unityPlayer.q, this, str3, i4, z6, z7, z8, str4, i5, z10);
                UnityPlayer.this.b.show();
            }
        });
    }

    /* access modifiers changed from: protected */
    public boolean showVideoPlayer(String str, int i2, int i3, int i4, boolean z, int i5, int i6) {
        if (this.t == null) {
            this.t = new q(this);
        }
        boolean a2 = this.t.a(this.q, str, i2, i3, i4, z, (long) i5, (long) i6, new q.a() {
            public final void a() {
                q unused = UnityPlayer.this.t = null;
            }
        });
        if (a2) {
            a((Runnable) new Runnable() {
                public final void run() {
                    if (UnityPlayer.this.nativeIsAutorotationOn() && (UnityPlayer.this.q instanceof Activity)) {
                        ((Activity) UnityPlayer.this.q).setRequestedOrientation(UnityPlayer.this.c);
                    }
                }
            });
        }
        return a2;
    }

    /* access modifiers changed from: protected */
    public boolean skipPermissionsDialog() {
        if (!j.c || currentActivity == null) {
            return false;
        }
        return j.d.a(currentActivity);
    }

    public void start() {
    }

    public void stop() {
    }

    /* access modifiers changed from: protected */
    public void toggleGyroscopeSensor(boolean z) {
        SensorManager sensorManager = (SensorManager) this.q.getSystemService("sensor");
        Sensor defaultSensor = sensorManager.getDefaultSensor(11);
        if (z) {
            sensorManager.registerListener(this.o, defaultSensor, 1);
        } else {
            sensorManager.unregisterListener(this.o);
        }
    }

    public void windowFocusChanged(boolean z) {
        this.f.a(z);
        if (this.f.e()) {
            if (z && this.b != null) {
                nativeSoftInputLostFocus();
                reportSoftInputStr((String) null, 1, false);
            }
            if (z) {
                this.a.c();
            } else {
                this.a.d();
            }
            h();
        }
    }
}
