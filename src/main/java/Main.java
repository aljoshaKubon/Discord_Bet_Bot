import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    private static final String token = "Nzk5MzM0MDM5MjY5NjcwOTky.YACD1Q.m2yNhm-Yhmb6fQTR-1nZvhXH0i0";
    private static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException {
        jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Main())
                .setActivity(Activity.playing("Type !ping"))
                .build();
        jda.awaitReady();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        System.out.println(msg.getContentRaw());

        if(msg.getContentRaw().length() > 0){
            if(msg.getContentRaw().charAt(0) == '!'){
                String[] msgArr = msg.getContentRaw().split("!");
                CommandHandler.delegateCommand(event, msgArr);
            }
        }
    }
}