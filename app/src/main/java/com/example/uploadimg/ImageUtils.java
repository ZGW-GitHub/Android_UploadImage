package com.example.uploadimg;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;

import com.example.uploadimg.database.Config;
import com.example.uploadimg.database.Key;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    private static String bucket;
    private static String upToken;
    private static UploadManager uploadManager;
    private static BucketManager bucketManager;
    private static String domain;

    private static void init() {

        List<Key> queryAll = Config.queryAll();
        Key key = queryAll.get(0);

        // 生成上传凭证
        Configuration cfg = new Configuration(Region.region0());
        Auth auth = Auth.create(key.getAccesskey(), key.getSecretkey());

        bucket = key.getBucket();
        domain = key.getDomain();
        upToken = auth.uploadToken(bucket);
        uploadManager = new UploadManager(cfg);

        bucketManager = new BucketManager(auth, cfg);

    }

    public static Boolean uploadImage(String filePath, String newFileName) {

        init();

        String key;

        if (newFileName != null) {
            String[] split = filePath.split("\\.");
            key = newFileName + "." + split[split.length - 1];
        } else {
            String[] split = filePath.split("/");
            key = split[split.length - 1];
        }

        try {
            uploadManager.put(filePath, key, upToken);
        } catch (QiniuException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public static void deleteImage(String imageKey) throws QiniuException {

        init();

        bucketManager.delete(bucket, imageKey);

    }

    public static Boolean downloadImage(String imageKey, Activity activity) {

        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {

            init();

            URL url;
            URLConnection con;
            InputStream is;
            OutputStream os;
            try {
                url = new URL(String.format("http://%s/%s", domain, imageKey));
                con = url.openConnection();
                is = con.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            byte[] bs = new byte[1024];

            int len;

            File sdPath = Environment.getExternalStorageDirectory();
            String savePath = sdPath.getPath() + "/Download/qiniuyun/";
            File saveFile = new File(savePath);
            try {

                if (!saveFile.exists()) {


                    int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");

                    if (permission != PackageManager.PERMISSION_GRANTED) {

                        final int REQUEST_EXTERNAL_STORAGE = 1;
                        String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);

                    }

                    saveFile.mkdirs();

                }

                os = new FileOutputStream(new File(saveFile, imageKey));
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }

                os.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;

    }

    public static ArrayList<String> getList() {

        init();

        String prefix = "";
        int limit = 1000;
        String delimiter = "";

        ArrayList list = new ArrayList<String>();

        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucket, prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            FileInfo[] items = fileListIterator.next();
            if (items == null)
                return null;
            for (FileInfo item : items) {
                list.add(item.key);
            }

        }

        return list;
    }

}
