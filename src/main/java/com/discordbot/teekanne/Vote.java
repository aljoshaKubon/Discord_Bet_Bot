package com.discordbot.teekanne;

import java.util.ArrayList;
import java.util.List;

public class Vote {
    private static boolean active = false;
    private static boolean running = false;
    private static List<Bet> betList = new ArrayList<Bet>();

    public static void start(){
        if(!active){
            active = true;
            running = true;
        }
    }

    public static void stop(){
        if(running){
            running = false;
        }
    }

    public static void end(){
        if(active && !running){
            active = false;
        }
    }

    public static void addBet(Bet bet){
        if(active && running) {
            betList.add(bet);
            System.out.println(betList.toString());
        }
    }

    public static boolean isActive(){
        return active;
    }

    public static boolean isRunning(){
        return running;
    }

    public static List<Bet> getBetList(){
        return betList;
    }
}
