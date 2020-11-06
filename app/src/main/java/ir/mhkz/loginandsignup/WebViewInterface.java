package ir.mhkz.loginandsignup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import ir.mhkz.loginandsignup.MainActivity;

import static android.content.Context.TELEPHONY_SERVICE;

public class WebViewInterface {
    public WebView mAppView;
    public Activity mContext;
    public String DeviceLocaion;

    public WebViewInterface(Activity activity, WebView view) {
        mAppView = view;
        mContext = activity;
    }

    @JavascriptInterface
    public void normal(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void normal2(String message) { // Show toast for a short time
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void normal3() {// 민감한 정보 다루지만 @Javascriptinterface 어노테이션 없음
        TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String PhoneNum = telManager.getLine1Number();
        if(PhoneNum.startsWith("+82")){
            PhoneNum = PhoneNum.replace("+82", "0");
        }
    }

    @JavascriptInterface
    public void vuln1() {//Case1 : 민감한 내용을 다루는 메소드 호출
        startLocationService();
    }

    @JavascriptInterface
    public void vuln2() {//Case2 : 함수에서 직접 민감한 정보 다룸
        TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String PhoneNum = telManager.getLine1Number();
        if(PhoneNum.startsWith("+82")){
            PhoneNum = PhoneNum.replace("+82", "0");
        }
    }

    @JavascriptInterface
    public String vuln3() {//Case3 : 전역변수에서 민감한 정보 받음
        String a = "My Location : ";
        String b = DeviceLocaion;

        return a.concat(b);
    }

    @SuppressLint("WrongConstant")
    public void startLocationService() {

        // get manager instance
        LocationManager manager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

        // set listener
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                gpsListener);

        Toast.makeText(mContext, "Location Service started.\nyou can test using DDMS.", 2000).show();
    }


    private class GPSListener implements LocationListener {

        @SuppressLint("WrongConstant")
        public void onLocationChanged(Location location) {
            //capture location data sent by current provider
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            DeviceLocaion = msg;
            Log.i("GPSLocationService", msg);
            Toast.makeText(mContext, msg, 2000).show();

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

}
