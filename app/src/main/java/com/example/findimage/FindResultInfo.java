package com.example.findimage;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindResultInfo implements Parcelable {
    private String url;
    private String thumb;
    private String title;

    public FindResultInfo(Parcel in) {
        readFromParcel(in);
    }

    public String getFullUrl() {
        return url;
    }

    public String getThumbUrl() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }

    public FindResultInfo(JSONObject json) {
        try {
            this.url = json.getString("link");
            this.thumb = json.getJSONObject("image").getString("thumbnailLink");
            this.title = json.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FindResultInfo> fromJSONArray(JSONArray array) {
        ArrayList<FindResultInfo> results = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new FindResultInfo(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public FindResultInfo createFromParcel(Parcel in) {
            return new FindResultInfo(in);
        }

        @Override
        public FindResultInfo[] newArray(int size) {
            return new FindResultInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(thumb);
        dest.writeString(title);
    }


    public void readFromParcel(Parcel in) {
        url = in.readString();
        thumb = in.readString();
        title = in.readString();
    }
}