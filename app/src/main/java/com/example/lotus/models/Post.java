package com.example.lotus.models;

public class Post {
    int id;
    String name, time, caption, likes, comments, profPic;
    String hashtag[];
    MediaData media[];

    public Post(int id, String name, String time, String caption, String likes, String comments, String profPic, String[] hashtag, MediaData[] media) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.caption = caption;
        this.likes = likes;
        this.comments = comments;
        this.profPic = profPic;
        this.hashtag = hashtag;
        this.media = media;
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

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public String[] getHashtag() {
        return hashtag;
    }

    public void setHashtag(String[] hashtag) {
        this.hashtag = hashtag;
    }

    public MediaData[] getMedia() {
        return media;
    }

    public void setMedia(MediaData[] media) {
        this.media = media;
    }
}
