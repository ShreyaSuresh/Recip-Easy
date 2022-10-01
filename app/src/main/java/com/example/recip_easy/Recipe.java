package com.example.recip_easy;

public class Recipe{
    String title;
    String id;
    String image;
    String estimatedTime;
    public Recipe(String title, String id, String image){
        this.title = title;
        this.id=id;
        this.image=image;
        //this.estimatedTime=estimatedTime;
    }
    public String getTitle(){
        return title;
    }
    public String getID(){
        return id;
    }
    public String getImage(){
        return image;
    }
    public String getTime(){
        return estimatedTime;
    }
}