package com.lifeknight.hypixelparkourhud.mod;

import com.lifeknight.hypixelparkourhud.gui.hud.EnhancedHudText;
import com.lifeknight.hypixelparkourhud.utilities.Chat;
import com.lifeknight.hypixelparkourhud.variables.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;
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
    public static Configuration configuration;

    @EventHandler
    public void init(FMLInitializationEvent initEvent) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

        configuration = new Configuration();
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Chat.sendQueuedChatMessages();
                onHypixel = !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
            }
        }, 1000);
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {

    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }

        if (Minecraft.getMinecraft().inGameHasFocus) {
            EnhancedHudText.doRender();
        }
    }

    public static void openGui(GuiScreen guiScreen) {
        guiToOpen = guiScreen;
    }
}