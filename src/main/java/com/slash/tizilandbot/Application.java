package com.slash.tizilandbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {
        String env = System.getProperty("app.profiles.active");
        if (env == null) {
            env = "dev";
        }

        Properties props = new Properties();
        try {
            props.load(Application.class.getClassLoader().getResourceAsStream("config-" + env + ".properties"));

            TizilandBotListener tizilandBotListener = new TizilandBotListener();

            JDABuilder.createDefault(props.getProperty("token"))
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(tizilandBotListener)
                    .build();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
