package com.unity3d.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

public final class l extends View {
    final int a;
    final int b = getResources().getIdentifier("unity_static_splash", "drawable", getContext().getPackageName());
    Bitmap c;
    Bitmap d;

    /* renamed from: com.unity3d.player.l$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a = new int[a.a().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0011 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x0019 */
        static {
            /*
                int[] r0 = com.unity3d.player.l.a.a()
                int r0 = r0.length
                int[] r0 = new int[r0]
                a = r0
                r0 = 1
                int[] r1 = a     // Catch:{ NoSuchFieldError -> 0x0011 }
                int r2 = com.unity3d.player.l.a.a     // Catch:{ NoSuchFieldError -> 0x0011 }
                int r2 = r2 - r0
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x0011 }
            L_0x0011:
                int[] r1 = a     // Catch:{ NoSuchFieldError -> 0x0019 }
                int r2 = com.unity3d.player.l.a.b     // Catch:{ NoSuchFieldError -> 0x0019 }
                int r2 = r2 - r0
                r3 = 2
                r1[r2] = r3     // Catch:{ NoSuchFieldError -> 0x0019 }
            L_0x0019:
                int[] r1 = a     // Catch:{ NoSuchFieldError -> 0x0021 }
                int r2 = com.unity3d.player.l.a.c     // Catch:{ NoSuchFieldError -> 0x0021 }
                int r2 = r2 - r0
                r0 = 3
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x0021 }
            L_0x0021:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.unity3d.player.l.AnonymousClass1.<clinit>():void");
        }
    }

    enum a {
        ;

        static {
            d = new int[]{a, b, c};
        }

        public static int[] a() {
            return (int[]) d.clone();
        }
    }

    public l(Context context, int i) {
        super(context);
        this.a = i;
        if (this.b != 0) {
            forceLayout();
        }
    }

    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Bitmap bitmap = this.c;
        if (bitmap != null) {
            bitmap.recycle();
            this.c = null;
        }
        Bitmap bitmap2 = this.d;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.d = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006b, code lost:
        if (r13 < r11) goto L_0x006d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0075  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
        /*
            r8 = this;
            int r9 = r8.b
            if (r9 != 0) goto L_0x0005
            return
        L_0x0005:
            android.graphics.Bitmap r9 = r8.c
            r10 = 0
            if (r9 != 0) goto L_0x001d
            android.graphics.BitmapFactory$Options r9 = new android.graphics.BitmapFactory$Options
            r9.<init>()
            r9.inScaled = r10
            android.content.res.Resources r11 = r8.getResources()
            int r12 = r8.b
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeResource(r11, r12, r9)
            r8.c = r9
        L_0x001d:
            android.graphics.Bitmap r9 = r8.c
            int r9 = r9.getWidth()
            android.graphics.Bitmap r11 = r8.c
            int r11 = r11.getHeight()
            int r12 = r8.getWidth()
            int r13 = r8.getHeight()
            if (r12 == 0) goto L_0x00cc
            if (r13 != 0) goto L_0x0037
            goto L_0x00cc
        L_0x0037:
            float r0 = (float) r9
            float r1 = (float) r11
            float r0 = r0 / r1
            float r1 = (float) r12
            float r2 = (float) r13
            float r3 = r1 / r2
            r4 = 1
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 > 0) goto L_0x0045
            r3 = 1
            goto L_0x0046
        L_0x0045:
            r3 = 0
        L_0x0046:
            int[] r5 = com.unity3d.player.l.AnonymousClass1.a
            int r6 = r8.a
            int r6 = r6 - r4
            r5 = r5[r6]
            r6 = 2
            if (r5 == r4) goto L_0x0066
            if (r5 == r6) goto L_0x0056
            r7 = 3
            if (r5 == r7) goto L_0x0056
            goto L_0x0071
        L_0x0056:
            int r9 = r8.a
            int r11 = com.unity3d.player.l.a.c
            if (r9 != r11) goto L_0x005e
            r9 = 1
            goto L_0x005f
        L_0x005e:
            r9 = 0
        L_0x005f:
            r9 = r9 ^ r3
            if (r9 == 0) goto L_0x006d
            float r1 = r1 / r0
            int r11 = (int) r1
            r9 = r12
            goto L_0x0071
        L_0x0066:
            if (r12 >= r9) goto L_0x006b
            float r1 = r1 / r0
            int r11 = (int) r1
            r9 = r12
        L_0x006b:
            if (r13 >= r11) goto L_0x0071
        L_0x006d:
            float r2 = r2 * r0
            int r9 = (int) r2
            r11 = r13
        L_0x0071:
            android.graphics.Bitmap r12 = r8.d
            if (r12 == 0) goto L_0x0090
            int r12 = r12.getWidth()
            if (r12 != r9) goto L_0x0084
            android.graphics.Bitmap r12 = r8.d
            int r12 = r12.getHeight()
            if (r12 != r11) goto L_0x0084
            return
        L_0x0084:
            android.graphics.Bitmap r12 = r8.d
            android.graphics.Bitmap r13 = r8.c
            if (r12 == r13) goto L_0x0090
            r12.recycle()
            r12 = 0
            r8.d = r12
        L_0x0090:
            android.graphics.Bitmap r12 = r8.c
            android.graphics.Bitmap r9 = android.graphics.Bitmap.createScaledBitmap(r12, r9, r11, r4)
            r8.d = r9
            android.graphics.Bitmap r9 = r8.d
            android.content.res.Resources r11 = r8.getResources()
            android.util.DisplayMetrics r11 = r11.getDisplayMetrics()
            int r11 = r11.densityDpi
            r9.setDensity(r11)
            android.graphics.drawable.ColorDrawable r9 = new android.graphics.drawable.ColorDrawable
            r11 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r9.<init>(r11)
            android.graphics.drawable.BitmapDrawable r11 = new android.graphics.drawable.BitmapDrawable
            android.content.res.Resources r12 = r8.getResources()
            android.graphics.Bitmap r13 = r8.d
            r11.<init>(r12, r13)
            r12 = 17
            r11.setGravity(r12)
            android.graphics.drawable.LayerDrawable r12 = new android.graphics.drawable.LayerDrawable
            android.graphics.drawable.Drawable[] r13 = new android.graphics.drawable.Drawable[r6]
            r13[r10] = r9
            r13[r4] = r11
            r12.<init>(r13)
            r8.setBackground(r12)
        L_0x00cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.unity3d.player.l.onLayout(boolean, int, int, int, int):void");
    }
}
