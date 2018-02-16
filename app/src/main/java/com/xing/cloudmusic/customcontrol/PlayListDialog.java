package com.xing.cloudmusic.customcontrol;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.xing.cloudmusic.R;
import com.xing.cloudmusic.activity.LocalSongActivity;
import com.xing.cloudmusic.activity.MainActivity;
import com.xing.cloudmusic.adapter.PlayListAdapter;
import com.xing.cloudmusic.base.Song;

import java.util.List;

/**
 * Created by xinG on 2018/2/3 0003.
 */

public class PlayListDialog extends Dialog{
    private ListView playListView;
    private ImageButton button;
    private Context context;
    private Handler mHandler;

    public PlayListDialog(@NonNull Context context, Handler mHandler) {
        super(context);
        this.mHandler = mHandler;
        initDialog(context);
    }

    private void initDialog(final Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.playlistdialog,null);
        playListView = view.findViewById(R.id.playList);
        button = view.findViewById(R.id.dialog_add_local);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.obtainMessage(MainActivity.DIALOG_START_LOCALACTIVITY).sendToTarget();
            }
        });
        super.setContentView(view);
    }

    public void setAdapter(List<Song> songs){
        playListView.setAdapter(new PlayListAdapter(context,mHandler,songs));
    }

    public void invalidate(){
        playListView.invalidateViews();
    }
}
