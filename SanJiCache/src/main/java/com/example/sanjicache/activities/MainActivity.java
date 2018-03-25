package com.example.sanjicache.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;


import com.example.sanjicache.adapter.MyAdapter;
import com.example.sanjicache.bean.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_SUCCEED = 1;
    private MyHandler mHandler;
    private static MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView mLv = (ListView) findViewById(R.id.lv);

        mHandler = new MyHandler(this);

        //先判断本地有没有该json文件
        if (isJsonExist(CacheConfig.JSON_DIR)) {
            //如果存在json文件,直接从本地读取该json文件
            loadJsonFromLocal(CacheConfig.JSON_DIR);
        } else {
            //本地没有json缓存,才开启网络进行json的下载并解析
            new LoadJsonThread().start();
        }

        mAdapter = new MyAdapter();
        mLv.setAdapter(mAdapter);
    }


    //处理消息,解析json
    static class MyHandler extends Handler {
        WeakReference<Activity> reference = null;

        MyHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference.get() != null) {
                switch (msg.what) {
                    case LOAD_SUCCEED:
                        String json = (String) msg.obj;
                        List<Person> persons = parseJson(json);
                        mAdapter.setData(persons);
                        break;
                }
            }
        }
    }

    //解析json的方法
    private static List<Person> parseJson(String json) {
        try {
            List<Person> persons = new ArrayList<>();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String image = obj.getString("image");
                Person p = new Person(name, image);
                persons.add(p);
            }
            return persons;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //加载本地的json文件
    private void loadJsonFromLocal(String jsonDir) {
        File file = new File(jsonDir);
        if (file.exists()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                //本地的json
                byte[] data = baos.toByteArray();
                //解析json
                if (data != null) {
                    String json = new String(data);
                    Message msg = Message.obtain();
                    msg.what = LOAD_SUCCEED;
                    msg.obj = json;
                    mHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //判断某个json文件是否存在
    private boolean isJsonExist(String jsonDir) {
        File file = new File(jsonDir);
        if (file != null && file.length() > 0) {
            return true;
        }
        return false;
    }

    //保存json到本地文件夹
    private void saveJsonToLocal(byte[] data) {
        try {
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(CacheConfig.JSON_DIR)));
            os.write(data, 0, data.length);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下载json的线程
    class LoadJsonThread extends Thread {
        @Override
        public void run() {
            //json
            byte[] data = HttpUtil.loadData(CacheConfig.JSON_URL);

            //把json保存起来.
            saveJsonToLocal(data);

            //解析json
            if (data != null) {
                String json = new String(data);
                Message msg = Message.obtain();
                msg.what = LOAD_SUCCEED;
                msg.obj = json;
                mHandler.sendMessage(msg);
            }
        }
    }
}
