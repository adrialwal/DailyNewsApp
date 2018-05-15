package com.example.android.dailynewsapp;

class News {

    /**
     * the image of the new article
     */
    private String mImage;

    /**
     * the title of the news article
     */
    private String mTitle;

    /**
     * the section of the news article
     */
    private String mSection;

    /**
     * the date of the new article
     */
    private String mDate;

    /**
     * the website (URL) of the news article
     */
    private String mUrl;


    /**
     * constructs a new {@link News} object
     *
     * @param image     is the image of the news article
     * @param title     is the title of the news article
     * @param section   is the section of the news article
     * @param date      is the date of the new article
     * @param url       is the website (URL) of the news article
     */
    public News(String image,String title, String section, String date, String url) {
        mImage = image;
        mTitle = title;
        mSection = section;
        mDate = date;
        mUrl = url;
    }

    /**
     * Returns the image of the news article
     */
    public String getImage(){
        return mImage;
    }

    /**
     * Returns the title of the news article
     */
    public String getTitle(){
        return mTitle;
    }

    /**
     * Returns the section of the news article
     */
    public String getSection(){
        return mSection;
    }

    /**
     * Returns the date of the news article
     */
    public String getDate(){
        return mDate;
    }

    /**
     * Returns the website (URL) of the news article
     */
    public String getUrl(){
        return mUrl;
    }
}
