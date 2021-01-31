import com.discordbot.teekanne.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CommandHandler {

    protected static void delegateCommand(MessageReceivedEvent event, String[] command){
        switch (command[1].toLowerCase()) {
            case "help" -> printHelp(event);
            case "leaderboard" -> printLeaderboard(event);
            case "join" -> join(event);
            case "leave" -> leave(event);
            case "bet" -> bet(event, command);
            case "set" -> set(event, command);
            case "score" -> score(event);
            case "vote" -> vote(event);
        }
    }

    private static void printHelp(MessageReceivedEvent event){
        String msg;
        msg = "!leaderboard\n!join\n!leave\n!bet";
        event.getChannel().sendMessage(msg).queue();
    }

    private static void printLeaderboard(MessageReceivedEvent event){
        StringBuilder msg = new StringBuilder();
        Session session = HibernateUtils.getSessionFactory().openSession();

        Transaction tx = session.beginTransaction();
        try {
            List<User> userList = session.createNativeQuery("SELECT * FROM USERS", User.class).list();
            for (User user : userList) {
                if (msg.length() > 0) {
                    msg.append("\n");
                }
                msg.append(user.getName()).append(": ").append(user.getScore());
            }
            event.getChannel().sendMessage(msg.toString()).queue();
            tx.commit();
        }catch (Exception e){

        }
    }

    private static void join(MessageReceivedEvent event){
        long id = event.getMember().getIdLong();
        String name = event.getAuthor().getName();

        if(getUserById(id) == null){
            User user = new User(id, name);
            Transaction transaction = null;

            try(Session session = HibernateUtils.getSessionFactory().openSession()){
                transaction = session.beginTransaction();
                session.save(user);
                transaction.commit();
            } catch (Exception e){
                if (transaction != null){
                    transaction.rollback();
                }
                e.printStackTrace();
            }

            //TODO: send message to user
        }else{
            System.out.println("User already exists.");
            //TODO: Send message to user
        }

    }

    private static void leave(MessageReceivedEvent event){
        long id = event.getMember().getIdLong();

        User user = getUserById(id);
        if(user != null){
            Transaction transaction;
            try(Session session = HibernateUtils.getSessionFactory().openSession()){
                transaction = session.beginTransaction();
                session.remove(user);
                transaction.commit();
            } catch (Exception e){
                e.printStackTrace();
            }
        }else{
            //TODO: Send message to user
        }
    }

    private static void bet(MessageReceivedEvent event, String[] command){
        //bet command for
    }

    private static void set(MessageReceivedEvent event, String[] command){
        //Admin command to set the score of a player or himself
    }

    private static void score(MessageReceivedEvent event){
        long id = event.getMember().getIdLong();
        String msg;
        User user = getUserById(id);
        if (user != null){
            msg = "Dein Score: " + user.getScore();
        }else{
            msg = "Du musst dich erst mit !join anmelden um deinen Score anzeigen zu lassen.";
        }
        event.getChannel().sendMessage(msg).queue();
    }

    private static void vote(MessageReceivedEvent event) {
    }

    private static User getUserById(long id){
        User user;
        try {
            Session session = HibernateUtils.getSessionFactory().openSession();
            user = session.get(User.class, id);

            if(user != null){
                return user;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void sendMessageToChat(String msg){

    }
}
