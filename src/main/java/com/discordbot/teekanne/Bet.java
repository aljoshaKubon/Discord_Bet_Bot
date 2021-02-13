package com.discordbot.teekanne;

import javax.persistence.*;

public class Bet {

    @Id
    @GeneratedValue
    @Column(name="BetId")
    private long _id;

    @ManyToOne
    @JoinColumn(name = "BetVoteId", referencedColumnName = "VoteId")
    private Vote _voteId;

    @ManyToOne
    @JoinColumn(name = "BetUserId", referencedColumnName = "UserId")
    private User _userId;

    private long _points;

    public Bet(){

    }
}
