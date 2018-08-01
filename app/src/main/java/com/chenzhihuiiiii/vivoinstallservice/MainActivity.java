package com.chenzhihuiiiii.vivoinstallservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etPassword;
    TextView tvCurrentSavePassword;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPassword = findViewById(R.id.etPassword);
        tvCurrentSavePassword = findViewById(R.id.tvCurrentPassword);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd = etPassword.getText().toString();
                if (!TextUtils.isEmpty(newPwd)) {
                    getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).edit().putString(Constant.PREFRENCE_KEY_VIVO_PASSWORD, newPwd).commit();
                    tvCurrentSavePassword.setText(getString(R.string.current_vivo_password, getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE)
                            .getString(Constant.PREFRENCE_KEY_VIVO_PASSWORD, Constant.DEFAULT_PASSWORD)));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.pwd_cannot_be_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        String password = getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE)
                .getString(Constant.PREFRENCE_KEY_VIVO_PASSWORD, Constant.DEFAULT_PASSWORD);
        tvCurrentSavePassword.setText(getString(R.string.current_vivo_password, password));

    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean isAccessbilityServiceRunning = Utils.isStartAccessibilityService(getApplicationContext(), VivoInstallerHelperService.class.getSimpleName());
        if (!isAccessbilityServiceRunning) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.attention).setMessage(R.string.please_open_service)
                    .setPositiveButton(getString(R.string.open_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            alertDialog.show();
        }
    }
}
