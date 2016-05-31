package com.example.josh.retrofitrssdemo.network;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Josh on 5/16/2016.
 */
public final class RequestManager {

    private final static RequestManager INSTANCE = new RequestManager();

    private RssInterface rssService;

    private RequestManager(){
    }

    public void initialize(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl("http://www.legislature.mi.gov/")
                .build();

        INSTANCE.rssService = retrofit.create(RssInterface.class);
    }

    public static RequestManager getInstance(){
        return INSTANCE;
    }

    public RssInterface getRssService(){
        if (INSTANCE.rssService == null){
            throw new IllegalStateException("RequestManager not initialized");
        }
        return INSTANCE.rssService;
    }
}
