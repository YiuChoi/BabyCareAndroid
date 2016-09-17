package com.llcwh.babycare.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.llcwh.babycare.R;

import static com.llcwh.babycare.util.CommonUtil.logout;

/**
 * Created by caiya on 2016/9/17 0017.
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findPreference("logout").setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getActivity()).setTitle("确认")
                    .setMessage("是否确认退出")
                    .setPositiveButton("确认", (dialog, which) -> {
                        logout();
                        getActivity().finish();
                    }).setNegativeButton("取消", (dialog, which) -> {
                dialog.dismiss();
            }).show();
            return true;
        });
    }
}
