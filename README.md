### Using Retrofit 2.0 on an RSS Feed. Mapping HTTP responses from XML to Java objects

Here's a link to the RSS Feed I'm using for this demo:
http://www.legislature.mi.gov/documents/publications/RssFeeds/billupdate.xml

The basic structure looks like the following:

```xml
<rss version="2.0">
  <channel>
    ...
    ...
    <item>
      <title>...</title>
      <link>...</link>
      <description>...</description>
      <pubDate>...</pubDate>
      <guid>...</guid>
    </item>
  </channel>
</rss>
```
#### dependencies
```java
    compile 'com.squareup.retrofit2:retrofit:2.0.0-beta4'
    compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'
    compile ('com.squareup.retrofit2:converter-simplexml:2.0.2'){
        exclude group: 'xpp3', module: 'xpp3'
        exclude group: 'stax', module: 'stax-api'
        exclude group: 'stax', module: 'stax'
    }
```

I then used this site to convert my XML to Java POJO Classes: http://pojo.sodhanalibrary.com/
This generated 4 classes, but only needed 3: ```Channel.java```, ```Item.java```, & ```Rss.java```

The important bits:
```java
@Root(name = "channel", strict = false)
public class Channel {
...
}

@Root(name = "item", strict = false)
public class Item {
...
}

@Root(name = "rss", strict = false)
public class Rss {
...
}
```

I only wanted to get the title, link, description, and pubDate -- but not the guid. Therefore, I needed to specify:
```java
    @Element(required = false)
    private String guid;
```
in the ```Item.java``` class. 

My ```Channel.java``` class also had fields which I didn't need, so I added ```required = false``` for those as well. 


#### RssInterface
Pretty straight forward:
```java
    @GET("documents/publications/RssFeeds/billupdate.xml")
    Call<Rss> getBillItems();
```

#### Putting it all together:
```java
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.legislature.mi.gov/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        RssInterface rssInterface = retrofit.create(RssInterface.class);
        Call<Rss> rssCall = rssInterface.getBillItems();
        rssCall.enqueue(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                Rss rss = response.body();
                recyclerView.setAdapter(new RssAdapter(MainActivity.this, rss.getChannel().getItems()));
            }
            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                Log.e("Error:: ", t.getMessage());
            }
        });
```
    
