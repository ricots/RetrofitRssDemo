package com.example.josh.retrofitrssdemo.model;

/**
 * Created by Josh on 4/21/2016.
 */
public class MyPojo {
    private Rss rss;

    public Rss getRss() {
        return rss;
    }

    public void setRss(Rss rss) {
        this.rss = rss;
    }

    @Override
    public String toString() {
        return "ClassPojo [rss = " + rss + "]";
    }
}
