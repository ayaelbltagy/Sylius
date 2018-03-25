package com.example.aya.sylius;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {
    String code ;
    String access_Token ;
    ImageView product_image ;
    TextView product_name ;
    TextView product_rating ;
    TextView product_category ;
    TextView product_description ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        product_image = findViewById(R.id.product_image);
        product_name = findViewById(R.id.product_name);
        product_rating = findViewById(R.id.product_rating);
        product_category = findViewById(R.id.category);
        product_description = findViewById(R.id.description);
        Intent i = getIntent();
        if ( i != null){
            code = i.getExtras().getString("code");
            access_Token = i.getExtras().getString("access_token");
            Toast.makeText(this ,code ,Toast.LENGTH_LONG).show();
        }else {

        }
       getProductDetails ();
    }
    // Product details method
    public void getProductDetails (){
        String url = "http://office.businessboomers.net:666/dresscode/web/app_dev.php/api/v1/products/"+code;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               String basePath = "http://office.businessboomers.net:666/dresscode/web/media/image/";
                try {
                           JSONObject jsonObject = new JSONObject(response);
                            String name = jsonObject.getString("name");
                            for(int i =0 ; i<jsonObject.length() ; i++){
                            JSONArray images = jsonObject.getJSONArray("images");
                            JSONObject mainTaxon = jsonObject.getJSONObject("mainTaxon");
                            String category = mainTaxon.getString("name");
                            product_category.setText("Category: "+category);
                            String image = "";
                            if (images.length() > 0) {
                                JSONObject imgObject = images.getJSONObject(0);
                                image = basePath + imgObject.getString("path");
                                Picasso.with(ProductDetails.this).load(image).into(product_image);
                            }
                            product_name.setText(name);
                            JSONObject root = mainTaxon.getJSONObject("root");
                            JSONObject translations = root.getJSONObject("translations");
                            JSONObject en_US =translations.getJSONObject("en_US");
                            String description = en_US .getString("description");
                            product_description.setText(description);

                                                              }
                    } catch (JSONException e1) {
                    e1.printStackTrace();
                }


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
                headers.put("Authorization","Bearer "+access_Token);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);    }

    }

