package com.xing.cloudmusic.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xing.cloudmusic.R;
import com.xing.cloudmusic.adapter.CmAdapter;
import com.xing.cloudmusic.base.DataAndCode;
import com.xing.cloudmusic.base.ResultAndCode;
import com.xing.cloudmusic.base.Song;
import com.xing.cloudmusic.http.CloudMusicApiImpl;
import com.xing.cloudmusic.util.LogUtil;
import com.xing.cloudmusic.util.ToastFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private EditText s;
    private ListView show;

    private CmAdapter adapter;

    private static final int SEARCHS_BACK = 10000;
    private static final int SEARCHID_BACK = 10001;
    private static final int DOWNLOADMUSIC = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s = findViewById(R.id.editText);
        show = findViewById(R.id.listView);
        //show.setAdapter(adapter);
        show.setOnItemClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEARCHS_BACK:
                    LogUtil.LogE("handler SEARCHS_BACK");
                    //List<String> ids = ((ResultAndCode) (msg.obj)).getID();
                    adapter = new CmAdapter(MainActivity.this,(ResultAndCode) msg.obj);
                    show.setAdapter(adapter);
                    //show.setAdapter(adapter);
                    //LogUtil.LogE("handler SEARCHS_BACK : ");
                    //searchID(ids.get(0));
                    break;
                case SEARCHID_BACK:
                    LogUtil.LogE("handler SEARCHID_BACK");
                    DataAndCode adc = (DataAndCode) msg.obj;
                    String name=null;
                    for(int i = 0 ; i < adapter.getCount() ;i++){
                        if(adc.getDataId().equals(((Song)adapter.getItem(i)).getId())){
                            name = ((Song)adapter.getItem(i)).getName();
                            break;
                        }
                    }
                    downloadFile(adc.getDataUrl(),name+"."+adc.getDataType());
                    break;
                case DOWNLOADMUSIC:
                    ToastFactory.show(MainActivity.this,"Download Finish!");
                    break;
            }
        }
    };

    public void search(View view){
        LogUtil.LogE("search begin");
        searchS(s.getText().toString());
        LogUtil.LogE("search end");
    }

    private void searchS(String s){
        LogUtil.LogE("searchS begin");
        Call<ResultAndCode> call = CloudMusicApiImpl.searchS(s);
        call.enqueue(new Callback<ResultAndCode>() {
            @Override
            public void onResponse(Call<ResultAndCode> call, Response<ResultAndCode> resp) {
                mHandler.obtainMessage(SEARCHS_BACK,resp.body()).sendToTarget();
                LogUtil.LogE("searchS response : " + resp.body());
            }

            @Override
            public void onFailure(Call<ResultAndCode> call, Throwable t) {
                LogUtil.LogE("searchS fail : " + t.getMessage());
            }
        });
        LogUtil.LogE("searchS end");
    }

    private void searchID(String id){
        LogUtil.LogE("searchID begin");
        Call<DataAndCode> call = CloudMusicApiImpl.searchID(id);
        call.enqueue(new Callback<DataAndCode>() {
            @Override
            public void onResponse(Call<DataAndCode> call, Response<DataAndCode> resp) {
                mHandler.obtainMessage(SEARCHID_BACK,resp.body()).sendToTarget();
                LogUtil.LogE("searchID response : " + resp.body());
            }

            @Override
            public void onFailure(Call<DataAndCode> call, Throwable t) {
                LogUtil.LogE("searchID fail : " + t.getMessage());
            }
        });
        LogUtil.LogE("searchID end");
    }

    private void downloadFile(String url, final String name){
        LogUtil.LogE("download begin");
        Call<ResponseBody> call = CloudMusicApiImpl.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> resp) {
                LogUtil.LogE("on Response start");
                try {
                    File file = new File(Environment.getExternalStorageDirectory()+"/"+name);

                    OutputStream os = new FileOutputStream(file);
                    InputStream is = resp.body().byteStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    while(( len = is.read(bytes) )!= -1 )
                        os.write(bytes,0,len);

                    is.close();
                    os.close();

                    mHandler.obtainMessage(DOWNLOADMUSIC).sendToTarget();
                    LogUtil.LogE("download end");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    LogUtil.LogE("file not found" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.LogE("io");
                }

                LogUtil.LogE("on Response end");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.LogE("on failure");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song song = (Song)show.getItemAtPosition(position);
        searchID(song.getId());
    }
}
