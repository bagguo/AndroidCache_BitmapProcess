package com.example.sanjicache.cache;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 14:12
 * 备注:
 */

class BitmapSoftCache {

    //利用软引用这个类,来对Bitmap做一个包裹,此时Bitmap对象就可以变成软引用对象了.
    //SoftReference<Bitmap> reference;
    private Map<String, SoftReference<Bitmap>> map = null;

    private static BitmapSoftCache mCache;

    private BitmapSoftCache() {
        map = new HashMap<>();
    }

    static BitmapSoftCache newInstance() {
        if (mCache == null) {
            synchronized (BitmapSoftCache.class) {
                if (mCache == null) {
                    mCache = new BitmapSoftCache();
                }
            }
        }
        return mCache;
    }

    //new出来的对象都属于强引用对象;
    //软引用对象:
    void put(String key, Bitmap value) {
        //软引用实现缓存.
        map.put(key, new SoftReference<>(value));
    }

    //取图片的方法
    Bitmap get(String key) {
        SoftReference<Bitmap> reference = map.get(key);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }
}
