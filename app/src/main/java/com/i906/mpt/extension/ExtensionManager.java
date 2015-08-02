package com.i906.mpt.extension;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.i906.mpt.Manifest;
import com.i906.mpt.view.DefaultPrayerView;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dalvik.system.PathClassLoader;
import timber.log.Timber;

@Singleton
public class ExtensionManager {

    public static final String EXTENSION_PERMISSION = Manifest.permission.MPT_EXTENSION;
    public static final String EXTENSION_METADATA = "com.i906.mpt.extension.ExtensionInfo";
    public static final String EXTENSION_VERSION = "com.i906.mpt.extension.version";

    @Inject
    protected Context mContext;

    @Inject
    protected PackageManager mPackageManager;

    @Inject
    public ExtensionManager() {
    }

    public List<ExtensionInfo> getDefaultExtensions() {
        List<ExtensionInfo> de = new ArrayList<>();

        ExtensionInfo dei = new ExtensionInfo();
        dei.name = "Default Extension";
        dei.author = "Noorzaini Ilhami";
        dei.screens = new ArrayList<>();

        ExtensionInfo.Screen ds = new ExtensionInfo.Screen();
        ds.isNative = true;
        ds.name = "MPT Original";
        ds.view = DefaultPrayerView.class.getCanonicalName();
        ds.nativeView = DefaultPrayerView.class;

        dei.screens.add(ds);
        de.add(dei);

        return de;
    }

    public List<ExtensionInfo> getExtensions() {
        List<ExtensionInfo> el = getDefaultExtensions();

        List<PackageInfo> packages = mPackageManager.getInstalledPackages(
                PackageManager.GET_PERMISSIONS | PackageManager.GET_META_DATA);

        for (PackageInfo pi : packages) {
            String[] perms = pi.requestedPermissions;

            if (perms != null && Arrays.asList(perms).contains(EXTENSION_PERMISSION)) {
                XmlResourceParser xrp = pi.applicationInfo.loadXmlMetaData(mPackageManager,
                        EXTENSION_METADATA);

                int version = pi.applicationInfo.metaData.getInt(EXTENSION_VERSION, -1);

                ExtensionInfo ei = parseExtensionInfo(pi.packageName, xrp);
                if (ei != null) {
                    ei.version = version;
                    el.add(ei);
                }
            }
        }

        return el;
    }

    public List<ExtensionInfo.Screen> getScreens() {
        List<ExtensionInfo> el = getExtensions();
        List<ExtensionInfo.Screen> screens = new ArrayList<>();

        for (ExtensionInfo ei : el) {
            for (ExtensionInfo.Screen s : ei.getScreens()) {
                if (s == null) continue;
                if (s.getView() == null) continue;
                screens.add(s);
            }
        }

        return screens;
    }

    @Nullable
    public PrayerView getPrayerView(String screenView) {
        List<ExtensionInfo.Screen> screens = getScreens();

        for (ExtensionInfo.Screen s : screens) {
            if (s.getView().equals(screenView)) return getPrayerView(s);
        }

        return null;
    }

    @Nullable
    public PrayerView getPrayerView(ExtensionInfo.Screen screen) {
        if (screen.isNative()) {
            return createNativePrayerView(screen);
        } else {
            return createExtensionPrayerView(screen);
        }
    }

    @Nullable
    private PrayerView createNativePrayerView(ExtensionInfo.Screen screen) {
        try {
            Constructor c = screen.getNativeView().getConstructor(Context.class);
            return (PrayerView) c.newInstance(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private PrayerView createExtensionPrayerView(ExtensionInfo.Screen screen) {
        try {
            String apkName = mPackageManager.getApplicationInfo(screen.apk, 0).sourceDir;
            PathClassLoader myClassLoader = new PathClassLoader(apkName,
                    PrayerView.class.getClassLoader());

            try {
                Class<?> handler = Class.forName(screen.view, true, myClassLoader);
                Constructor c = handler.getConstructor(Context.class);
                Context extensionContext = mContext.createPackageContext(screen.apk,
                        Context.CONTEXT_RESTRICTED);
                return (PrayerView) c.newInstance(extensionContext);
            } catch (IncompatibleClassChangeError icce) {
                Timber.e("Extension %s is incompatible with this version of MPT.", screen.getName());
            }
        } catch (Exception e) {
            Timber.e(e, "Unable to create extension %s.", screen.getName());
        }

        return null;
    }

    @Nullable
    private ExtensionInfo parseExtensionInfo(String packageName, XmlResourceParser xrp) {
        if (xrp == null) return null;
        ExtensionInfo ei = new ExtensionInfo();
        ei.apk = packageName;

        try {
            int et = xrp.getEventType();
            String currentTag;
            String screenName = null;
            String screenView = null;
            String screenSettings = null;

            while (et != XmlPullParser.END_DOCUMENT) {
                if (et == XmlPullParser.START_TAG) {
                    currentTag = xrp.getName();
                    if ("mpt-extension".equals(currentTag)) {
                        ei.name = xrp.getAttributeValue(null, "name");
                        ei.author = xrp.getAttributeValue(null, "author");
                    }
                    if ("screen".equals(currentTag)) {
                        screenName = xrp.getAttributeValue(null, "name");
                        screenView = xrp.getAttributeValue(null, "view");
                        screenSettings = xrp.getAttributeValue(null, "settings");
                    }
                } else if (et == XmlPullParser.END_TAG) {
                    if ("screen".equals(xrp.getName())) {
                        ExtensionInfo.Screen s = new ExtensionInfo.Screen();
                        s.apk = packageName;
                        s.name = screenName;
                        s.view = screenView;
                        s.settings = screenSettings;
                        ei.screens.add(s);
                    }
                }
                et = xrp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return ei;
    }

    public void uninstallExtension(Context context, ExtensionInfo extension) {
        Uri packageUri = Uri.parse("package:" + extension.getApk());
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        context.startActivity(uninstallIntent);
    }
}
