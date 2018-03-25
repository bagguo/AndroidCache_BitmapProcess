package com.example.sanjicache.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.syc.a25_androidcache.CacheConfig;

import java.io.File;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 11:37
 * 备注:
 */

class BitmapCache {

    //单例模式的写法.①.饿汉式;②.懒汉式.
    private LruCache<String, Bitmap> mCache;
    private static BitmapCache mInstance;
    private static final Object sObj = new Object();
    private final DiskLruCache diskLruCache;

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

            //Android4.0以前很流行的技术,软引用;
            //4.0以后:gc一旦发现软引用对象,也会立马回收.
            //移除每个条目的方法.
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                //在这里添加以及软引用.实际开发过程没有必要添加.本例只是通过此处讲解一下软引用的使用!
                if (evicted) {
                    //存放到软引用中.
                    BitmapSoftCache.newInstance().put(key, oldValue);
                }
            }
        };

        //创建磁盘缓存的实例
        diskLruCache = DiskLruCache.openCache(new File(CacheConfig.DISK_CACHE), CacheConfig.DISK_SIZE);
    }

    //构建类的实例
    static BitmapCache newInstance() {
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
    void putBitmap(String key, Bitmap value) {
        //存到内存中
        mCache.put(key, value);
        //缓存到磁盘中
        diskLruCache.put(key, value);
    }

    //从缓存中取图片的方法
    Bitmap getBitmap(String key) {
        //从LruCache中.
        Bitmap bitmap = mCache.get(key);
        if (bitmap == null) {
            //从软引用中取图片
            bitmap = BitmapSoftCache.newInstance().get(key);
            if (bitmap == null) {
                //从磁盘中取图片
                bitmap = diskLruCache.get(key);
            }
        }
        return bitmap;
    }

    //清理缓存的方法
    public void clearCache() {
        //mCache.remove(key);
        mCache.evictAll();

        //清理磁盘缓存的方法
        diskLruCache.clearCache();
    }
}
