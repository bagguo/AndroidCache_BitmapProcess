package com.example.sanjicache.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sanjicache.R;
import com.example.sanjicache.bean.Person;

import java.util.List;


/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 10:53
 * 备注:
 */

public class MyAdapter extends BaseAdapter {

    private List<Person> persons;

    void setData(List<Person> persons) {
        this.persons = persons;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return persons != null ? persons.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return persons != null ? persons.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Person person = persons.get(position);
        holder.tvName.setText(person.getName());

        final String imageUrl = person.getImage();

        final BitmapCache cache = BitmapCache.newInstance();

        //先从缓存中获取图片
        Bitmap bitmap = cache.getBitmap(imageUrl);

        if (bitmap == null) {//第一次,从网络下载.下载完成后进行缓存
            Log.i("TAG", "来自于网络");
            new LoadImgTask(new LoadImgTask.OnLoadImgListener() {
                @Override
                public void onLoadImg(Bitmap bitmap) {
                    holder.ivLogo.setImageBitmap(bitmap);
                    //进行缓存
                    cache.putBitmap(imageUrl, bitmap);
                }
            }).execute(imageUrl);
        } else {
            //从缓存中获取的图片
            Log.i("TAG", "来自于缓存");
            holder.ivLogo.setImageBitmap(bitmap);
        }


        return convertView;
    }

    static class ViewHolder {

        private TextView tvName;
        private ImageView ivLogo;

        ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tv_name);
            ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
        }
    }
}
