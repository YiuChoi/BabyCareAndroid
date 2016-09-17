package com.llcwh.babycare.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.llcwh.babycare.CoreService;
import com.llcwh.babycare.R;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.BabyData;
import com.llcwh.babycare.model.BluetoothStatus;
import com.llcwh.babycare.model.InfoData;
import com.llcwh.babycare.ui.adapter.BabyAdapter;
import com.llcwh.babycare.ui.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

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
import static com.llcwh.babycare.ui.BindActivity.REQUEST_ENABLE_BT;

@RuntimePermissions
public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_baby)
    RecyclerView rv_baby;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;
    @BindView(R.id.fab_add)
    FloatingActionButton fab_add;

    BabyAdapter babyAdapter;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BabyData> babyDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setRefreshing(false);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        babyAdapter = new BabyAdapter(babyDatas, mBluetoothAdapter);
        rv_baby.setHasFixedSize(true);
        rv_baby.setLayoutManager(new LinearLayoutManager(this));
        rv_baby.setAdapter(babyAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        MainActivityPermissionsDispatcher.refreshLocationWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void refreshLocation() {
        startService(new Intent(this, CoreService.class));
        LlcService.getApi().getBindInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_container.setRefreshing(false);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(InfoData locationData) {
                        swipe_container.setRefreshing(false);
                        if (locationData.isStatus()) {
                            babyDatas.clear();
                            babyDatas.addAll(locationData.getBabyDatas());
                            bindIds.clear();
                            for (BabyData babyData : locationData.getBabyDatas()) {
                                bindIds.add(babyData.getBaby_uuid());
                                if (mBluetoothAdapter.isEnabled() && babyData.getBaby_uuid().contains(":")) {
                                    BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(babyData.getBaby_uuid());
                                    if (bluetoothDevice != null) {
                                        EventBus.getDefault().post(bluetoothDevice);
                                    }
                                }
                            }
                            babyAdapter.notifyDataSetChanged();
                        }
                        showToast(locationData.getMsg());
                    }
                });
    }

    @OnClick(R.id.fab_add)
    public void add() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前手机不支持BLE,无法连接", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, BindActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBlutooth(BluetoothStatus bluetoothStatus) {
        babyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            showToast("请打开蓝牙以进行连接...");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onRefresh() {
        refreshLocation();
    }
}
