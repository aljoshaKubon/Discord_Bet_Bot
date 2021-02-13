package com.discordbot.teekanne;

import javax.persistence.*;

@Entity
@Table(name="Votes")
public class Vote {

    @Id
    @GeneratedValue
    @Column(name="VoteId")
    private long _id;

    @Column(name="Title")
    private String _title;

    @Column(name="active")
    private boolean _active;

    public Vote(){
    }

    public Vote(String title){
        this._title = title;
    }

    protected long getId(){
        return this._id;
    }

    protected String getTitle(){
        return this._title;
    }

    protected void setActive(boolean active){
        this._active = active;
    }

    protected boolean isActive(){
        return this._active;
    }
}
