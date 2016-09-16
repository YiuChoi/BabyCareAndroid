package com.llcwh.babycare.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.llcwh.babycare.R;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.Baby;
import com.llcwh.babycare.model.BluetoothStatus;
import com.llcwh.babycare.model.CommonResponse;
import com.llcwh.babycare.ui.adapter.BluetoothListAdapter;
import com.llcwh.babycare.ui.base.BaseActivity;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.llcwh.babycare.Const.bindIds;
import static com.llcwh.babycare.Const.connectedId;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class BindActivity extends BaseActivity implements BluetoothListAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.rv_bluetooth)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab_refresh)
    FloatingActionButton fab_refresh;

    private static final int TIME_OUT = 5000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SHOW_TOAST = 12;

    ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    BluetoothListAdapter mBluetoothListAdapter;
    BluetoothAdapter mBluetoothAdapter;
    ProgressDialog progressDialog;
    Animation mAnimation;
    boolean isInScanning;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    showToast(msg.getData().getString("msg"));
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        ButterKnife.bind(this);

        mToolbar.setTitle("连接设备");
        setSupportActionBar(mToolbar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBluetoothListAdapter = new BluetoothListAdapter(bluetoothDevices);
        mBluetoothListAdapter.setOnItemClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mBluetoothListAdapter);

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_rotate);
        mAnimation.setDuration(TIME_OUT);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        EventBus.getDefault().register(this);
        scan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
        EventBus.getDefault().unregister(this);
        bluetoothDevices.clear();
        mBluetoothListAdapter.notifyDataSetChanged();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothDevices.add(device);
                            mBluetoothListAdapter.notifyDataSetChanged();
                            if (bluetoothDevices.isEmpty()) {
                                mRecyclerView.setVisibility(View.GONE);
                                tv_info.setVisibility(View.VISIBLE);
                            } else {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                tv_info.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bluetoothDevices.contains(result.getDevice()))
                        return;
                    bluetoothDevices.add(result.getDevice());
                    mBluetoothListAdapter.notifyDataSetChanged();
                    if (bluetoothDevices.isEmpty()) {
                        mRecyclerView.setVisibility(View.GONE);
                        tv_info.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        tv_info.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("扫描失败:" + errorCode);
                }
            });
        }
    };

    @OnClick(R.id.fab_refresh)
    public void scan() {
        if (isInScanning) {
            showToast("正在扫描，请稍后...");
            return;
        }
        fab_refresh.setAnimation(mAnimation);
        mAnimation.startNow();
        bluetoothDevices.clear();
        mBluetoothListAdapter.notifyDataSetChanged();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, TIME_OUT);
        isInScanning = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        } else {
            //noinspection deprecation
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    private void stopScan() {
        isInScanning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        } else {
            //noinspection deprecation
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    public void onItemClick(View view, BluetoothDevice data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            data.setPin("123456".getBytes());
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在连接...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        EventBus.getDefault().post(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBluetoothStatus(BluetoothStatus bluetoothStatus) {
        progressDialog.dismiss();
        if (bluetoothStatus.isStatus()) {
            Message message = handler.obtainMessage();
            message.what = SHOW_TOAST;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "连接成功");
            message.setData(bundle);
            handler.sendMessage(message);
            Logger.i(bindIds.get(0));
            if (bindIds == null) {
                bind();
                return;
            }
            if (bindIds != null && !bindIds.contains(connectedId)) {
                bind();
                return;
            }
            finish();
        } else {
            Message message = handler.obtainMessage();
            message.what = SHOW_TOAST;
            Bundle bundle = new Bundle();
            bundle.putString("msg", "连接断开");
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    public void bind() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("绑定");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bind, null, false);
        alertDialog.setView(view);
        EditText et_nickname = (EditText) view.findViewById(R.id.et_nickname);
        EditText et_relation = (EditText) view.findViewById(R.id.et_relation);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确认", (dialogInterface, i) -> {
            String nickname = et_nickname.getText().toString();
            String relation = et_relation.getText().toString();
            LlcService.getApi().bind(new Baby(nickname, connectedId, relation))
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
                                finish();
                            }
                            showToast(commonResponse.getMsg());
                        }
                    });
        });
        alertDialog.show();
    }
}
