import com.discordbot.teekanne.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandHandler {

    protected static void delegateCommand(MessageReceivedEvent event, String[] command){
        System.out.println(Arrays.toString(command));
        switch (command[0].toLowerCase()) {
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
                msg.append(getNicknameById(event, user.getId())).append(": ").append(user.getScore());
            }
            event.getChannel().sendMessage(msg.toString()).queue();
            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void join(MessageReceivedEvent event){
        long id = Objects.requireNonNull(event.getMember()).getIdLong();
        User user = getUserById(id);
        String name = event.getAuthor().getName();
        String msg;

        if(user == null){
            user = new User(id, name);
            Transaction transaction = null;

            try(Session session = HibernateUtils.getSessionFactory().openSession()){
                transaction = session.beginTransaction();
                session.save(user);
                transaction.commit();
                msg = name + " du hast dich erfolgreich registriert.";
                event.getChannel().sendMessage(msg).queue();
            } catch (Exception e){
                if (transaction != null){
                    transaction.rollback();
                    msg = "Es ist ein Fehler aufgetreten, versuchen sie es erneut oder melden sie sich bei dem Admin.";
                    event.getChannel().sendMessage(msg).queue();
                }
                e.printStackTrace();
            }
        }else{
            msg = name + " du hast dich bereits registriert.";
            event.getChannel().sendMessage(msg).queue();
        }
    }

    private static void leave(MessageReceivedEvent event){
        long id = Objects.requireNonNull(event.getMember()).getIdLong();
        User user = getUserById(id);
        String name = event.getAuthor().getName();
        String msg;

        if(user != null){
            Transaction transaction;
            try(Session session = HibernateUtils.getSessionFactory().openSession()){
                transaction = session.beginTransaction();
                session.remove(user);
                transaction.commit();
                msg = name + " du bist erfolgreich ausgetreten.";
                event.getChannel().sendMessage(msg).queue();
            } catch (Exception e){
                e.printStackTrace();
            }
        }else{
            msg = name + " du befinden sich nicht in dem System.";
            event.getChannel().sendMessage(msg).queue();
        }
    }

    private static void bet(MessageReceivedEvent event, String[] command){
        //bet command for
    }

    private static void set(MessageReceivedEvent event, String[] command){
        Role role = event.getGuild().getRoleById(810130877757128704L);
        User user;
        if (Objects.requireNonNull(event.getMember()).getRoles().contains(role)) {
            List<Member> members = event.getGuild().getMembersByEffectiveName(command[1], true);
            if (members.size() > 0){
                if(members.size() == 1){
                    user = getUserById(members.get(0).getIdLong());
                    if(user != null){
                        Transaction transaction;
                        user.setScore(Integer.parseInt(command[2]));
                        try(Session session = HibernateUtils.getSessionFactory().openSession()){
                            transaction = session.beginTransaction();
                            session.update(user);
                            transaction.commit();
                            event.getChannel().sendMessage("Der score von " + members.get(0).getUser().getName() + " wurde auf " + command[2] + " gesetzt.").queue();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        event.getChannel().sendMessage("Dieses Mitglied ist nicht registriert.").queue();
                    }
                }else{
                    event.getChannel().sendMessage("Users: " + members.toString()).queue();
                }
            }else {
                event.getChannel().sendMessage("Mitglied konnte nicht gefunden werden.").queue();
            }
        }else{
            event.getChannel().sendMessage("Du hast nicht die erforderlichen Rechte für diese Operation.").queue();
        }
    }

    private static void score(MessageReceivedEvent event){
        long id = Objects.requireNonNull(event.getMember()).getIdLong();
        String msg;
        String name = event.getAuthor().getName();
        User user = getUserById(id);

        if (user != null){
            msg = "Dein Score: " + user.getScore();
        }else{
            msg = name + " du musst dich erst mit !join anmelden um deinen Score anzeigen zu lassen.";
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

    private static String getNicknameById(MessageReceivedEvent event, long id){
        return Objects.requireNonNull(event.getGuild().getMemberById(id)).getNickname();
    }
}
