package com.lifeknight.hypixelparkourhud.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.lifeknight.hypixelparkourhud.mod.Core.modColor;
import static com.lifeknight.hypixelparkourhud.mod.Core.modName;

public enum Chat {
	NORMAL(""),
	ALL("/allchat"),
	PARTY("/partychat"),
	GUILD("/guildchat"),
	SHOUT("/shout"),
	REPLY("/reply");

	private String prefix;
	Chat(String prefix) {
		this.prefix = prefix;
	}

	public static final ArrayList<String> queuedMessages = new ArrayList<>();

	public static void addChatMessage(String msg) {
		if (Minecraft.getMinecraft().theWorld != null) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(modColor + "" + EnumChatFormatting.BOLD + modName + " > " + EnumChatFormatting.RESET + msg));
		} else {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					addChatMessage(msg);
				}
			}, 100L);
		}
	}

	public static void sendChatMessage(String msg, Chat chatType) {
		if (Minecraft.getMinecraft().theWorld != null) {

			Minecraft.getMinecraft().thePlayer.sendChatMessage(chatType.prefix + msg);
		} else {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					sendChatMessage(msg, chatType);
				}
			}, 100L);
		}

	}

	public static void queueChatMessageForConnection(String msg) {
		queuedMessages.add(msg);
	}

	public static void sendQueuedChatMessages() {
		for (String queuedMessage : queuedMessages) {
			Chat.addChatMessage(queuedMessage);
		}
		queuedMessages.clear();
	}
}
