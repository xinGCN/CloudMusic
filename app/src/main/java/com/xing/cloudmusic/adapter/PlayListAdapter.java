package com.xing.cloudmusic.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xing.cloudmusic.R;
import com.xing.cloudmusic.activity.MainActivity;
import com.xing.cloudmusic.base.Song;
import com.xing.cloudmusic.util.LogUtil;

import java.util.List;

/**
 * Created by xinG on 2018/2/3 0003.
 */

public class PlayListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Song> songs;
    private Context context;
    private Handler mHandler;

    public PlayListAdapter(Context context, Handler mHandler,List<Song> songs){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.songs = songs;
        this.mHandler = mHandler;
    }

    @Override
    public int getCount() {
        if(songs != null) return songs.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(songs != null) return songs.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.playlistitem,parent,false);
            holder = new ViewHolder();
            holder.text1 = convertView.findViewById(R.id.item_text1);
            holder.text2 = convertView.findViewById(R.id.item_text2);
            holder.delete = convertView.findViewById(R.id.deleteItem);
            holder.download = convertView.findViewById(R.id.downloadItem);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();

        final Song song = songs.get(position);
        holder.text1.setText(song.getName());
        holder.text2.setText(song.getArtistName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // LogUtil.LogE();
            if(mHandler != null)
                mHandler.obtainMessage(MainActivity.DELETE_PLAYLISTITEM,song.getId()).sendToTarget();
            }
        });
        holder.text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHandler != null)
                    mHandler.obtainMessage(MainActivity.DIALOG_ACTION_PLAY,song.getId()).sendToTarget();
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHandler != null)
                    mHandler.obtainMessage(MainActivity.DIALOG_DOWNLOAD_MUSIC,song).sendToTarget();
            }
        });


        return convertView;
    }


    private class ViewHolder{
        TextView text1;
        TextView text2;
        ImageButton delete;
        ImageButton download;
    }
}
