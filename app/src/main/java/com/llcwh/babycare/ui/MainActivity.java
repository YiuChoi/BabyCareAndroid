package com.llcwh.babycare.ui;

import android.Manifest;
import android.content.Intent;
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
import com.llcwh.babycare.model.Baby;
import com.llcwh.babycare.model.CommonResponse;
import com.llcwh.babycare.model.LocationResponse;
import com.llcwh.babycare.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
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
    @BindView(R.id.btn_refresh)
    Button btn_refresh;

    LocationResponse mLocationResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        MainActivityPermissionsDispatcher.refreshLocationWithCheck(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @OnClick(R.id.btn_refresh)
    public void refreshLocation() {
        startService(new Intent(this, CoreService.class));
        LlcService.getApi().getLocation(new Baby("1", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LocationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(LocationResponse locationResponse) {
                        if (locationResponse.isStatus()) {
                            tv_baby_location.setText("您的baby1在" + locationResponse.getAddress() + "(" + locationResponse.getLast_time() + ")");
                            mLocationResponse = locationResponse;
                        }
                        showToast(locationResponse.getMsg());
                    }
                });
    }

    @OnClick(R.id.tv_go_map)
    public void goMap() {
        if (mLocationResponse == null) {
            showToast("暂时没有位置信息，请刷新试试");
            return;
        }
        startActivity(new Intent(this, MapActivity.class).putExtra("data", mLocationResponse));
    }

    @OnClick(R.id.tv_bind)
    public void bind() {
        LlcService.getApi().bind(new Baby("1", "baba"))
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
                        if (commonResponse.isStatus()) {
                        }
                        showToast(commonResponse.getMsg());
                    }
                });
    }

    @Subscribe
    public void receiveLocation(AMapLocation aMapLocation) {
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
