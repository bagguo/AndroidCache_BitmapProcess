package com.example.sanjicache;

import android.os.Environment;

import java.io.File;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 10:52
 * 备注:
 */

class CacheConfig {

    static final String JSON_URL = "http://10.0.166.252:8080/MyService/data.json";

    static final String DISK_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1610";

    static final long DISK_SIZE = 1024*1024*1;

    static final String JSON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1610"+File.separator+"data.json";

}
