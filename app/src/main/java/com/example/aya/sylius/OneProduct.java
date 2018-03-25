package com.example.aya.sylius;

/**
 * Created by aya on 3/22/2018.
 */

public class OneProduct  {

    private String img_url;
    private String name;
    private String rating;
    private String code ;

    // Constructor
    public OneProduct(String img_url , String name, String rating ,String code){
        this.img_url = img_url;
        this.name = name ;
        this.rating = rating ;
        this.code = code;

    }
    // Getter and setter methods
    public String getimg_url()
    {
        return img_url;
    }

    public void setimg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRating() {

        return rating;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }
    public  void setCode (String code ){
        this.code = code;

    }
    public String getCode (){
        return code ;
    }





}
