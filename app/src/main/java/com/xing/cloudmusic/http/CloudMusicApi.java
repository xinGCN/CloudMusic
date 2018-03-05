package com.xing.cloudmusic.http;

import com.xing.cloudmusic.base.DataAndCode;
import com.xing.cloudmusic.base.ResultAndCode;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public interface CloudMusicApi {
    //查询某个关键词对应的一些数据
    //20180227发现这个api使用默认值type=1即查询单曲的时候，如果此时s为歌手的名称则会返回该歌手的歌曲，也就是说s的值并不一定包含在返回的歌曲名中，这个api一下子变得很便捷
    @GET("cloudmusic/")
    Call<ResultAndCode> searchS(@Query("s")String s , @Query("search_type")int searchType, @Query("type")String type);

    //根据id查询Id对应的文件
    @GET("cloudmusic/")
    Call<DataAndCode> searchID(@Query("type")String type, @Query("id")String id);


    @GET
    Call<ResponseBody> download(@Url String fileUrl);
}
