package com.lifeknight.hypixelparkourhud.mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lifeknight.hypixelparkourhud.gui.LifeKnightGui;
import com.lifeknight.hypixelparkourhud.gui.ManipulableGui;
import com.lifeknight.hypixelparkourhud.gui.ParkourWorldListGui;
import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightButton;
import com.lifeknight.hypixelparkourhud.gui.hud.EnhancedHudText;
import com.lifeknight.hypixelparkourhud.gui.hud.ParkourHud;
import com.lifeknight.hypixelparkourhud.utilities.*;
import com.lifeknight.hypixelparkourhud.variables.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.File;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.util.EnumChatFormatting.*;

@net.minecraftforge.fml.common.Mod(modid = Core.modId, name = Core.modName, version = Core.modVersion, clientSideOnly = true)
public class Core {
    public static final String
            modName = "Hypixel Parkour HUD",
            modVersion = "1.0",
            modId = "hypixelparkourhud";
    public static final EnumChatFormatting modColor = GOLD;
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new LifeKnightThreadFactory());
    public static boolean onHypixel = false;
    public static GuiScreen guiToOpen = null;
    public static final LifeKnightBoolean runMod = new LifeKnightBoolean("Mod", "Main", true);
    public static final LifeKnightBoolean hudTextShadow = new LifeKnightBoolean("HUD Text Shadow", "HUD", true);
    private static final LifeKnightCycle showHud = new LifeKnightCycle("Show HUD", "HUD", Arrays.asList(
            "No",
            "When Active",
            "Yes"
    ), 1);
    public static final LifeKnightCycle timeDisplayType = new LifeKnightCycle("Time Type", "HUD", Arrays.asList(
            "Total",
            "Interval"
    ));
    public static final LifeKnightCycle timeToCompare = new LifeKnightCycle("Time To Compare", "HUD", Arrays.asList(
            "Best",
            "Latest"
    ));
    public static final LifeKnightNumber.LifeKnightInteger hudOpacity = new LifeKnightNumber.LifeKnightInteger("HUD Opacity", "HUD", 70, 0, 100);
    public static final LifeKnightBoolean automaticallyEnableAndDisableFlying = new LifeKnightBoolean("Auto Enable/Disable Fly", "Settings", false);
    public static final LifeKnightString nickName = new LifeKnightString("Nickname", "Settings", "");
    public static final LifeKnightList.LifeKnightIntegerList deletedSessionIds = new LifeKnightList.LifeKnightIntegerList("Deleted Session IDs", "Extra");
    public static Logger parkourLogger;
    public static Configuration configuration;
    final String[] housingParkourMessages = {
            "Parkour challenge started!",
            "Reset your timer to 00:00! Get to the finish line!",
            "Parkour challenge failed!", //do not fly, do not teleport, etc.
            "You reached Checkpoint #", //start
            User.getUsername() + " completed the parkour in ", //start
            "You completed the parkour in ", //start
    };
    public static boolean type = false;
    public static String location = "";
    public static boolean sessionIsRunning = false;
    private static ParkourHud parkourHud;
    private static boolean requestSent = false;
    public static final LifeKnightGui defaultGui = new LifeKnightGui("[" + modVersion + "] " + modName, LifeKnightVariable.getVariables(), Arrays.asList(
            new LifeKnightButton("View Sessions") {
                @Override
                public void work() {
                    openGui(new ParkourWorldListGui());
                }
            },
            new LifeKnightButton("Edit HUD") {
                @Override
                public void work() {
                    openGui(new ManipulableGui());
                }
            }));

    @EventHandler
    public void init(FMLInitializationEvent initEvent) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());
        deletedSessionIds.setShowInLifeKnightGui(false);

        parkourLogger = new Logger(new File("logs/lifeknight/hypixelparkourhud"));

        parkourHud = new ParkourHud("ParkourHUD", 5, 5);

        configuration = new Configuration();

        processLogs();
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Chat.sendQueuedChatMessages();
                onHypixel = !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
            }
        }, 2500);
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (onHypixel) {
            String message = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getFormattedText());
            if (message.startsWith("{")) {
                try {
                    JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
                    if (requestSent) {
                        event.setCanceled(true);
                    }
                    if (jsonObject.has("server")) {
                        type = !jsonObject.get("server").getAsString().contains("lobby");

                        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
                        if (!type && scoreboard.getTeams().size() > 0) {
                            String scoreboardDisplayName = "";

                            for (ScoreObjective scoreObjective : scoreboard.getScoreObjectives()) {
                                scoreboardDisplayName = scoreObjective.getDisplayName();
                            }

                            scoreboardDisplayName = EnumChatFormatting.getTextWithoutFormattingCodes(scoreboardDisplayName);

                            if (scoreboardDisplayName.length() > 1) {
                                location = scoreboardDisplayName;
                            }
                        } else {
                            int nameIndex = -1;
                            boolean found = false;
                            List<ScorePlayerTeam> scorePlayerTeams = new ArrayList<>(scoreboard.getTeams());
                            for (int i = 0; i < scorePlayerTeams.size(); i++) {
                                ScorePlayerTeam scorePlayerTeam = scorePlayerTeams.get(i);
                                if (scorePlayerTeam.getColorPrefix().contains("House Name:")) {
                                    found = true;
                                } else if (found) {
                                    ScorePlayerTeam scorePlayerTeam1 = scorePlayerTeams.get(i);
                                    if (Logic.containsNonWhiteSpace(EnumChatFormatting.getTextWithoutFormattingCodes(scorePlayerTeam1.getColorPrefix())) && !EnumChatFormatting.getTextWithoutFormattingCodes(scorePlayerTeam1.getColorPrefix()).equals("[MVP++] ")) {
                                        nameIndex = i;
                                        break;
                                    }
                                }
                            }

                            if (nameIndex != -1) {
                                location = scorePlayerTeams.get(nameIndex).getColorPrefix() + scorePlayerTeams.get(nameIndex).getColorSuffix();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else if (message.equals("You are sending commands too fast! Please slow down.") && requestSent) {
                event.setCanceled(true);
            } else if (message.equals(housingParkourMessages[0]) || message.equalsIgnoreCase(housingParkourMessages[1])) {
                ParkourSession.createAndActivate(type, location);
                if (automaticallyEnableAndDisableFlying.getValue() && Minecraft.getMinecraft().thePlayer.capabilities.allowFlying) {
                    Chat.sendChatMessage("/fly", Chat.NORMAL);
                }
            } else if (!message.contains(": ")) {
                if (message.startsWith(housingParkourMessages[2]) || message.equals("Parkour challenge cancelled!")) {
                    ParkourSession.cancelParkourSession();
                } else if (message.startsWith(housingParkourMessages[4]) || message.startsWith(housingParkourMessages[5]) || message.toLowerCase().startsWith(nickName.getValue().toLowerCase() + " completed the parkour in") || message.startsWith("Congratulations on completing the parkour! You finished in ") || message.endsWith("Try again to beat your old record!") || message.startsWith("That's a new record of ")) {
                    ParkourSession.endCurrentSession();
                    if (automaticallyEnableAndDisableFlying.getValue() && !Minecraft.getMinecraft().thePlayer.capabilities.allowFlying) {
                        Chat.sendChatMessage("/fly", Chat.NORMAL);
                    }
                } else if (message.startsWith(housingParkourMessages[3])) {
                    ParkourSession.onCheckpointReached();
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (onHypixel && !requestSent) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Chat.sendChatMessage("/locraw", Chat.NORMAL);
                    requestSent = true;
                    THREAD_POOL.submit(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        requestSent = false;
                    });
                }
            }, 5000);
            ParkourSession.cancelParkourSession();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }

        if (Minecraft.getMinecraft().inGameHasFocus && runMod.getValue()) {
            EnhancedHudText.doRender();
            if ((showHud.getValue() == 2 && ParkourWorld.containsParkourWorld(type, location)) || (sessionIsRunning && showHud.getValue() == 1)) {
                parkourHud.render();
            }
        }
    }

    public static void openGui(GuiScreen guiScreen) {
        guiToOpen = guiScreen;
    }

    private void processLogs() {
        for (String log : parkourLogger.getLogs()) {
            THREAD_POOL.submit(() -> {
                Scanner scanner = new Scanner(log);
                while (scanner.hasNextLine()) {
                    String line;
                    if ((line = scanner.nextLine()).startsWith("{"))
                        ParkourSession.interpretParkourSessionFromString(line);
                }
            });
        }
    }
}
