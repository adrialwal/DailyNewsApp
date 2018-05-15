package com.example.android.dailynewsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    /**
     * constructs a new {@link NewsAdapter}
     *
     * @param context of the app
     * @param news    is the list of new articles, which is the data source of the adapter
     */
    public NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }

    /**
     * returns a list item view that displays information about the article
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list, parent, false);
        }

        // find the article at the given position in the list
        final News newArticle = getItem(position);

        // find the TextView with the view ID section
        assert newArticle != null;
        String sectionName = newArticle.getSection();
        TextView sectionTv = listItemView.findViewById(R.id.section);
        sectionTv.setText(sectionName);

        // find the TextView with the view ID date
        String date = newArticle.getDate();
        String findSeparator = "T";
        String[] splitText = date.split(findSeparator);
        String correctedDate = splitText[0];
        Date convertDate = new Date();
        Locale current = getContext().getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL d, yyyy", current);
        try {
            convertDate = dateFormat.parse(correctedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String displayedDate = dateFormat.format(convertDate);

        TextView newsDate = listItemView.findViewById(R.id.date);
        newsDate.setText(displayedDate);

        // find the TextView with the view ID title
        String title = newArticle.getTitle();
        TextView newsTitle = listItemView.findViewById(R.id.title);
        newsTitle.setText(title);

        // find the ImageView with the view ID image
        ImageView image = listItemView.findViewById(R.id.image_thumbnail);
        if (newArticle.getImage() != null) {
            Glide.with(getContext())
                    .load(newArticle.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        return listItemView;
    }
}