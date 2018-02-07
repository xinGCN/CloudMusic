package com.xing.cloudmusic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xing.cloudmusic.R;
import com.xing.cloudmusic.adapter.PlayListAdapter;
import com.xing.cloudmusic.base.Song;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by xinG on 2018/2/3 0003.
 */

public class PlayListDialog extends Dialog{
    private ListView playListView;
    private Context context;
    private Handler mHandler;

    public PlayListDialog(@NonNull Context context, Handler mHandler) {
        super(context);
        this.mHandler = mHandler;
        initDialog(context);
    }

    private void initDialog(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.playlistdialog,null);
        playListView = view.findViewById(R.id.playList);
        super.setContentView(view);
    }

    public void setAdapter(List<Song> songs){
        playListView.setAdapter(new PlayListAdapter(context,mHandler,songs));
    }

    public void invalidate(){
        playListView.invalidateViews();
    }
}
