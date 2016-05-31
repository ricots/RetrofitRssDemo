package com.example.josh.retrofitrssdemo.network;

import com.example.josh.retrofitrssdemo.model.Rss;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Josh on 4/21/2016.
 */
public interface RssInterface {

    //http://www.legislature.mi.gov/documents/2015-2016/status/house/rss/2016-hb-5344rss.xml

    @GET("documents/publications/RssFeeds/billupdate.xml")
    Call<Rss> getBillItems();

    //TODO: Possible to use search?
    // status/house/rss/ = searching house bills
    // status/senate/rss/ = search senate bills
    @GET("documents/2015-2016/status/house/rss/{query}rss.xml")
    Call<Rss> searchBills(@Path("query") String query);
}
