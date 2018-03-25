package com.syc.a25_bitmapprocess;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
    }

    public void loadImg(View view) {

        final String path = "http://pimg1.126.net/movie/product/movie/147616900024410675_520_692_webp.jpg";

        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        while ((len = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                            baos.flush();
                        }

                        byte[] data = baos.toByteArray();
                        //进行二次采样
                        final Bitmap bitmap = BitmapUtil.doParse(data, 400, 400, Bitmap.Config.ARGB_8888);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                iv.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //    public void loadImg(View view) {
    //        //OOM:out of memory.
    //        BitmapFactory.Options opts = new BitmapFactory.Options();
    //        //如何给图片分配内存?
    //        //"开".--->仅测量边界,但是不会给Bitmap分配内存.
    //        opts.inJustDecodeBounds = true;
    //        //加载图片.但是此时并没有给该图片分配内存.
    //        BitmapFactory.decodeResource(getResources(), R.mipmap.snow, opts);
    //
    //        //中间做想要执行的事情.
    //        //图片真实的宽度
    //        int outWidth = opts.outWidth;
    //        //图片真实的高度
    //        int outHeight = opts.outHeight;
    //
    //        Log.i("TAG", "width=" + outWidth);
    //        Log.i("TAG", "height=" + outHeight);
    //
    //        //inSampleSize最后用2的整数倍.
    //        //宽变为原有的1/2,高也变为原有的1/2.整体就变为原有的1/4.
    //        //最低标准:400x400
    //        //1200x800;400x300
    //        opts.inSampleSize = 2;
    //
    //        //此时的bitmap内存是为空的.
    //        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.snow, opts);
    //
    //        //"关"
    //        opts.inJustDecodeBounds = false;
    //
    //        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.snow, opts);
    //        int width = bitmap.getWidth();
    //        int height = bitmap.getHeight();
    //        Log.i("TAG", "w=" + width);
    //        Log.i("TAG", "h=" + height);
    //        iv.setImageBitmap(bitmap);
    //    }
}
