package com.example.android.dailynewsapp;

        import android.text.TextUtils;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.nio.charset.Charset;
        import java.util.ArrayList;

final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * query the Guardian API and return {@link News} objects
     */
    public static ArrayList<News> fetchNewsData(String requestUrl) {
        Log.i(LOG_TAG, "Test: fetchNewsData() called...");

        // create url object
        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        // perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        return extractArticleFromJson(jsonResponse);
    }

    /**
     * returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL");
        }
        return url;
    }

    /**
     * make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // if the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(1500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code:" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response
     */
    private static ArrayList<News> extractArticleFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding books to.
        ArrayList<News> newsList = new ArrayList<>();
        try {
            // Create a JSONObject from the JSON response jsonResponse
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Create a JSONArray from the response
            JSONObject responseJsonObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseJsonObject.getJSONArray("results");

            // For each article in the newsArray, create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get the news at the current index and crate a JSON Object
                JSONObject thisNews = resultsArray.getJSONObject(i);
                String category = thisNews.getString("sectionName");
                String title = thisNews.getString("webTitle");
                String date = thisNews.getString("webPublicationDate");
                String url = thisNews.getString("webUrl");
                String image = null;
                JSONObject object = thisNews.getJSONObject("fields");
                if (object.has("thumbnail")) {
                    image = object.getString("thumbnail");
                }
                // Add the new {@link News} to the list of articles.
                News news = new News(image, title, category, date, url);
                newsList.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from exception.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }
        // Return the list of news articles
        return newsList;
    }
}