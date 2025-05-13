package com.slash.tizilandbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class Application {

    private static DataSource dataSource;
    private static JDA jda;

    public static void main(String[] args) throws IOException, InterruptedException {
        String env = System.getenv("app.profiles.active");
        String propertiesFilename;
        if (env == null) {
            propertiesFilename = "application.properties";
        }
        else {
            propertiesFilename = "application-" + env + ".properties";
        }

        Properties props = new Properties();
        try {
            props.load(Application.class.getClassLoader().getResourceAsStream(propertiesFilename));
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        System.setProperty("embed.color", props.getProperty("embed.color", "#5566f2"));
        System.setProperty("embed.event_over_color", props.getProperty("embed.event_over_color", "#e74d3c"));
        System.setProperty("general_channel_id", props.getProperty("general_channel_id"));
        System.setProperty("active_channel_id", props.getProperty("active_channel_id"));
        System.setProperty("prefix", props.getProperty("prefix", "t!"));
        System.setProperty("points_role_id", props.getProperty("points_role_id"));
        System.setProperty("event_chance", props.getProperty("event_chance", "1.6"));

        dataSource = createDataSource(props);
        try {
            jda = createJda(props);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }

        TizilandScheduler scheduler = new TizilandScheduler();
        scheduler.start();
        System.out.println("Bot started successfully!");
    }

    private static DataSource createDataSource(Properties props) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(props.getProperty("database.username"));
        dataSource.setPassword(props.getProperty("database.password"));
        dataSource.setDatabaseName(props.getProperty("database.name"));
        dataSource.setServerNames(new String[]{ props.getProperty("database.server") });
        return dataSource;
    }

    private static JDA createJda(Properties props) throws InterruptedException {
        JDA jda = JDABuilder.createDefault(props.getProperty("bot.token"))
                .setActivity(Activity.playing("Join Tiziland!! - " + System.getProperty("prefix") + "help"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new TizilandBotListener())
                .build();
        jda.awaitReady();
        return jda;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static JDA getJda() {
        return jda;
    }
}
