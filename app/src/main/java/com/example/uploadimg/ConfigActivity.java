package com.example.uploadimg;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uploadimg.database.Config;
import com.example.uploadimg.database.Key;

import java.util.List;

public class ConfigActivity extends Activity {

    private EditText accessKeyET;
    private EditText secretKeyET;
    private EditText bucketET;
    private EditText domainET;

    private Button updateConfigButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        accessKeyET = findViewById(R.id.showAccessKey);
        secretKeyET = findViewById(R.id.showSecretKey);
        bucketET = findViewById(R.id.showBucket);
        domainET = findViewById(R.id.showDomain);
        updateConfigButton = findViewById(R.id.updateConfig);

        List<Key> queryAll = Config.queryAll();
        Key key = queryAll.get(0);
        String accessKey = key.getAccesskey();
        String secretKey = key.getSecretkey();
        String bucket = key.getBucket();
        String domain = key.getDomain();

        accessKeyET.setText(accessKey);
        secretKeyET.setText(secretKey);
        bucketET.setText(bucket);
        domainET.setText(domain);

        updateConfigButton.setOnClickListener(v -> {

            final TextView msgTV = new TextView(this);

            msgTV.setText(
                    "\n\n\t\t\t\t\t\t请勿随意修改该处配置,以免功能失效!!!" +
                    "\n\n\t\t\t\t\t\t可根据自己的七牛云账号按照规定修改此处配置!!!" +
                    "\n\n\t\t\t\t\t\t如果不小心修改可通过卸载重装来恢复默认设置!!!\n");

            AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);

            builder.setTitle("确认要修改吗？");

            builder.setView(msgTV);

            builder.setPositiveButton("确定", (dialog, which) -> {

                String accessKey1 = accessKeyET.getText().toString().trim();
                String secretKey1 = secretKeyET.getText().toString().trim();
                String bucket1 = bucketET.getText().toString().trim();
                String domain1 = domainET.getText().toString().trim();

                if (accessKey1.length() != 40 || secretKey1.length() != 40) {

                    Toast.makeText(ConfigActivity.this, "AccessKey 或 SecretKey 字符长度不正确,请核实", Toast.LENGTH_SHORT).show();

                } else {

                    Key key1 = new Key(accessKey1, secretKey1, bucket1, domain1);

                    Config.update(key1);

                    Toast.makeText(ConfigActivity.this, "设置成功", Toast.LENGTH_SHORT).show();

                }

            });

            builder.setNegativeButton("取消", null);

            builder.show();

        });

    }


}
