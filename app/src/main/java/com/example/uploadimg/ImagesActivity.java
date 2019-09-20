package com.example.uploadimg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uploadimg.util.MyUtils;
import com.qiniu.common.QiniuException;

import java.util.ArrayList;

public class ImagesActivity extends Activity {

    private ListView imagesLV;

    private static ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        list = extras.getStringArrayList("images");

        if (list.size() == 0) {
            Toast.makeText(this, "云端无数据", Toast.LENGTH_SHORT).show();
        }

        imagesLV = findViewById(R.id.imagesLV);

        // 为 ListView 添加适配器
        imagesLV.setAdapter(new MyAdapter());

    }

    /**
     * 自定义适配器
     */
    public class MyAdapter extends BaseAdapter {

        /**
         * 获取条目总数
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 根据位置获取对象
         */
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        /**
         * 根据位置获取 ID
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 获取一个条目视图
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView != null ? convertView : View.inflate(getApplicationContext(), R.layout.image_item, null);
            TextView imageNameTV = item.findViewById(R.id.imageNameTV);
            final String imagekey = list.get(position);
            imageNameTV.setText(imagekey);
            Button itemDeleteButton = item.findViewById(R.id.itemDeleteButton);
            Button itemDownLoadButton = item.findViewById(R.id.itemDownLoadButton);

            itemDeleteButton.setOnClickListener(v -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(ImagesActivity.this);

                builder.setTitle("确认要删除吗？");

                builder.setPositiveButton("确定", (dialog, which) -> {

                    if (!MyUtils.getActiveNetworkInfo(ImagesActivity.this)) {

                        Toast.makeText(ImagesActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();

                    } else {

                        new Thread(() -> {

                            try {
                                ImageUtils.deleteImage(imagekey);
                            } catch (QiniuException e) {
                                e.printStackTrace();
                            }

                        }).start();

                        list.remove(imagekey);

                        notifyDataSetChanged();

                        MyUtils.showToast(ImagesActivity.this, "删除成功");

                    }

                });

                builder.setNegativeButton("取消",null);

                builder.show();

            });

            itemDownLoadButton.setOnClickListener(v -> {

                if (!MyUtils.getActiveNetworkInfo(ImagesActivity.this)) {

                    Toast.makeText(ImagesActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();

                } else {

                    new Thread(() -> {

                        Boolean ok = ImageUtils.downloadImage(imagekey, ImagesActivity.this);

                        if (ok) {
                            MyUtils.showToast(ImagesActivity.this, "下载成功,文件下载至 /Download/qiniuyun 中了");
                        } else {
                            MyUtils.showToast(ImagesActivity.this, "下载失败,请检查配置或网络");
                        }

                    }).start();

                }

            });

            return item;
        }
    }

}
