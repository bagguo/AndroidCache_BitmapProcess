package com.syc.a25_bitmapprocess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class BitmapUtil {

    //    Bitmap.Config.ALPHA_8;--->只存储透明度的信息,占1个字节.
    //    Bitmap.Config.ARGB_4444;--->存储透明度及红绿蓝的信息,占2个字节.图片的质量特别次!API13以后就被Google给废弃了.
    //    Bitmap.Config.ARGB_8888;--->占4个字节.默认的配置.
    //    Bitmap.Config.RGB_565;--->2个字节,只保存红绿蓝信息,没有透明度.

    //压缩图片的方法
    public static Bitmap doParse(byte[] datas, int width, int height, Bitmap.Config config) {
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        if (width == 0 && height == 0) {
            //不想改变图片的宽,高和采样率
            options.inPreferredConfig = config;//更喜欢的参数
            bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length, options);
        } else {
            //打开仅测量Bitmap边界的功能
            options.inJustDecodeBounds = true;
            //此处只会测量图片的宽,高,并不会给图片分配内存
            BitmapFactory.decodeByteArray(datas, 0, datas.length, options);
            //拿到了Bitmap的真实的宽,高.
            int acturalWidth = options.outWidth;
            int acturalHeight = options.outHeight;

            //以宽为主
            int resizedWidth = getResizedDimension(width, height, acturalWidth, acturalHeight);
            //以高为主
            int resizedHeight = getResizedDimension(height, width, acturalHeight, acturalWidth);

            //计算出一个合理的SimpleSize.它会根据图片本身的尺寸结合期望值计算出来的合理值.
            options.inSampleSize = findBestSampleSize(resizedWidth, resizedHeight, width, height);

            //关闭仅测量Bitmap边界的功能
            options.inJustDecodeBounds = false;

            //得到一个临时的Bimtap对象
            Bitmap tempBitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length, options);
            if (tempBitmap != null && (tempBitmap.getWidth() > width || tempBitmap.getHeight() > height)) {
                //创建一个压缩图片
                bitmap = tempBitmap.createScaledBitmap(tempBitmap, width, height, true);
                //回收临时Bitmap所占用的内存
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        return bitmap;

    }

    //计算最佳采样率的方法.actualWidth=120,actualHeight=120,desiredWidth=100,desiredHeight=100
    static int findBestSampleSize(int actualWidth, int actualHeight,
                                  int desiredWidth, int desiredHeight) {
        //1.73
        double wr = (double) actualWidth / desiredWidth;
        //1.73
        double hr = (double) actualHeight / desiredHeight;
        //1.73
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    //通过期望的宽,高与真实的宽,高,这4个值,最终得到一个合理的宽和高.
    //maxPrimary:期望的宽,maxSecondary:期望的高
    //actualPrimary:真实的宽,actualSecondary:真实的高
    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }
}
