package com.example.lotus.models;

public class Post {
    int id;
    String name, time, caption, likes, comments, postpic;
    String propic;

    public Post(int id, String name, String time, String caption, String likes, String comments, String postpic, String propic) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.caption = caption;
        this.likes = likes;
        this.comments = comments;
        this.postpic = postpic;
        this.propic = propic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPostpic() {
        return postpic;
    }

    public void setPostpic(String postpic) {
        this.postpic = postpic;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }
}
