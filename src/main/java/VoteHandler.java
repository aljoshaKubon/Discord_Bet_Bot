import com.discordbot.teekanne.Bet;
import com.discordbot.teekanne.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;

public class VoteHandler {
    private static boolean active = false;
    private static boolean running = false;
    private static boolean result;
    private static long winningPot;
    private static final List<Bet> betList = new ArrayList<>();

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

    public static void end(boolean r){
        if(active){
            active = false;
            running = false;
            result = r;
            System.out.println("Result: " + r);
            calculateWinningPot();
            calculateContribution();
            takePointsFromUsers();
            profitDistribution();
            clear();
        }
    }

    public static void addBet(Bet bet){
        if(active && running) {
            Bet currentBet = getBetByUser(bet.getUser());
            if (currentBet != null) {
                betList.remove(currentBet);
            }
            betList.add(bet);
        }
    }

    public static boolean isActive(){
        return active;
    }

    public static boolean isRunning(){
        return running;
    }

    private static void calculateWinningPot(){
        for(Bet bet: betList){
            winningPot += bet.getPoints();
        }
    }

    private static Bet getBetByUser(User user){
        for(Bet bet: betList){
            if(bet.getUser().equals(user)){
                return bet;
            }
        }
        return null;
    }

    private static void calculateContribution(){
        long winningChoicePot = 0;
        List<Bet> winnerBets = new ArrayList<>();

        for(Bet bet: betList){
            if(bet.getChoice() == result){
                winningChoicePot += bet.getPoints();
                winnerBets.add(bet);
            }
        }

        for(Bet bet: winnerBets){
            bet.setContributionInPercent(((float)bet.getPoints()/winningChoicePot)*100);
        }
    }

    private static void takePointsFromUsers(){
        Transaction transaction;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            for(Bet bet: betList){
                transaction = session.beginTransaction();
                bet.getUser().setScore(bet.getUser().getScore() - bet.getPoints());
                session.update(bet.getUser());
                transaction.commit();
                session.evict(bet.getUser());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void profitDistribution(){
        Transaction transaction;
        long points;
        try(Session session = HibernateUtils.getSessionFactory().openSession()){
            for(Bet bet: betList){
                if(bet.getChoice() == result){
                    transaction = session.beginTransaction();
                    points =  (long)Math.floor((winningPot * (bet.getContributionInPercent() / 100)));
                    System.out.println(points);
                    bet.getUser().setScore(bet.getUser().getScore() + points);
                    session.update(bet.getUser());
                    transaction.commit();
                    session.evict(bet.getUser());
                }
            }
        }
    }

    private static void clear(){
        winningPot = 0;
        betList.clear();
    }
}
