package com.sinosoft.myfiledeletetest;

import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private LruCache<String, File> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDir();//创建文件夹
        initFontCache();
        createFont("font1.txt");
        createFont("font2.txt");
        createFont("font3.txt");
        createFont("font4.txt");
        showCache(cache);//1234
        createFont("font5.txt");
        showCache(cache);//2345
        accessFile("font3.txt");
        showCache(cache);//2453
        createFont("font6.txt");
        showCache(cache);//4536
        accessFile("font5.txt");
        showCache(cache);//4365
    }
    private void accessFile(String fileName){
        cache.get(fileName);//模拟访问文件,正常情况下在访问文件时，调用下改方法即可
    }

    private void initFontCache(){
        int maxCacheSize=4;//默认最多4个文件
        //int maxCacheSize = 100 * 1024 * 1024;//100M
        cache = new LruCache<String, File>(maxCacheSize) {
           /* @Override
            protected int sizeOf(String key, File value) {
                return (int) value.length();
            }*/

            @Override
            protected void entryRemoved(boolean evicted, String key, File oldValue, File newValue) {
                if (evicted) {//如果是为释放空间，则删除文件
                    deleteFontFile(Environment.getExternalStorageDirectory() + "/Fonts/" + key);
                }

            }

        };
        File fileDir = new File(Environment.getExternalStorageDirectory(), "Fonts");
        File[] files = fileDir.listFiles();
        for (File file : files) {
            cache.put(file.getName(), file);
        }
    }

    private void showCache(LruCache<String, File> cache){
        Map<String, File> map=cache.snapshot();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : map.entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        Log.e("CacheInfo", sb.toString());
    }

    private void createDir() {
        File file = new File(Environment.getExternalStorageDirectory(), "Fonts");
        if (!file.exists()) {
            file.mkdir();
        }
    }


    private void createFont(String name) {
        File file = new File(Environment.getExternalStorageDirectory(), "Fonts/" + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write("hello".getBytes());
                fileOutputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cache.put(file.getName(), file);//加入缓存
    }

    public void deleteFontFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }


    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public long getFolderSize(File file) {
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
