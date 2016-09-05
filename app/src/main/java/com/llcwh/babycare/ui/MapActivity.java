package com.llcwh.babycare.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.llcwh.babycare.R;
import com.llcwh.babycare.model.LocationResponse;
import com.llcwh.babycare.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class MapActivity extends BaseActivity implements LocationSource {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mv)
    MapView mMapView;

    AMap aMap;
    LocationResponse mLocationResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        mToolbar.setTitle(R.string.app_name);
        mLocationResponse = (LocationResponse) getIntent().getParcelableExtra("data");
        LatLng latLng = new LatLng(Double.valueOf(mLocationResponse.getLat()), Double.valueOf(mLocationResponse.getLng()));
        aMap.addMarker(new MarkerOptions().title(mLocationResponse.getAddress() + "(" + mLocationResponse.getLast_time() + ")").position(latLng));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel()));
        UiSettings  mUiSettings = aMap.getUiSettings();//实例化UiSettings类
        aMap.setLocationSource(this);// 设置定位监听
        mUiSettings.setMyLocationButtonEnabled(true); // 显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 可触发定位并显示定位层
        mUiSettings.setScaleControlsEnabled(true);//显示比例尺控件
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }
}
