package com.llcwh.babycare.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.llcwh.babycare.Const;
import com.llcwh.babycare.R;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.User;
import com.llcwh.babycare.ui.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.username)
    EditText et_username;
    @BindView(R.id.password)
    EditText et_password;
    @BindView(R.id.btn_sign_in)
    Button btn_sign_in;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.tv_register)
    TextView tv_register;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucent(this);

        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.tv_register)
    public void click() {
        alertDialog = new AlertDialog.Builder(this).setPositiveButton("完成注册", null).create();
        View custom = LayoutInflater.from(this).inflate(R.layout.dialog_register, null, false);
        alertDialog.setView(custom);
        final EditText et_re_username = (EditText) custom.findViewById(R.id.et_username);
        final EditText et_re_password = (EditText) custom.findViewById(R.id.et_password);
        final EditText et_replay_password = (EditText) custom.findViewById(R.id.et_replay_password);
        alertDialog.setTitle("注册");
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_re_username.getText().toString();
                String password = et_re_password.getText().toString();
                if (username.isEmpty()) {
                    showToast("手机号码不能为空");
                    return;
                }
                if (password.isEmpty()) {
                    showToast("密码不能为空");
                    return;
                }
                if (!password.equals(et_replay_password.getText().toString())) {
                    showToast("两次密码不匹配");
                    return;
                }
                final User us = new User();
                us.setUsername(username);
                us.setPassword(password);
                final ProgressDialog progressView = new ProgressDialog(LoginActivity.this);
                progressView.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressView.setMessage("正在注册...");
                progressView.setCancelable(false);
                progressView.show();
                try {
                    LlcService.getApi().register(new JSONObject(new Gson().toJson(us)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    progressView.dismiss();
                                    showToast("注册失败:" + e.getMessage());
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(ResponseBody responseBody) {
                                    progressView.dismiss();
                                    try {
                                        String response = responseBody.string();
                                        Log.i("TAG", new Gson().toJson(us)+"\n"+response);
                                        Const.sUser = us;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.btn_sign_in)
    public void attemptLogin() {
        et_username.setError(null);
        et_password.setError(null);

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            et_username.setError(getString(R.string.error_field_required));
            focusView = et_username;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            final User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            final ProgressDialog progressView = new ProgressDialog(this);
            progressView.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressView.setMessage("正在登录...");
            progressView.setCancelable(false);
            progressView.show();
            try {
                LlcService.getApi().login(new JSONObject(new Gson().toJson(user)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                progressView.dismiss();
                                if (e.getMessage().contains("401"))
                                    showToast("用户名或密码错误");
                                else
                                    showToast("登录失败:" + e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                progressView.dismiss();
                                try {
                                    String response = responseBody.string();
                                    Log.i("TAG", response);
                                    Const.sUser = user;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //  关键部分在这里
    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

