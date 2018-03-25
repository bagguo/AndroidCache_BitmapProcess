package com.example.sanjicache.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.syc.a25_androidcache.utils.HttpUtil;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 11:30
 * 备注:
 */

class LoadImgTask extends AsyncTask<String, Void, Bitmap> {

    interface OnLoadImgListener {
        void onLoadImg(Bitmap bitmap);
    }

    private OnLoadImgListener mListener;

    LoadImgTask(OnLoadImgListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        byte[] data = HttpUtil.loadData(params[0]);
        if (data != null) {
            //考虑采用二次采样,减少图片的内存大小
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mListener != null && bitmap != null) {
            mListener.onLoadImg(bitmap);
        }
    }
}
