package com.example.android.dailynewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * tag for log message
     */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /**
     * query URL
     */
    private String mUrl;

    /**
     * constructs a new {@link NewsLoader}
     *
     * @param context   of the activity
     * @param url       to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "Test: onStartLoading() called...");

        forceLoad();
    }

    /**
     * this is a background thread
     */
    @Override
    public List<News> loadInBackground(){
        Log.i(LOG_TAG,"Test: loadInBackground() called...");

        if (mUrl == null){
            return null;
        }
        // perform the network request, parse the response, and extract a list of news articles
        List<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}