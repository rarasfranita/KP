package com.example.lotus.models;

public class Comment (
    val comment: String,
    val username: String,
    val avatar: String,
    val time: String,
    val like: String,
    var childComment: MutableList<ChildComment>?){
}

