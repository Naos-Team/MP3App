package com.naosteam.countrymusic.ringtone.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.ringtone.Activity.MainActivity;
import com.naosteam.countrymusic.ringtone.CheckBok.SmoothCheckBox;
import com.naosteam.countrymusic.ringtone.Method.Methods;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.SharedPref.SharedPref;


public class LoginActivity extends AppCompatActivity {


    private String from = "";

    SharedPref sharedPre;
    EditText editText_email, editText_password;
    Button login, skip;
    TextView textView_register;
    Methods methods;
    ProgressDialog progressDialog;
    LinearLayout checkbox;
    SmoothCheckBox rememberme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ringtone);

        from = getIntent().getStringExtra("from");

        sharedPre = new SharedPref(this);
        methods = new Methods(this);
        methods.setStatusColor2(getWindow());


        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("loading");
        progressDialog.setCancelable(false);

        checkbox = findViewById(R.id.checkbox);
        rememberme = findViewById(R.id.cb_rememberme);
        editText_email = findViewById(R.id.et_login_email);
        editText_password = findViewById(R.id.et_login_password);
        login = findViewById(R.id.button_login);
        skip = findViewById(R.id.button_skip);
        textView_register = findViewById(R.id.tv_login_signup);

        skip.setTextColor(getResources().getColor(R.color.colorAccentNight));

        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        textView_register.setTypeface(textView_register.getTypeface(), Typeface.BOLD);
        login.setTypeface(login.getTypeface(), Typeface.BOLD);
        skip.setTypeface(skip.getTypeface(), Typeface.BOLD);

        if(sharedPre.getIsRemember()) {
            editText_email.setText(sharedPre.getEmail());
            editText_password.setText(sharedPre.getPassword());
            rememberme.setChecked(true);
        }

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberme.setChecked(!rememberme.isChecked());
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        editText_email.setError(null);
        editText_password.setError(null);

        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            editText_password.setError(getString(R.string.error_password_sort));
            focusView = editText_password;
            cancel = true;
        }
        if (editText_password.getText().toString().endsWith(" ")) {
            editText_password.setError(getString(R.string.pass_end_space));
            focusView = editText_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            editText_email.setError(getString(R.string.cannot_empty));
            focusView = editText_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            editText_email.setError(getString(R.string.error_invalid_email));
            focusView = editText_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loadLogin();
        }
    }

    private void loadLogin() {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Setting.itemUser = new ItemUser(user_id, user_name, editText_email.getText().toString(), "");
                            if (rememberme.isChecked()) {
                                sharedPre.setLoginDetails(Setting.itemUser, rememberme.isChecked(), editText_password.getText().toString());
                            } else {
                                sharedPre.setRemeber(false);
                            }
                            sharedPre.setIsAutoLogin(true);
                            Setting.isLogged = true;
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (from.equals("app")) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.err_server), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_LOGIN, 0,"","","","","","","","","", editText_email.getText().toString(), editText_password.getText().toString(),"","","","", null));
            loadLogin.execute();
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}