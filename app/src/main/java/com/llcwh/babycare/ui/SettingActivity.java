package com.llcwh.babycare.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.llcwh.babycare.R;
import com.llcwh.babycare.ui.base.BaseActivity;
import com.llcwh.babycare.ui.fragment.SettingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by caiya on 2016/9/17 0017.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingFragment()).commit();

    }
}
