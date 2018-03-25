package com.example.androidcache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 11:03
 * 备注:
 */

public class HttpUtil {

    public static byte[] loadData(String path) {

        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }

                return baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is);
            closeIO(baos);
        }

        return null;
    }

    private static void closeIO(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
