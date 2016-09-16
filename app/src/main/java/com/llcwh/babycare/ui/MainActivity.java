package com.llcwh.babycare.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.llcwh.babycare.CoreService;
import com.llcwh.babycare.R;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.BindInfo;
import com.llcwh.babycare.model.BindInfoData;
import com.llcwh.babycare.model.BluetoothStatus;
import com.llcwh.babycare.model.LocationData;
import com.llcwh.babycare.model.LocationResponse;
import com.llcwh.babycare.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.llcwh.babycare.Const.bindIds;
import static com.llcwh.babycare.Const.connectedId;

@RuntimePermissions
public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_my_location)
    TextView tv_my_location;
    @BindView(R.id.tv_baby_location)
    TextView tv_baby_location;
    @BindView(R.id.tv_go_map)
    TextView tv_go_map;
    @BindView(R.id.tv_has_bind)
    TextView tv_has_bind;
    @BindView(R.id.tv_bind)
    TextView tv_bind;
    @BindView(R.id.btn_refresh_location)
    Button btn_refresh;
    @BindView(R.id.btn_refresh_bind)
    Button btn_refresh_bind;

    LocationData locationData;
    BindInfo bindInfo;
    AMapLocation mAMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        refreshBind();
        MainActivityPermissionsDispatcher.refreshLocationWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @OnClick(R.id.btn_refresh_location)
    public void refreshLocation() {
        startService(new Intent(this, CoreService.class));
        LlcService.getApi().getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LocationData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(LocationData locationData1) {
                        if (locationData1.isStatus()) {
                            ArrayList<LocationResponse> locationResponses = locationData1.getLocationResponses();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (LocationResponse locationResponse : locationResponses) {
                                String address = locationResponse.getAddress();
                                if (TextUtils.isEmpty(address)) {
                                    address = "[" + locationResponse.getLat() + "," + locationResponse.getLng() + "]";
                                }
                                stringBuilder.append("您的" + locationResponse.getNickname() + "在" + address + "(" + locationResponse.getLast_time() + ")\n");
                            }
                            tv_baby_location.setText(stringBuilder.toString());
                            locationData = locationData1;
                        }
                        showToast(locationData1.getMsg());
                    }
                });
    }

    @OnClick(R.id.tv_go_map)
    public void goMap() {
        if (locationData == null || mAMapLocation == null) {
            showToast("暂时没有位置信息，请刷新试试");
            return;
        }
        startActivity(new Intent(this, MapActivity.class).putExtra("end", locationData).putExtra("start", mAMapLocation));
    }

    @OnClick(R.id.btn_refresh_bind)
    public void refreshBind() {
        LlcService.getApi().getBindInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BindInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BindInfo bindInfo1) {
                        if (bindInfo1.isStatus()) {
                            bindInfo = bindInfo1;
                            showBindData();
                        }
                        showToast(bindInfo1.getMsg());
                    }
                });
    }

    private void showBindData() {
        if (bindInfo != null) {
            ArrayList<BindInfoData> bindInfoDatas = bindInfo.getData();
            StringBuilder stringBuilder = new StringBuilder();
            bindIds = new ArrayList<>();
            for (BindInfoData bindInfoData : bindInfoDatas) {
                bindIds.add(bindInfoData.getBaby_uuid());
                stringBuilder.append("已绑定" + bindInfoData.getNickname() + "(" + bindInfoData.getRelationship() + ")");
                if (connectedId != null && connectedId.equals(bindInfoData.getBaby_uuid())) {
                    stringBuilder.append("(已连接)");
                } else {
                    stringBuilder.append("(未连接)");
                }
                stringBuilder.append("\n");
            }
            tv_has_bind.setText(stringBuilder);
        }
    }

    @OnClick(R.id.tv_bind)
    public void bind() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("当前手机不支持BLE,无法绑定");
            return;
        }
        startActivity(new Intent(this, BindActivity.class));
    }

    @Subscribe
    public void receiveLocation(AMapLocation aMapLocation) {
        mAMapLocation = aMapLocation;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(aMapLocation.getTime());
        String address = aMapLocation.getAddress();
        if (TextUtils.isEmpty(address)) {
            address = "[" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude() + "]";
        }
        tv_my_location.setText("您在" + address + "(" + df.format(date) + ")");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBlutooth(BluetoothStatus bluetoothStatus) {
        showBindData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForPhoneState(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("需要位置和读写权限进行定位")
                .setPositiveButton("允许", (dialog, button) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForCamera() {
        Toast.makeText(this, "位置和读写权限被拒绝，将无法使用", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        Toast.makeText(this, "位置和读写权限已被永久拒绝，将无法使用，请到设置里打开权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
