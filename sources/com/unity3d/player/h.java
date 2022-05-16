package com.unity3d.player;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public final class h implements e {
    private static boolean a(PackageItemInfo packageItemInfo) {
        try {
            return packageItemInfo.metaData.getBoolean("unityplayer.SkipPermissionsDialog");
        } catch (Exception unused) {
            return false;
        }
    }

    public final void a(Activity activity, String str) {
        if (activity != null && str != null) {
            FragmentManager fragmentManager = activity.getFragmentManager();
            if (fragmentManager.findFragmentByTag("96489") == null) {
                i iVar = new i();
                Bundle bundle = new Bundle();
                bundle.putString("PermissionNames", str);
                iVar.setArguments(bundle);
                FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                beginTransaction.add(0, iVar, "96489");
                beginTransaction.commit();
            }
        }
    }

    public final boolean a(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            return a((PackageItemInfo) packageManager.getActivityInfo(activity.getComponentName(), 128)) || a((PackageItemInfo) packageManager.getApplicationInfo(activity.getPackageName(), 128));
        } catch (Exception unused) {
            return false;
        }
    }
}
