package com.llcwh.babycare.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.ListScanCallback;
import com.llcwh.babycare.R;
import com.llcwh.babycare.ui.adapter.BluetoothAdapter;
import com.llcwh.babycare.ui.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class BindActivity extends BaseActivity implements BluetoothAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.rv_bluetooth)
    RecyclerView mRecyclerView;

    ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    BleManager mBleManager;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBluetoothAdapter = new BluetoothAdapter(bluetoothDevices);
        mBluetoothAdapter.setOnItemClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mBluetoothAdapter);

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
                ArrayList<BluetoothDevice> tmpBluetoothDevices = new ArrayList<>(Arrays.asList(devices));
                bluetoothDevices.clear();
                bluetoothDevices.addAll(tmpBluetoothDevices);
                mBluetoothAdapter.notifyDataSetChanged();
                if (bluetoothDevices.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    tv_info.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tv_info.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScanTimeout() {
                super.onScanTimeout();
                Logger.i("搜索时间结束");
            }
        });
    }

    @Override
    public void onItemClick(View view, BluetoothDevice data) {

    }
}
