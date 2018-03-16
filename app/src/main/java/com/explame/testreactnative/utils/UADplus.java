package com.explame.testreactnative.utils;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XQ on 2017/12/2.
 * 集成 友盟UADplus功能 调用Appkey
 */
public class UADplus {

    private static final Pattern pattern = Pattern.compile("UTDID\">([^<]+)");
    private Context mContext = null;
    private String mAppkey = null;

    /**
     * * send message To Server * * @param context * android Context. * @param*
     * appkey * umeng appkey
     *
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("deprecation")
    public void sendMessage(Context context, String appkey) {
        mContext = context;
        mAppkey = appkey;
        new Thread(mNetrequest).start();
    }

    private Runnable mNetrequest = new Runnable() {

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                StringBuilder sber = new StringBuilder();
                sber.append("https://ar.umeng.com/stat.htm?");
                if (!TextUtils.isEmpty(mAppkey)) {
                    sber.append("ak=").append(mAppkey);
                }

                String devicename = Build.MODEL;

                if (!TextUtils.isEmpty(devicename)) {
                    sber.append("&device_name=").append(URLEncoder.encode(devicename, "UTF-8"));
                }

                TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                PackageManager pm = mContext.getPackageManager();

                if (pm.checkPermission(permission.READ_PHONE_STATE,
                        mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                    String imei = tm.getDeviceId();

                    if (!TextUtils.isEmpty(imei)) {
                        sber.append("&imei=").append(URLEncoder.encode(imei, "UTF-8"));
                    }
                }

                if (pm.checkPermission(permission.ACCESS_WIFI_STATE,
                        mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED) {

                    WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifi.getConnectionInfo();
                    String mac = info.getMacAddress();

                    // String mac = tm.getDeviceId();
                    if (!TextUtils.isEmpty(mac)) {
                        sber.append("&mac=").append(URLEncoder.encode(mac, "UTF-8"));
                    }
                }

                String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
                if (!TextUtils.isEmpty(androidId)) {
                    sber.append("&android_id=").append(URLEncoder.encode(androidId, "UTF-8"));
                }

                String utdid = getUTDid(mContext);
                if (!TextUtils.isEmpty(utdid)) {
                    sber.append("&utdid=").append(URLEncoder.encode(utdid, "UTF-8"));
                }

                String[] networkType = getNetworkAccessMode(mContext);
                if (networkType != null) {
                    sber.append("&access=").append(URLEncoder.encode(networkType[0], "UTF-8"));
                    sber.append("&access_subtype=").append(URLEncoder.encode(networkType[1], "UTF-8"));
                }

                String resolution = getDisplayResolution(mContext);
                if (!TextUtils.isEmpty(resolution)) {
                    sber.append("&resolution=").append(URLEncoder.encode(resolution, "UTF-8"));
                }

                String carrier = getNetworkOperatorName(mContext);
                if (!TextUtils.isEmpty(carrier)) {
                    sber.append("&carrier=").append(URLEncoder.encode(carrier, "UTF-8"));
                }


                sber.append("&os=").append(URLEncoder.encode("android", "UTF-8"));
                String version = Build.VERSION.RELEASE;
                if (!TextUtils.isEmpty(version)) {
                    sber.append("&os_version=").append(URLEncoder.encode(version, "UTF-8"));
                }

                conn = (HttpURLConnection) new URL(sber.toString()).openConnection();

                conn.setRequestMethod("GET");
                conn.setConnectTimeout(30 * 1000);
                conn.setReadTimeout(30 * 1000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
                    System.setProperty("http.keepAlive", "false");
                }
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = conn.getInputStream();

                    // do something
                } else {
                    Log.e("test", "status code: " + code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

        }
    };

    /**
     * * get UTDID * * @param context * @return
     */
    private String getUTDid(Context context) {
        try {
            Class<?> utdevice = Class.forName("com.ut.device.UTDevice");
            Method reMethod = utdevice.getMethod("getUtdid", Context.class);
            return (String) reMethod.invoke(null, context);
        } catch (Exception e) {
            return readNativeFile(context);
        }
    }

    /**
     * Get the mobile network access mode.
     *
     * @param context
     * @return A 2-elements String array, 1st specifies the network type, the
     * 2nd specifies the network subtype. If the network cannot be
     * retrieved, "Unknown" is filled instead.
     * require <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     */
    public static String[] getNetworkAccessMode(Context context) {
        final String Unknown = "";
        final String WIFI = "Wifi";
        final String _2G_3G = "2G/3G";

        String[] res = new String[]{"", ""};

        try {
            if (!checkPermission(context, permission.ACCESS_NETWORK_STATE)) {
                res[0] = Unknown;
                return res;
            }

            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                res[0] = Unknown;
                return res;
            } else {
                NetworkInfo wifi_network = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifi_network != null) {
                    if (wifi_network.getState() == NetworkInfo.State.CONNECTED) {
                        res[0] = WIFI;
                        return res;
                    }
                }
                NetworkInfo mobile_network = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobile_network != null) {
                    if (mobile_network.getState() == NetworkInfo.State.CONNECTED) {
                        res[0] = _2G_3G;
                        res[1] = mobile_network.getSubtypeName();
                        return res;
                    }
                }
            }
        } catch (Throwable e) {

        }

        return res;
    }

    /**
     * get resolution
     *
     * @param context
     * @return e.g.:800*480
     */
    public static String getDisplayResolution(Context context) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
            wm.getDefaultDisplay().getMetrics(metrics);

            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            String resolution = String.valueOf(height) + "*" + String.valueOf(width);
            return resolution;
        } catch (Throwable e) {

            return "";
        }
    }

    /**
     * check permission
     *
     * @param context
     * @param permission <code>android.permission.INTERNET</code>
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Throwable e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }


    /**
     * get carrier
     *
     * @param context
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (checkPermission(context, permission.READ_PHONE_STATE)) {
                if (tm != null)
                    return tm.getNetworkOperatorName();
            }
        } catch (Throwable e) {
        }
        return "";
    }

    /**
     * * read native file method * * @param context * @return
     */
    private String readNativeFile(Context context) {
        try {
            File f = getFile(context);
            if (f == null || !f.exists()) {
                return null;
            }

            FileInputStream fis = new FileInputStream(f);
            try {
                return parseId(readStreamToString(fis));
            } finally {
                safeClose(fis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * * java pattern parse native utdid * * @param content * @return
     */
    private String parseId(String content) {
        if (content == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * read native file * * @param context * @return
     */

    private File getFile(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm.checkPermission(permission.WRITE_EXTERNAL_STORAGE,
                context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();
            try {
                return new File(sdCardDir.getCanonicalPath(), ".UTSystemConfig/Global/Alvin2.xml");
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    /**
     * * convert inputstream to String * * @param input * @return * @throws
     * IOException
     */
    private String readStreamToString(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        char[] buffer = new char[1024];
        int n = 0;
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);

        }
        return writer.toString();
    }

    /**
     * * close io stream * * @param is
     */
    private void safeClose(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

}
