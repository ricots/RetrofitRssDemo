package com.example.josh.retrofitrssdemo.network;

import com.example.josh.retrofitrssdemo.model.Rss;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Josh on 4/21/2016.
 */
public interface RssInterface {

    @GET("documents/publications/RssFeeds/billupdate.xml")
    Call<Rss> getBillItems();
}
