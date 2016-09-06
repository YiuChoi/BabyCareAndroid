package com.llcwh.babycare.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.llcwh.babycare.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 蔡小木 on 2016/9/6 0006.
 */
public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.BluetoothViewHolder> implements View.OnClickListener {

    private ArrayList<BluetoothDevice> mBluetoothDevices;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(BluetoothDevice)view.getTag());
        }
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , BluetoothDevice data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public BluetoothAdapter(ArrayList<BluetoothDevice> bluetoothDevices) {
        this.mBluetoothDevices = bluetoothDevices;
    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item,parent,false);
        BluetoothViewHolder bluetoothViewHolder = new BluetoothViewHolder(view);
        view.setOnClickListener(this);
        return bluetoothViewHolder;
    }

    @Override
    public void onBindViewHolder(BluetoothViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = mBluetoothDevices.get(position);
        holder.tv_bluetooth_name.setText(bluetoothDevice.getName());
        holder.tv_bluetooth_address.setText(bluetoothDevice.getAddress());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(bluetoothDevice);
    }

    @Override
    public int getItemCount() {
        return mBluetoothDevices.size();
    }

    public class BluetoothViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_bluetooth_name)
        TextView tv_bluetooth_name;
        @BindView(R.id.tv_bluetooth_address)
        TextView tv_bluetooth_address;

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
