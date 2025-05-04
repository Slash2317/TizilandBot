package com.slash.tizilandbot;

import com.slash.tizilandbot.service.PointEventService;
import com.slash.tizilandbot.service.PointEventServiceImpl;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TizilandScheduler {
    private static final int percentageForAction = 80;

    private final PointEventService pointEventService;

    public TizilandScheduler() {
        this(new PointEventServiceImpl());
    }

    public TizilandScheduler(PointEventService pointEventService) {
        this.pointEventService = pointEventService;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::run, 5, 60, TimeUnit.SECONDS);
    }

    private void run() {
        try {
            for (Guild guild : Application.getJda().getGuilds()) {
                boolean eventsExpired = pointEventService.handleExpiredEvents();
                if (eventsExpired) {
                    continue;
                }

                int chance = (int) (Math.random() * 100);
                if (chance < percentageForAction) {
                    if (pointEventService.existsActiveEvent(guild.getIdLong())) {
                        continue;
                    }
                    pointEventService.sendRandomEventMessage(guild.getIdLong(), Long.valueOf(System.getProperty("general_channel_id")));
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
