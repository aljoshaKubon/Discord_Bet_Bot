import com.discordbot.teekanne.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

        HibernateUtils.handleJoinCommand(id, name);
    }

    private static void leave(MessageReceivedEvent event){
        long id = event.getMember().getIdLong();

        HibernateUtils.handleLeaveCommand(id);
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
        User user = HibernateUtils.getUserById(id);
        if (user != null){
            msg = "Dein Score beträgt: " + user.getScore();
        }else{
            msg = "Du musst dich erst mit !join anmelden um deinen Score anzeigen zu lassen.";
        }
        event.getChannel().sendMessage(msg).queue();
    }
}
