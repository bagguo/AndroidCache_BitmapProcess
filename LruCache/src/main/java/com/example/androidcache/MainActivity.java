package com.example.androidcache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
//1下载json的线程
        new LoadJsonThread().start();

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

    //下载json的线程
    class LoadJsonThread extends Thread {
        @Override
        public void run() {
            //网络请求json
            byte[] data = HttpUtil.loadData(CacheConfig.JSON_URL);
            //解析json
            if (data != null) {
                String json = new String(data);
                Message msg = Message.obtain();
                msg.what = LOAD_SUCCEED;
                msg.obj = json;//把json用handler传到主线程
                mHandler.sendMessage(msg);
            }
        }
    }
}
