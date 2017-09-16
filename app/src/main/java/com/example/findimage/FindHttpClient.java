package com.example.findimage;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FindHttpClient {
    private static final String URL = "https://www.googleapis.com/customsearch/v1?";
    private static final String APIKEY = "AIzaSyBUYh-H3NXdbYduvTFSO6s346xJPIUwAwo";
    private static final String CXKEY = "012906612677898706264:pejb1jkcbfw";
    private AsyncHttpClient httpClient;

    public FindHttpClient(){
        this.httpClient = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl){
        return URL + relativeUrl;
    }

    public String getAttr (ImageInfo imageAttr){
        String attr = "";
            if (imageAttr.getColor() != null && !imageAttr.getColor().equals("Any")){
                attr += "&imgDominantColor=" + imageAttr.getColor();
            }
            if (imageAttr.getSize() != null && !imageAttr.getSize().equals("Any")){
                attr += "&imgSize=" + imageAttr.getSize();
            }
            if (imageAttr.getType() != null && !imageAttr.getType().equals("Any")){
                attr += "&imgType=" + imageAttr.getType();
            }
            if (imageAttr.getSite() != null && !imageAttr.getSite().equals("")){
                attr += "&siteSearch=" + imageAttr.getSite();
            }

        return attr;
    }
    public void getSearch(final String query, int startPage, ImageInfo imageAttr, Context context, JsonHttpResponseHandler handler ){
        try {
            String url = getApiUrl("q="
                    + URLEncoder.encode(query,"utf-8")
                    + "&start="
                    + startPage
                    + "&cx="
                    + CXKEY
                    + "&searchType=image"
                    + getAttr(imageAttr)
                    + "&key="
                    + APIKEY);
            httpClient.get(url, handler);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            Toast.makeText(context, R.string.not_found, Toast.LENGTH_SHORT).show();
        }
    }
}