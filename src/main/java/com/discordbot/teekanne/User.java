package com.discordbot.teekanne;


import javax.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @Column(name = "UserId")
    private long _id;

    @Column(name = "Name")
    private String _name;

    @Column(name = "Score")
    private int _score;

    public User(){
    }

    public User(long userId, String name){
        this._id = userId;
        this._name = name;
        this._score = 100;
    }

    public long getId(){
        return this._id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public int getScore(){
        return this._score;
    }

    public void setScore(int score){
        this._score = score;
    }
}
