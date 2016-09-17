package com.llcwh.babycare.ui.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.llcwh.babycare.R;
import com.llcwh.babycare.model.BabyData;
import com.llcwh.babycare.ui.MapActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.llcwh.babycare.Const.connectedId;

/**
 * Created by caiya on 2016/9/17 0017.
 */

public class BabyAdapter extends RecyclerView.Adapter<BabyAdapter.BabyViewHolder> {


    private ArrayList<BabyData> babyDatas;
    private BluetoothAdapter bluetoothAdapter;

    public BabyAdapter(ArrayList<BabyData> babyDatas, BluetoothAdapter bluetoothAdapter) {
        this.babyDatas = babyDatas;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @Override
    public BabyAdapter.BabyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.baby_card, parent, false);
        return new BabyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BabyAdapter.BabyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.cv.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
        }
        BabyData b = babyDatas.get(position);
        String address = b.getAddress();
        if (TextUtils.isEmpty(address)) {
            address = "[" + b.getLat() + "," + b.getLng() + "]";
        }
        holder.btn_add.setVisibility(b.is_admin() ? View.VISIBLE : View.GONE);
        holder.tv_baby_name.setText(b.getNickname());
        holder.tv_baby_location.setText(address + "--" + b.getLast_time() + "--" + b.getUpload_user());
        holder.tv_connection.setText((connectedId != null && connectedId.equals(b.getBaby_uuid()) ? R.string.connected : R.string.disconnected));
        holder.tv_connect.setVisibility((connectedId != null && connectedId.equals(b.getBaby_uuid()) ? View.GONE : View.VISIBLE));
        String finalAddress = address;
        holder.tv_go_map.setOnClickListener(v -> {
            if (TextUtils.isEmpty(finalAddress)) {
                Toast.makeText(holder.itemView.getContext(), "暂时没有位置信息，请刷新试试", Toast.LENGTH_SHORT).show();
                return;
            }
            holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), MapActivity.class).putExtra("start", b));
        });
        holder.tv_connect.setOnClickListener(v -> {
            if (!holder.itemView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(holder.itemView.getContext(), "当前手机不支持BLE,无法连接", Toast.LENGTH_SHORT).show();
                return;
            }
            if (bluetoothAdapter.isEnabled() && b.getBaby_uuid().contains(":")) {
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(b.getBaby_uuid());
                if (bluetoothDevice != null) {
                    EventBus.getDefault().post(bluetoothDevice);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return babyDatas.size();
    }

    public class BabyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        CardView cv;
        @BindView(R.id.tv_baby_name)
        TextView tv_baby_name;
        @BindView(R.id.tv_baby_location)
        TextView tv_baby_location;
        @BindView(R.id.tv_connection)
        TextView tv_connection;
        @BindView(R.id.tv_connect)
        TextView tv_connect;
        @BindView(R.id.tv_go_map)
        TextView tv_go_map;
        @BindView(R.id.btn_add)
        Button btn_add;

        public BabyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
