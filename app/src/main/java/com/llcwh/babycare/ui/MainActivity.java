package com.llcwh.babycare.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.llcwh.babycare.CoreService;
import com.llcwh.babycare.R;

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

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void refreshLocation() {
        startService(new Intent(this, CoreService.class));
    }

    @OnClick(R.id.tv_go_map)
    public void goMap() {
        startActivity(new Intent(this, MapActivity.class));
    }

    @OnClick(R.id.tv_bind)
    public void bind() {

    }

    @Subscribe
    private void receiveLocation(AMapLocation aMapLocation) {
        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        aMapLocation.getLatitude();//获取纬度
        aMapLocation.getLongitude();//获取经度
        aMapLocation.getAccuracy();//获取精度信息
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(aMapLocation.getTime());
        tv_my_location.setText("您在" + aMapLocation.getAddress() + "(" + df.format(date) + ")");
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

}
