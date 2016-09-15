package com.llcwh.babycare.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.llcwh.babycare.R;
import com.llcwh.babycare.api.LlcService;
import com.llcwh.babycare.model.CommonResponse;
import com.llcwh.babycare.model.LoginResponse;
import com.llcwh.babycare.model.User;
import com.llcwh.babycare.ui.base.BaseActivity;
import com.llcwh.babycare.util.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@RuntimePermissions
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

        et_password.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
        loginWithToken();
        btn_sign_in.setOnClickListener(view -> LoginActivityPermissionsDispatcher.attemptLoginWithCheck(LoginActivity.this));
        tv_register.setOnClickListener(view -> LoginActivityPermissionsDispatcher.registerWithCheck(LoginActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginActivityPermissionsDispatcher.resumeLoginWithCheck(this);

    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void resumeLogin() {
        String userString = SPUtil.getUser();
        if (TextUtils.isEmpty(userString))
            return;
        String[] userStrings = userString.split(":");
        if (userStrings.length == 2) {
            et_username.setText(userStrings[0]);
            et_password.setText(userStrings[1]);
        }
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void register() {
        alertDialog = new AlertDialog.Builder(this).setPositiveButton("完成注册", null).create();
        @SuppressLint("InflateParams") View custom = LayoutInflater.from(this).inflate(R.layout.dialog_register, null, false);
        alertDialog.setView(custom);
        final EditText et_re_username = (EditText) custom.findViewById(R.id.et_username);
        final EditText et_re_password = (EditText) custom.findViewById(R.id.et_password);
        final EditText et_replay_password = (EditText) custom.findViewById(R.id.et_replay_password);
        alertDialog.setTitle("注册");
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            final String username = et_re_username.getText().toString();
            final String password = et_re_password.getText().toString();
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
            final User user = new User(username, password);
            final ProgressDialog progressView = new ProgressDialog(LoginActivity.this);
            progressView.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressView.setMessage("正在注册...");
            progressView.setCancelable(false);
            progressView.show();
            LlcService.getApi().register(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CommonResponse>() {
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
                        public void onNext(CommonResponse responseBody) {
                            progressView.dismiss();
                            if (responseBody.isStatus()) {
                                alertDialog.dismiss();
                                showToast(responseBody.getMsg());
                                et_username.setText(username);
                                et_password.setText(password);
                                attemptLogin();
                            } else {
                                showToast(responseBody.getMsg());
                            }
                        }
                    });
        });
    }

    public void loginWithToken() {
        LlcService.getApi().getInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
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
            final User user = new User(username, password);
            final ProgressDialog progressView = new ProgressDialog(this);
            progressView.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressView.setMessage("正在登录...");
            progressView.setCancelable(false);
            progressView.show();
            LlcService.getApi().login(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LoginResponse>() {
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
                        public void onNext(LoginResponse responseBody) {
                            progressView.dismiss();
                            SPUtil.saveToken(responseBody.getAccess_token());
                            SPUtil.saveUser(username + ":" + password);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    void showRationaleForPhoneState(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("需要电话权限读取唯一认证")
                .setPositiveButton("允许", (dialog, button) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    void showDeniedForCamera() {
        Toast.makeText(this, "读取电话权限被拒绝，将无法使用", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    void showNeverAskForCamera() {
        Toast.makeText(this, "读取电话权限已被永久拒绝，将无法使用，请到设置里打开权限", Toast.LENGTH_SHORT).show();
    }
}

