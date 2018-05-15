package com.example.android.dailynewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for article data from the a news dataset
     * old "https://content.guardianapis.com/search?api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c"
     * <p>
     * Technology - "http://content.guardianapis.com/search?from-date=2018-05-01&q=technology&api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c"
     * Sports - "http://content.guardianapis.com/search?from-date=2018-05-01&q=sports&api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c"
     * Politics - "http://content.guardianapis.com/search?from-date=2018-05-01&q=politics&api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c"
     * Finance - "http://content.guardianapis.com/search?from-date=2018-05-01&q=finance&api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c"
     * "http://content.guardianapis.com/search?from-date=2018-05-01&q=sports&api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c";
     * "http://content.guardianapis.com/search?from-date=2018-05-01&show-elements=image&q=top%20headlines&api-key=3710fd83-82fb-4f06-81d0-a8911c3d6f2c";
     */
    private static final String NEWS_REQUEST_URL = "http://content.guardianapis.com/search";


    static final String API_KEY = "3ff3a4bf-bffa-43b7-a448-c2d0f363a0e7";
    static final String URL_Q = "q";
    static final String URL_SHOW_FIELDS = "show-fields";
    static final String URL_BY_LINE_THUMBNAIL = "byline,thumbnail";
    static final String URL_FORMAT = "format";
    static final String URL_JSON = "json";
    static final String URL_ORDER_BY = "order-by";
    static final String URL_API_KEY = "api-key";


    /**
     * Constant value for the news article loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news articles.
     */
    private NewsAdapter mAdapter;

    /**
     * NetworkInfo to check the current status of the network.
     */
    private NetworkInfo mNetworkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test: News Activity onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the ConnectivityManager to check stat of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
            assert connMgr != null;
            mNetworkInfo =connMgr.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (mNetworkInfo == null || !mNetworkInfo.isConnected() ||
                (mNetworkInfo.getType() != connMgr.TYPE_WIFI
                && mNetworkInfo.getType() != connMgr.TYPE_MOBILE)){
            TextView emptyView = findViewById(R.id.empty_view);
            emptyView.setText(R.string.no_internet);
            ProgressBar progressBar = findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            mAdapter = new NewsAdapter(this, new ArrayList<News>());
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(mAdapter);
            listView.setEmptyView(findViewById(R.id.empty_view));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News news = mAdapter.getItem(position);
                    String url = null;
                    try {
                        assert news != null;
                        url = news.getUrl();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    Intent intent =new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) !=null) {
                        startActivity(intent);
                    }
                }
            });
        }

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String keyword = sharedPreferences.getString(
                getString(R.string.settings_keyword_value),
                getString(R.string.settings_keyword_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter(URL_SHOW_FIELDS, URL_BY_LINE_THUMBNAIL);
        builder.appendQueryParameter(URL_Q, keyword);
        builder.appendQueryParameter(URL_FORMAT, URL_JSON);
        builder.appendQueryParameter(URL_API_KEY, API_KEY);
        builder.appendQueryParameter(URL_ORDER_BY, orderBy);

        Log.i(LOG_TAG, "Test: onCreateLoader() called...");

        // crate a new loader for the given URL
        return new NewsLoader(this, builder.build().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        Log.i(LOG_TAG, "Test: onLoadFinished() called...");

        //hide loading indicator because the data has been loaded
        ProgressBar progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.INVISIBLE);

        // set empty state text to display "No articles found."
        TextView emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.empty_view);

        // clear the adapter of previous news data
        mAdapter.clear();

        // if there is a value list of {@link News}, them to the adapter's
        // data set. This will trigger the ListView to update
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i(LOG_TAG, "Test: onLoaderReset() called..");

        // loader reset, so we can clear out existing data
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.news_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, FilterFragment.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}