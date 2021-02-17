package com.discordbot.teekanne;


import javax.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @Column(name = "UserId")
    private long _id;

    @Column(name = "Score")
    private long _score;

    public User(){
    }

    public User(long userId){
        this._id = userId;
        this._score = 100;
    }

    public long getId(){
        return this._id;
    }

    public long getScore(){
        return this._score;
    }

    public void setScore(long score){
        this._score = score;
    }
}
