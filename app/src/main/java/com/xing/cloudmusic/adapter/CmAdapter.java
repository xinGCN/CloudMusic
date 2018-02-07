package com.xing.cloudmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xing.cloudmusic.R;
import com.xing.cloudmusic.activity.MainActivity;
import com.xing.cloudmusic.base.ResultAndCode;
import com.xing.cloudmusic.base.Song;


import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class CmAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Song> songs;
    private Context context;

    public CmAdapter(Context context, ResultAndCode rac){
        this.context = context;
        mInflater = LayoutInflater.from(context);
        songs = rac.getSongs();
    }

    @Override
    public int getCount() {
        if(songs != null) return songs.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item,parent,false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.imageView);
            holder.text1 = convertView.findViewById(R.id.textView1);
            holder.text2 = convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Song song = songs.get(position);



        if(song.getAlbumPicUrl()!=null)
            Picasso.with(context)
                    .load(song.getAlbumPicUrl())
                    .resize(50, 50)
                    .centerCrop()
                    .into(holder.image);
        
        holder.text1.setText(song.getName());
        holder.text2.setText(song.getArtistName() + " - " + song.getAlbumName());

        return convertView;
    }

    private class ViewHolder{
        ImageView image;
        TextView text1;
        TextView text2;

    }
}
