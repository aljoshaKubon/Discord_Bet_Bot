import com.discordbot.teekanne.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CommandHandler {

    protected static void delegateCommand(MessageReceivedEvent event, String[] command){
        switch(command[1].toLowerCase()){
            case "help":
                printHelp(event);
                break;
            case "leaderboard":
                printLeaderboard(event);
                break;
            case "join":
                join(event);
                break;
            case "leave":
                leave(event);
                break;
            case "bet":
                bet(event, command);
                break;
            case "set":
                set(event, command);
                break;
            case "score":
                score(event);
                break;
        }
    }

    private static void printHelp(MessageReceivedEvent event){
        String msg;
        msg = "!leaderboard\n!join\n!leave\n!bet";
        event.getChannel().sendMessage(msg).queue();
    }

    private static void printLeaderboard(MessageReceivedEvent event){
        System.out.println(event.getMember().getId());
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
            Transaction transaction = null;
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
            msg = "Dein Score beträgt: " + user.getScore();
        }else{
            msg = "Du musst dich erst mit !join anmelden um deinen Score anzeigen zu lassen.";
        }
        event.getChannel().sendMessage(msg).queue();
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
}
