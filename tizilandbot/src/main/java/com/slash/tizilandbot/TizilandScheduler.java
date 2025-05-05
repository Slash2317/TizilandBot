package com.slash.tizilandbot;

import com.slash.tizilandbot.service.PointEventService;
import com.slash.tizilandbot.service.PointEventServiceImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TizilandScheduler {
    private static final int percentageForAction = 5;

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

                    Long activeChannelId = Long.valueOf(System.getProperty("active_channel_id"));
                    Long generalChannelId = Long.valueOf(System.getProperty("general_channel_id"));
                    Long channelId;



                    TextChannel activeChannel = guild.getTextChannelById(activeChannelId);
                    List<Message> messages = activeChannel.getHistory().retrievePast(10).complete();
                    if (messages.get(messages.size() - 1).getTimeCreated().isAfter(OffsetDateTime.now().minusMinutes(10))) {
                        //channel is active
                        int channelChance = (int) (Math.random() * 2);
                        if (channelChance == 0) {
                            channelId = activeChannelId;
                        }
                        else {
                            channelId = generalChannelId;
                        }
                    }
                    else {
                        channelId = generalChannelId;
                    }

                    pointEventService.sendRandomEventMessage(guild.getIdLong(), channelId);
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
