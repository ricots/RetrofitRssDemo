package com.example.josh.retrofitrssdemo.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Josh on 4/21/2016.
 */
@Root(name = "channel", strict = false)
public class Channel {

    @ElementList(name = "item", inline = true)
    private List<Item> mItems;

    @Element(required = false)
    private String pubDate;
    @Element(required = false)
    private String title;
    @Element(required = false)
    private String managingEditor;
    @Element(required = false)
    private String description;
    @Element(required = false)
    private String docs;
    @Element(required = false)
    private String link;
    @Element(required = false)
    private String lastBuildDate;
    @Element(required = false)
    private String generator;
    @Element(required = false)
    private String language;
    @Element(required = false)
    private String webMaster;


    public List<Item> getItems(){
        return mItems;
    }

}
