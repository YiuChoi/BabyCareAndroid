package com.llcwh.babycare.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.ListScanCallback;
import com.llcwh.babycare.R;
import com.llcwh.babycare.ui.base.BaseActivity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class BindActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_bluetooth)
    RecyclerView mRecyclerView;

    BluetoothDevice[] bluetoothDevices;
    BleManager mBleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        ButterKnife.bind(this);


        mBleManager = BleManager.getInstance();
        mBleManager.init(this);
    }

    private void scan() {
        mBleManager.scanDevice(new ListScanCallback(2000) {
            @Override
            public void onDeviceFound(BluetoothDevice[] devices) {
                Logger.i("共发现" + devices.length + "台设备");
                for (int i = 0; i < devices.length; i++) {
                    Logger.i("name:" + devices[i].getName() + "------mac:" + devices[i].getAddress());
                }
                bluetoothDevices = devices;
            }

            @Override
            public void onScanTimeout() {
                super.onScanTimeout();
                Logger.i("搜索时间结束");
            }
        });
    }

}
