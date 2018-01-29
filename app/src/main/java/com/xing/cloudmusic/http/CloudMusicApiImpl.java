package com.xing.cloudmusic.http;

import com.xing.cloudmusic.base.DataAndCode;
import com.xing.cloudmusic.base.ResultAndCode;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class CloudMusicApiImpl {
    private static Retrofit retrofit = null;
    private static CloudMusicApi service = null;
    public static Call<ResultAndCode> searchS(String s){
        if(retrofit == null) retrofit = retrofitFactory();
        if(service == null) service = retrofit.create(CloudMusicApi.class);


        Call<ResultAndCode> call = service.searchS(s,1,"search",5);
        return call;
    }

    public static Call<DataAndCode> searchID(String id){
        if(retrofit == null) retrofit = retrofitFactory();
        if(service == null) service = retrofit.create(CloudMusicApi.class);

        Call<DataAndCode> call = service.searchID("song",id);
        return call;
    }

    public static Call<ResponseBody> downloadFile(String url){
        if(retrofit == null) retrofit = retrofitFactory();
        if(service == null) service = retrofit.create(CloudMusicApi.class);

        Call<ResponseBody> call = service.download(url);
        return call;
    }


    private static Retrofit retrofitFactory(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imjad.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
