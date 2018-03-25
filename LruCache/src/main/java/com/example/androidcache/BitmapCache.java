package com.example.androidcache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 11:37
 * 备注:
 */

class BitmapCache {

    //单例模式的写法.①.饿汉式;②.懒汉式.
    private LruCache<String, Bitmap> mCache;//最少最近使用
    private static BitmapCache mInstance;
    private static final Object sObj = new Object();

    private BitmapCache() {

        //获取虚拟机的最大内存值
        long maxMemory = Runtime.getRuntime().maxMemory();
        //初始化LruCache对象
        mCache = new LruCache<String, Bitmap>((int) maxMemory / 4) {

            //用来返回集合中每个条目的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //图片上每一行的字节大小*行高
                return value.getRowBytes() * value.getHeight();
            }

            //移除每个条目的方法.
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    //构建类的实例
    public static BitmapCache newInstance() {
        if (mInstance == null) {
            synchronized (sObj) {//防止资源争抢
                if (mInstance == null) {
                    mInstance = new BitmapCache();
                }
            }
        }
        return mInstance;
    }

    //存储缓存的方法
    public void putBitmap(String key, Bitmap value) {
        //存到内存中
        mCache.put(key, value);
    }

    //从缓存中取图片的方法
    public Bitmap getBitmap(String key){
        return mCache.get(key);
    }

    //清理缓存的方法
    public void clearCache(){
        //mCache.remove(key);
        mCache.evictAll();
    }
}
