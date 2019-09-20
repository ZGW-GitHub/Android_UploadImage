package com.example.uploadimg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uploadimg.database.Config;
import com.example.uploadimg.util.MyUtils;
import com.qiniu.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import me.rosuh.filepicker.bean.FileItemBeanImpl;
import me.rosuh.filepicker.config.AbstractFileFilter;
import me.rosuh.filepicker.config.FilePickerManager;
import me.rosuh.filepicker.filetype.RasterImageFileType;

public class MainActivity extends AppCompatActivity {

    private Config config = new Config(this);

    private TextView showFilePathTextView;
    private Button findFileButton;
    private Button uploadFileButton;
    private Button gotoConfigButton;
    private Button getImagesButton;

    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showFilePathTextView = findViewById(R.id.ShowFilePath);
        findFileButton = findViewById(R.id.FindFile);
        uploadFileButton = findViewById(R.id.UploadFile);
        gotoConfigButton = findViewById(R.id.GoToConfig);
        getImagesButton = findViewById(R.id.GetImages);

        int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {

            final int REQUEST_EXTERNAL_STORAGE = 1;
            String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);

        }

        uploadFileButton.setOnClickListener(v -> {

            final EditText changeNameET = new EditText(this);

            if (StringUtils.isNullOrEmpty(filePath)) {

                Toast.makeText(MainActivity.this, "请选择文件", Toast.LENGTH_SHORT).show();

            } else if (!MyUtils.getActiveNetworkInfo(MainActivity.this)) {

                Toast.makeText(MainActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("请输上传后文件的名称 : ");

                builder.setView(changeNameET);

                builder.setPositiveButton("不重命名文件", (dialog, which) -> {

                    // 上传图片
                    new Thread(() -> {
                        Boolean ok = ImageUtils.uploadImage(filePath, null);
                        if (ok) {
                            showFilePathTextView.setText("");
                            MyUtils.showToast(this, "上传成功");
                        } else {
                            MyUtils.showToast(this, "上传失败,请检查配置或网络");
                        }
                    }).start();

                });

                builder.setNegativeButton("重命名文件", (dialog, which) -> {

                    String newFileName = changeNameET.getText().toString().trim();

                    if (StringUtils.isNullOrEmpty(newFileName)) {

                        Toast.makeText(MainActivity.this, "文件名非法,请重新输入", Toast.LENGTH_SHORT).show();

                    } else {
                        // 上传图片
                        new Thread(() -> {
                            Boolean ok = ImageUtils.uploadImage(filePath, newFileName);
                            if (ok) {
                                showFilePathTextView.setText("");
                                MyUtils.showToast(this, "上传成功");
                            } else {
                                MyUtils.showToast(this, "上传失败,请检查配置或网络");
                            }
                        }).start();

                    }

                });

                builder.show();

            }

        });

        findFileButton.setOnClickListener(v -> FilePickerManager.INSTANCE
                .from(MainActivity.this)
                .filter(myFilter)
                .maxSelectable(1)
                .showCheckBox(false)
                .forResult(1));

        gotoConfigButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ConfigActivity.class)));

        getImagesButton.setOnClickListener(v -> {

            if (!MyUtils.getActiveNetworkInfo(MainActivity.this))
                Toast.makeText(MainActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
            else {

                Toast.makeText(this, "查询中,请稍等", Toast.LENGTH_SHORT).show();

                CountDownLatch countDownLatch = new CountDownLatch(1);
                // 从云端查询数据
                new Thread(() -> {
                    final ArrayList<String> list = ImageUtils.getList();
                    countDownLatch.countDown();
                    if (list != null) {
                        Intent intent = new Intent(getApplicationContext(), ImagesActivity.class);
                        intent.putStringArrayListExtra("images", list);
                        startActivity(intent);
                    }
                }).start();

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    Toast.makeText(this, "查询失败,请检查配置或网络", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * 自定义文件过滤器
     */
    AbstractFileFilter myFilter = new AbstractFileFilter() {
        @Override
        public ArrayList<FileItemBeanImpl> doFilter(final ArrayList<FileItemBeanImpl> arrayList) {
            ArrayList<FileItemBeanImpl> fileItemBeans = new ArrayList<>();
            for (FileItemBeanImpl fileItemBean : arrayList) {
                if (fileItemBean.isDir() || fileItemBean.getFileType() instanceof RasterImageFileType) {
                    fileItemBeans.add(fileItemBean);
                }
            }
            return fileItemBeans;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            List<String> list = FilePickerManager.INSTANCE.obtainData();
            if (resultCode == Activity.RESULT_OK) {
                filePath = list.get(0);
                showFilePathTextView.setText(filePath);
            } else {
                list.clear();
                filePath = null;
                showFilePathTextView.setText("");
                Toast.makeText(this, "没有选择任何东西", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
