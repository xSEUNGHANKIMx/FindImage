package com.example.findimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends FragmentActivity {

    private static int MAX_PAGE = 10;
    private EditText etQuery;
    private GridView gvResults;
    private ImageButton btnSearch;
    private ArrayList<FindResultInfo> imageFindResults;
    private ImageResultArrayAdapter imageAdapter;
    private FindHttpClient client;
    private int startPage = 1;
    private String query;
    private ImageInfo imageAttr = new ImageInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        setupViews();
    }

    public void setupViews(){
        etQuery = (EditText) findViewById(R.id.edittextFind);
        gvResults = (GridView) findViewById(R.id.gridView);
        btnSearch = (ImageButton) findViewById(R.id.searchBtn);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                onImageSearch(1);
            }
        });

        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard(v);
                    onImageSearch(1);
                    return true;
                }
                return false;
            }
        });

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ViewImageActivity.class);
                FindResultInfo imageFindResult = imageFindResults.get(position);
                i.putExtra("result", imageFindResult);
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new CustomScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (page <= MAX_PAGE) {
                    onImageSearch((MAX_PAGE*(page-1)) + 1);
                }
            }
        });

        imageFindResults = new ArrayList<>();
        imageAdapter = new ImageResultArrayAdapter(this, imageFindResults);
        gvResults.setAdapter(imageAdapter);
    }

    public void onImageSearch(int start) {

        if (isNetworkAvailable()) {
            client = new FindHttpClient();
            query = etQuery.getText().toString();
            startPage = start;
            if (startPage == 1)
                imageAdapter.clear();

            if (!query.equals("")) {
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                if(startPage == 1) {
                    dialog.setMessage("Please wait...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

                client.getSearch(query, startPage, imageAttr, this, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    JSONArray imageJsonResults;
                                    if (response != null) {
                                        imageJsonResults = response.getJSONArray("items");
                                        if (imageJsonResults.length() > 0) {
                                            imageAdapter.addAll(FindResultInfo.fromJSONArray(imageJsonResults));
                                        }
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), R.string.error_invalid_data, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } finally {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                Toast.makeText(getApplicationContext(), R.string.error_service_unavailable, Toast.LENGTH_SHORT).show();

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }
                );
            }
            else {
                Toast.makeText(this, R.string.error_no_input, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,R.string.error_no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class ImageResultArrayAdapter extends ArrayAdapter<FindResultInfo> {

        public ImageResultArrayAdapter (Context context, List<FindResultInfo> images){
            super(context, android.R.layout.simple_list_item_1, images);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FindResultInfo imageFindResult = getItem(position);
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.image_item_layout, parent, false);
            }
            ImageView ivImage = (ImageView) convertView.findViewById(R.id.imageItemView);
            ivImage.setImageResource(0);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.imageItemTitle);

            tvTitle.setText(Html.fromHtml(imageFindResult.getTitle()));
            Picasso.with(getContext()).load(imageFindResult.getThumbUrl()).into(ivImage);
            return convertView;
        }
    }
}