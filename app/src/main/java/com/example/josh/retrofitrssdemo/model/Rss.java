package com.example.josh.retrofitrssdemo.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Josh on 4/21/2016.
 */
@Root(name = "rss", strict = false)
public class Rss {
    @Element(name = "channel")
    private Channel channel;
    @Attribute
    private String version;

    public Channel getChannel ()
    {
        return channel;
    }
}

