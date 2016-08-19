package com.example.gtb.network;

import com.example.gtb.model.Item;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {

    //https://api.douban.com/v2/movie/top250?start=0&count=10
    @GET("top250")
    Observable<HttpResult<List<Item>>> getTopMovie(@Query("start") int start, @Query("count") int count);
}
