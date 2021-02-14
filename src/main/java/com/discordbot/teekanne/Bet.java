package com.discordbot.teekanne;

public class Bet {
    private final User _user;
    private boolean _choice;
    private long _points;

    public Bet(User user, boolean choice, long points){
        this._user = user;
        this._choice = choice;
        this._points = points;
    }

    public User getUser(){
        return this._user;
    }

    public void setChoice(boolean choice){
        this._choice = choice;
    }

    public boolean getChoice(){
        return this._choice;
    }

    public void setPoints(long points){
        this._points = points;
    }

    public long getPoints(){
        return this._points;
    }
}
