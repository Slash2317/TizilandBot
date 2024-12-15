package com.slash.tizilandbot;

import com.slash.tizilandbot.domain.Data;
import com.slash.tizilandbot.domain.DataOld;
import com.slash.tizilandbot.repository.DataOldRepository;
import com.slash.tizilandbot.repository.DataOldRepositoryImpl;
import com.slash.tizilandbot.repository.DataRepository;
import com.slash.tizilandbot.repository.DataRepositoryImpl;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
            migrateDataIfNeeded();

            JDABuilder.createDefault(props.getProperty("token"))
                    .setActivity(Activity.playing("Join Tiziland!! - t!help"))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new TizilandBotListener())
                    .build();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void migrateDataIfNeeded() {
        DataOldRepository dataOldRepository = new DataOldRepositoryImpl();
        DataRepository dataRepository = new DataRepositoryImpl();

        DataOld dataOld = dataOldRepository.loadData();
        if (dataOld == null) {
            return;
        }
        dataOldRepository.renameFolderLocation();
        Data data = Data.from(dataOld);
        dataRepository.saveData(data);
    }
}
