package com.lifeknight.hypixelparkourhud.mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

@net.minecraftforge.fml.common.Mod(modid = Mod.modId, name = Mod.modName, version = Mod.modVersion, clientSideOnly = true)
public class Mod {
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
            "When active",
            "Yes"
    ), 1);
    public static final LifeKnightCycle timeDisplayType = new LifeKnightCycle("Time Type", "HUD", Arrays.asList(
            "Total time",
            "Interval"
    ));
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
    private static boolean endogenous = false;
    private static ParkourHud parkourHud;
    private static boolean requestSent = false;

    @EventHandler
    public void init(FMLInitializationEvent initEvent) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

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
                    if (endogenous) {
                        event.setCanceled(true);
                        endogenous = false;
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
                                    if (Logic.containsNonWhiteSpace(EnumChatFormatting.getTextWithoutFormattingCodes(scorePlayerTeam1.getColorPrefix())) && !scorePlayerTeam1.getColorPrefix().equals("§6[MVP§f++§6] ")) {
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
            } else if (message.equals("You are sending commands too fast! Please slow down.") && endogenous) {
                event.setCanceled(true);
                endogenous = false;
            } else if (message.equals(housingParkourMessages[0]) || message.equalsIgnoreCase(housingParkourMessages[1])) {
                ParkourSession.createAndActivate(type, location);
            } else if (!message.contains(": ")) {
                if (message.startsWith(housingParkourMessages[2])) {
                    ParkourSession.cancelParkourSession();
                } else if (message.startsWith(housingParkourMessages[4]) || message.startsWith(housingParkourMessages[5]) || message.startsWith("Congratulations on completing the parkour! You finished in ") || message.endsWith("Try again to beat your old record!")) {
                    ParkourSession.endCurrentSession();
                } else if (message.startsWith(housingParkourMessages[3]) || message.startsWith("You finished this part of the parkour in ")) {
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
                    endogenous = true;
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
