package com.example.aya.sylius;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by aya on 3/21/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter <ProductsAdapter.ProductHolder> {

    List<OneProduct> productList;
    Context context;

    public  ProductsAdapter (List<OneProduct> productList, Context context){
        this.productList = productList ;
        this.context = context;
    }
    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        ProductHolder holder = new ProductHolder (v);
        return holder ;
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        OneProduct  list = productList.get(position);
        holder.product_name.setText(list.getName());
        try {
            Picasso.with(context).load(list.getimg_url()).error(R.drawable.sylius).into(holder.product_image);

        }
        catch (Exception e) {
            holder.product_image.setImageResource(R.drawable.sylius);
            // this set the default img source if the path provided in .load is null or some error happened on download.
        }

        holder.product_rating.setText(list.getRating());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        TextView product_name ,product_rating  ;
        ImageView product_image ;

    public ProductHolder(View itemView) {
        super(itemView);
        product_name =  itemView.findViewById(R.id.product_name);
        product_rating = itemView.findViewById(R.id.product_rating);
        product_image = itemView.findViewById(R.id.product_image);

    }
}
}
