package com.llcwh.babycare.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.llcwh.babycare.CoreService;
import com.llcwh.babycare.R;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.Baby;
import com.llcwh.babycare.model.BindInfo;
import com.llcwh.babycare.model.BindInfoData;
import com.llcwh.babycare.model.CommonResponse;
import com.llcwh.babycare.model.LocationResponse;
import com.llcwh.babycare.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

import static com.llcwh.babycare.Const.bindId;
import static com.llcwh.babycare.Const.isTest;

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

    LocationResponse mLocationResponse;
    AMapLocation mAMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        refreshBind();
        MainActivityPermissionsDispatcher.refreshLocationWithCheck(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @OnClick(R.id.btn_refresh_location)
    public void refreshLocation() {
        startService(new Intent(this, CoreService.class));
        LlcService.getApi().getLocation(new Baby(bindId, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LocationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(LocationResponse locationResponse) {
                        if (locationResponse.isStatus()) {
                            String address = locationResponse.getAddress();
                            if (TextUtils.isEmpty(address)) {
                                address = "[" + locationResponse.getLat() + "," + locationResponse.getLng() + "]";
                            }
                            tv_baby_location.setText("您的baby" + bindId + "在" + address + "(" + locationResponse.getLast_time() + ")");
                            mLocationResponse = locationResponse;
                        }
                        showToast(locationResponse.getMsg());
                    }
                });
    }

    @OnClick(R.id.tv_go_map)
    public void goMap() {
        if (mLocationResponse == null || mAMapLocation == null) {
            showToast("暂时没有位置信息，请刷新试试");
            return;
        }
        startActivity(new Intent(this, MapActivity.class).putExtra("end", mLocationResponse).putExtra("start", mAMapLocation));
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
                    public void onNext(BindInfo bindInfo) {
                        if (bindInfo.isStatus()) {
                            ArrayList<BindInfoData> bindInfoDatas = bindInfo.getData();
                            StringBuilder stringBuilder = new StringBuilder();
                            for (BindInfoData bindInfoData : bindInfoDatas) {
                                stringBuilder.append("已绑定" + bindInfoData.getBaby_uuid() + "(" + bindInfoData.getRelationship() + ")\n");
                                bindId = bindInfoData.getBaby_uuid();
                            }
                            tv_has_bind.setText(stringBuilder);
                        }
                        showToast(bindInfo.getMsg());
                    }
                });
    }

    @OnClick(R.id.tv_bind)
    public void bind() {
        if (isTest) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("绑定");
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_bind, null, false);
            alertDialog.setView(view);
            EditText et_uuid = (EditText) view.findViewById(R.id.et_uuid);
            EditText et_relation = (EditText) view.findViewById(R.id.et_relation);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String uuid = et_uuid.getText().toString();
                    String relation = et_relation.getText().toString();
                    LlcService.getApi().bind(new Baby(uuid, relation))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<CommonResponse>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(CommonResponse commonResponse) {
                                    if (commonResponse.isStatus()) {
                                        refreshBind();
                                    }
                                    showToast(commonResponse.getMsg());
                                }
                            });
                }
            });
            alertDialog.show();
            return;
        }
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
