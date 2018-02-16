package com.xing.cloudmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xing.cloudmusic.R;
import com.xing.cloudmusic.base.LocalSong;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinG on 2018/2/11 0011.
 */
public class LocalListAdapter extends BaseAdapter{
    private List<LocalSong> songs;
    private LayoutInflater mInflater;

    public LocalListAdapter(Context context, List<LocalSong> songs){
        mInflater = LayoutInflater.from(context);
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs == null ? 0 : songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs == null ? null : songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.localitem,parent,false);
            holder = new ViewHolder();
            holder.text1 = convertView.findViewById(R.id.local_text1);
            holder.text2 = convertView.findViewById(R.id.local_text2);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        holder.text1.setText(songs.get(position).getName());
        holder.text2.setText(songs.get(position).getArtistName() + " - " + songs.get(position).getAlbumName());
        return convertView;
    }

    private class ViewHolder{
        TextView text1;
        TextView text2;
    }

}
