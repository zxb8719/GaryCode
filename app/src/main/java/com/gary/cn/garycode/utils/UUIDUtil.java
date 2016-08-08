package com.gary.cn.garycode.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 跟手机唯一标识码的辅助类
 */
public class UUIDUtil
{
    private static final String TAG = UUIDUtil.class.getSimpleName();
    private UUIDUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    public static String getUUID(Context context)
    {
        String uuidStr = "";
        try {
            uuidStr = getIMEI(context)+getWifiMac(context);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        LogUtil.d(TAG, "uuidStr:"+uuidStr);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(uuidStr.getBytes(),0,uuidStr.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i=0;i<p_md5Data.length;i++) {
            int b =  (0xFF & p_md5Data[i]);
        // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID+="0";
        // add number to string
            m_szUniqueID+=Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();

        LogUtil.d(TAG, "m_szUniqueID:"+m_szUniqueID);

        return  uuidStr;
    }

    /**
     * 获取IMEI标识
     *
     * 1、需要在AndroidManifest.xml中加入一个许可：android.permission.READ_PHONE_STATE，并且用户应当允许安装此应用
     *
     * 会根据不同的手机设备返回IMEI，MEID或者ESN码，但在使用的过程中有以下问题：

     1、非手机设备：最开始搭载Android系统都手机设备，而现在也出现了非手机设备：如平板电脑、电子书、电视、音乐播放器等。这些设备没有通话的硬件功能，
        系统中也就没有TELEPHONY_SERVICE，自然也就无法通过上面的方法获得DEVICE_ID。
     2、权限问题：获取DEVICE_ID需要READ_PHONE_STATE权限，如果只是为了获取DEVICE_ID而没有用到其他的通话功能，申请这个权限一来大才小用，
        二来部分用户会怀疑软件的安全性。
     3、厂商定制系统中的Bug：少数手机设备上，由于该实现有漏洞，会返回垃圾，如:zeros或者asterisks
     * @param context
     * @return
     */
    public static  String getIMEI(Context context) throws Exception
    {
        String imeiStr = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imeiStr = tm.getDeviceId();
        LogUtil.d(TAG, "imeiStr:"+imeiStr.toString());
        return imeiStr.toString();
    }

    /**
     * 获取蓝牙的MAC地址
     *  只在有蓝牙的设备上运行。并且要加入android.permission.BLUETOOTH 权限.
     */
    public static String getBluetoothMac() throws Exception
    {
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMac = m_BluetoothAdapter.getAddress();
        LogUtil.d(TAG, "bluetoothMac:"+bluetoothMac);
        return bluetoothMac;
    }

    /**
     * 获取序列号
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context) throws Exception
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String serialNumber = tm.getSimSerialNumber();
        LogUtil.d(TAG, "getSimSerialNumber:"+serialNumber);
        return serialNumber;
    }

    /**
     *  获取WIFI的MAC地址
     * 需要权限<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * @param context
     * @return
     */
    public static String getWifiMac(Context context) throws Exception
    {
        //wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiMac = wifi.getConnectionInfo().getMacAddress();
        LogUtil.d(TAG, "wifiMac:"+wifiMac.toString());
        return wifiMac.toString();
    }

}