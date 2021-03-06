package com.llcwh.babycare;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.BluetoothStatus;
import com.llcwh.babycare.model.CommonResponse;
import com.llcwh.babycare.model.UploadLocation;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.llcwh.babycare.Const.bindIds;
import static com.llcwh.babycare.Const.connectedId;

public class CoreService extends Service implements AMapLocationListener {

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient = null;

    public CoreService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(10000);
        mLocationOption.setMockEnable(false);
        mLocationOption.setNeedAddress(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                if (!TextUtils.isEmpty(connectedId) && bindIds != null && bindIds.contains(connectedId)) {
                    LlcService.getApi().uploadLocation(new UploadLocation(String.valueOf(amapLocation.getLatitude()), String.valueOf(amapLocation.getLongitude()), amapLocation.getAddress(), connectedId))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<CommonResponse>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(CommonResponse commonResponse) {
                                    if (!commonResponse.isStatus()) {
                                        Toast.makeText(CoreService.this, commonResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Logger.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Subscribe
    public void connectBluetooth(BluetoothDevice bluetoothDevice) {
        bluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    EventBus.getDefault().post(new BluetoothStatus(true));
                    connectedId = bluetoothDevice.getAddress();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    EventBus.getDefault().post(new BluetoothStatus(false));
                    connectedId = null;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }
}
