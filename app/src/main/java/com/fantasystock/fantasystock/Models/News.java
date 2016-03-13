package com.fantasystock.fantasystock.Models;

import java.util.ArrayList;

/**
 * Created by chengfu_lin on 3/5/16.
 */
public class News {
    public String id;
    public String title;
    public String summary;
    public String link;
    public String author;
    public String published;
    public String publisher;
    public String content;

    public ArrayList<Entity> entities;
    public ArrayList<Image> images;

    public class Entity {
        public String term;
        public String label;
    }

    public class Image {
        public String url;
        public String width;
        public String height;
    }
}
