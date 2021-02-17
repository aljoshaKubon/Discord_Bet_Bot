import com.discordbot.teekanne.Bet;
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
        switch (command[0].toLowerCase()) {
            case "help" -> printHelp(event);
            case "leaderboard" -> printLeaderboard(event);
            case "join" -> join(event);
            case "leave" -> leave(event);
            case "vote" -> vote(event, command);
            case "bet" -> bet(event, command);
            case "set" -> set(event, command);
            case "score" -> score(event);
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
            List<User> userList = session.createNativeQuery("SELECT * FROM USERS ORDER BY SCORE DESC", User.class).list();
            for (User user : userList) {
                if (msg.length() > 0) {
                    msg.append("\n");
                }
                msg.append(user.getScore()).append(" - ").append(getNicknameById(event, user.getId()));
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
            user = new User(id);
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
                }
                event.getChannel().sendMessage("Es ist ein Fehler aufgetreten, versuchen sie es erneut oder melden sie sich bei dem Admin.").queue();
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

        if(user != null){
            Transaction transaction = null;
            try(Session session = HibernateUtils.getSessionFactory().openSession()){
                transaction = session.beginTransaction();
                session.remove(user);
                transaction.commit();
                event.getChannel().sendMessage(name + " du bist erfolgreich ausgetreten.").queue();
            } catch (Exception e){
                e.printStackTrace();
                if(transaction != null) {
                    transaction.rollback();
                }
                event.getChannel().sendMessage("Es ist ein Fehler aufgetreten, versuchen sie es erneut oder melden sie sich bei dem Admin.").queue();
            }
        }else{
            event.getChannel().sendMessage(name + " du befinden sich nicht in dem System.").queue();
        }
    }

    private static void vote(MessageReceivedEvent event, String[] command) {
        if(isAdmin(event)){
            String msg;
            switch (command[1]) {
                case "start" -> {
                    if(!VoteHandler.isActive()){
                        VoteHandler.start();
                        msg = "Vote wurde erstellt es kann nun gewettet werden.";
                    }else{
                        msg = "Es ist schon ein Vote aktiv.";
                    }
                }
                case "stop" -> {
                    if(VoteHandler.isActive()){
                        if(VoteHandler.isRunning()){
                            VoteHandler.stop();
                            msg = "Vote wurde gestoppt, es kann nicht mehr gewettet werden.";
                        }else{
                            msg = "Vote wurde schon gestoppt.";
                        }

                    }else{
                        msg = "Momentan gibt es keinen Vote der gestoppt werden kann.";
                    }
                }
                case "end" -> {
                    if(VoteHandler.isActive()){
                        VoteHandler.end(Boolean.parseBoolean(command[2]));
                        msg = "Vote wurde beendet, Gewinn wurde ausgegeben.";
                    }else{
                        msg = "Momentan gibt es keinen Vote der beendet werden kann.";
                    }

                }
                default -> msg = "Den Befehl " + command[1] + " gibt es nicht.";
            }
            event.getChannel().sendMessage(msg).queue();
        }
    }

    private static void bet(MessageReceivedEvent event, String[] command){
        User user = getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
        String msg;

        if(user != null){
            if(VoteHandler.isActive()){
                if(VoteHandler.isRunning()){
                    try{
                        if(Long.parseLong(command[2]) > 0){
                        if(user.getScore() >= Long.parseLong(command[2])){
                            if(command[1].equals("true")){
                                Bet bet = new Bet(user, true, Long.parseLong(command[2]));
                                VoteHandler.addBet(bet);
                                msg = "Deine Wette wurde aufgenommen.";
                            }else if(command[1].equals("false")) {
                                Bet bet = new Bet(user, false, Long.parseLong(command[2]));
                                VoteHandler.addBet(bet);
                                msg = "Deine Wette wurde aufgenommen.";
                            }else{
                                msg = "Konnte die Eingabe '" + command[1] + "' nicht erkennen.";
                            }
                        }else{
                            msg = "Du hast nicht so viele Punkte wie du verwetten willst.";
                        }
                    }else{
                        msg = "Bitte gib keine Zahlen kleiner eins an.";
                    }}catch(Exception e){
                        msg = "Konnte die Zahl '" + command[2] + "' nicht erkennen.";
                    }
                }else{
                    msg = "Der Vote wurde schon gestoppt, es kann nicht mehr gewettet werden.";
                }
            }else{
                msg = "Zurzeit gibt es keinen Vote auf den du wetten kannst.";
            }
        }else{
            msg = "Du " + event.getAuthor().getName() + " musst dich erst mit !join registrieren um an Votes teilzunehmen.";
        }
        event.getChannel().sendMessage(msg).queue();
    }

    private static void set(MessageReceivedEvent event, String[] command){
        if (isAdmin(event)) {
            List<Member> members = event.getGuild().getMembersByEffectiveName(command[1], true);
            if (members.size() > 0){
                if(members.size() == 1){
                    User user = getUserById(members.get(0).getIdLong());
                    if(user != null){
                        Transaction transaction;
                        user.setScore(Long.parseLong(command[2]));
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
            event.getChannel().sendMessage("Du hast nicht die erforderlichen Rechte um diesen Befehl zu nutzen.").queue();
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

    private static User getUserById(long id){
        User user = null;
        try {
            Session session = HibernateUtils.getSessionFactory().openSession();
            user = session.get(User.class, id);
        } catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    private static boolean isAdmin(MessageReceivedEvent event){
        Role role = event.getGuild().getRoleById(810130877757128704L);
        return Objects.requireNonNull(event.getMember()).getRoles().contains(role);
    }

    private static String getNicknameById(MessageReceivedEvent event, long id){
        return Objects.requireNonNull(event.getGuild().getMemberById(id).getUser().getName());
    }
}