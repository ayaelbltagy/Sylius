package com.example.aya.sylius;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Products extends AppCompatActivity {
    RecyclerView recyclerView;
    List<OneProduct> list;
    SharedPreferences preferences;
    String access_Token_1;
    String refresh_token_1;
    long token_expired_time;
    String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        updateProducts ();
        preferences = getPreferences(Context.MODE_PRIVATE);
        access_Token_1 = preferences.getString("prefAccessToken", null);
        refresh_token_1 = preferences.getString("prefRefreshToken", null);
        if (token_expired_time != 0) {
            long expTime = Calendar.getInstance().getTimeInMillis() - token_expired_time;
            if (expTime > 3600000) {
                refreshToken();
            }
        }

        if (access_Token_1 == null) {
            accessToken();
        } else {
            getProducts();
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(Products.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(Products.this, ProductDetails.class);
                        i.putExtra("code", list.get(position).getCode());
                        i.putExtra("access_token",access_Token_1);
                        Log.i("tokenActivity1",access_Token_1);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
        Log.i("token",access_Token_1);
    }
    private void updateProducts() {
        if (isOnline()) {
            accessToken();
        } else {
            Toast.makeText(Products.this, "Check your internet connection !", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Products.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // Refresh Token method
    public void refreshToken() {
        String client_id_and_client_secret = "client_id=46v3htox13uookw4o8c8gs44oggocgos88804oggggkwss8o04&client_secret=4jm5k8h9vxmokkssw4wkcsgs0cws0kow0w48s8gc80cwc404g0";
        String grant_type = "&grant_type=refresh_token&";
        String url = "http://office.businessboomers.net:666/dresscode/web/app_dev.php/api/oauth/v2/token?" + client_id_and_client_secret + grant_type + "refresh_token=" + refresh_token_1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    SharedPreferences.Editor editor = preferences.edit();
                    access_Token_1 = jsonObject.getString("access_token");
                    refresh_token_1 = jsonObject.getString("refresh_token");
                    editor.putString("prefAccessToken", access_Token_1);
                    editor.putString("prefRefreshToken", refresh_token_1);
                    editor.putString("prefExpireIn", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    editor.apply();
                    getProducts();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> prams = new HashMap<>();
                prams.put("client_id", "46v3htox13uookw4o8c8gs44oggocgos88804oggggkwss8o04");
                prams.put("client_secret", "4jm5k8h9vxmokkssw4wkcsgs0cws0kow0w48s8gc80cwc404g0");
                prams.put("grant_type", "password");
                prams.put("username", "api@example.com");
                prams.put("password", "api");
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Get All Products Method
    public void getProducts() {
        String url = "http://office.businessboomers.net:666/dresscode/web/app_dev.php/api/v1/products/";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("res", response);
                String basePath = "http://office.businessboomers.net:666/dresscode/web/media/image/";
                OneProduct product = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject("_embedded");
                    JSONArray jsonArray = object.getJSONArray("items");
                    list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject elements = jsonArray.getJSONObject(i);
                        JSONArray imgsArray = elements.getJSONArray("images");
                        String name =  elements.getString("name");
                        code = elements.getString("code");
                        String rating = "Rating:" + elements.getString("averageRating");
                        String image = "";
                        if (imgsArray.length() > 0) {
                            JSONObject imgObject = imgsArray.getJSONObject(0);
                            image = basePath + imgObject.getString("path");
                        }

                        product = new OneProduct(image, name, rating ,code);
                        list.add(product);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ProductsAdapter adapter = new ProductsAdapter(list, Products.this);
                recyclerView.setAdapter(adapter);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
            }
        }) {

            //This is for Headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + access_Token_1);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    //Access Token Method
    public void accessToken() {
        String url = "http://office.businessboomers.net:666/dresscode/web/app_dev.php/api/oauth/v2/token";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    SharedPreferences.Editor editor = preferences.edit();
                    access_Token_1 = jsonObject.getString("access_token");
                    refresh_token_1 = jsonObject.getString("refresh_token");
                    editor.putString("prefAccessToken", access_Token_1);
                    editor.putString("prefRefreshToken", refresh_token_1);
                    editor.putString("prefExpireIn", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    editor.apply();
                    getProducts();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> prams = new HashMap<>();
                prams.put("client_id", "46v3htox13uookw4o8c8gs44oggocgos88804oggggkwss8o04");
                prams.put("client_secret", "4jm5k8h9vxmokkssw4wkcsgs0cws0kow0w48s8gc80cwc404g0");
                prams.put("grant_type", "password");
                prams.put("username", "api@example.com");
                prams.put("password", "api");
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}