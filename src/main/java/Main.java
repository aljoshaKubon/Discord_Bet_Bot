import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    private static final String token = "Nzk5MzM0MDM5MjY5NjcwOTky.YACD1Q.-kwhYgpp_GwCeCuWuE4RM33HZQw";

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new Main())
                .setActivity(Activity.playing("Schreibe !help"))
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
                String[] msgArr = msg.getContentRaw().substring(1).split(" ");
                CommandHandler.delegateCommand(event, msgArr);
            }
        }
    }
}