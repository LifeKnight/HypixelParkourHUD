package com.lifeknight.hypixelparkourhud.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.util.EnumChatFormatting.*;

public class Miscellaneous {
	public static String getCurrentDateString() {
		return new SimpleDateFormat("MM/dd/yyyy").format(System.currentTimeMillis());
	}

	public static String getCurrentTimeString() {
		return new SimpleDateFormat("hh:mm:ss a").format(System.currentTimeMillis());
	}


	public static int scale(int toScale) {
		switch (Minecraft.getMinecraft().gameSettings.guiScale) {
			case 0:
				return (int) ((toScale * 2) / (double) getScaleFactor());
			case 1: {
				return toScale * 2;
			}
			case 2: {
				return toScale;
			}
			default: {
				return (int) (toScale / 1.5);
			}
		}
	}

	public static int get2ndPanelCenter() {
		return getScaledHeight(300) + (getGameWidth() - getScaledWidth(300)) / 2;
	}

	public static int getSupposedWidth() {
		return 1920 / getScaleFactor();
	}

	public static int getSupposedHeight() {
		return 1080 / getScaleFactor();
	}

	public static int getScaledWidth(int widthIn) {
		return scale((int) (widthIn * ((double) getGameWidth() / (double) getSupposedWidth())));
	}

	public static int getScaledHeight(int heightIn) {
		return scale((int) (heightIn * ((double) getGameHeight() / (double) getSupposedHeight())));
	}

	private static int getScaleFactor() {
		int scaledWidth = Minecraft.getMinecraft().displayWidth;
		int scaledHeight = Minecraft.getMinecraft().displayHeight;
		int scaleFactor = 1;
		boolean flag = Minecraft.getMinecraft().isUnicode();
		int i = Minecraft.getMinecraft().gameSettings.guiScale;

		if (i == 0) {
			i = 1000;
		}

		while (scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}

		if (flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
			--scaleFactor;
		}

		return scaleFactor;
	}

	public static int getGameWidth() {
		if (Minecraft.getMinecraft().gameSettings.guiScale != 0) {
			return Minecraft.getMinecraft().displayWidth / Minecraft.getMinecraft().gameSettings.guiScale;
		}
		return (int) (Math.ceil(Minecraft.getMinecraft().displayWidth / (double) getScaleFactor()));
	}

	public static int getGameHeight() {
		if (Minecraft.getMinecraft().gameSettings.guiScale != 0) {
			return Minecraft.getMinecraft().displayHeight / Minecraft.getMinecraft().gameSettings.guiScale;
		}
		return (int) (Math.ceil(Minecraft.getMinecraft().displayHeight / (double) getScaleFactor()));
	}

	public static int scaleFrom1080pWidth(int widthIn) {
		int i = widthIn / getScaleFactor();
		return (int) (i * (getGameWidth() / (double) getSupposedWidth()));
	}

	public static int scaleFrom1080pHeight(int heightIn) {
		int i = heightIn / getScaleFactor();
		return (int) (i * (getGameHeight() / (double) getSupposedHeight()));
	}

	public static int scaleTo1080pWidth(int widthIn) {
		int i = widthIn * getScaleFactor();
		return (int) (i * (getSupposedWidth() / (double) getGameWidth()));
	}

	public static int scaleTo1080pHeight(int heightIn) {
		int i = heightIn * getScaleFactor();
		return (int) (i * (getSupposedHeight() / (double) getGameHeight()));
	}

	public static String formatTimeFromMilliseconds(long milliseconds) {
		long days;
		long hours;
		long minutes;
		long seconds;
		long millisecondsLeft = milliseconds;
		days = millisecondsLeft / 86400000;
		millisecondsLeft %= 86400000;
		hours = millisecondsLeft / 3600000;
		millisecondsLeft %= 3600000;
		minutes = millisecondsLeft / 60000;
		millisecondsLeft %= 60000;
		seconds = millisecondsLeft / 1000;
		millisecondsLeft %= 1000;

		StringBuilder result = new StringBuilder();

		if (days > 0) {
			result.append(days).append(":");
			result.append(appendTime(hours)).append(":");
		} else {
			result.append(hours).append(":");
		}

		result.append(appendTime(minutes)).append(":");

		result.append(appendTime(seconds)).append(".");

		result.append(formatMilliseconds(millisecondsLeft));

		return result.toString();
	}

	private static String appendTime(long timeValue) {
		StringBuilder result = new StringBuilder();
		if (timeValue > 9) {
			result.append(timeValue);
		} else {
			result.append("0").append(timeValue);
		}
		return result.toString();
	}

	private static String formatMilliseconds(long milliseconds) {
		String asString = String.valueOf(milliseconds);

		if (asString.length() == 1) {
			return "00" + milliseconds;
		} else if (asString.length() == 2) {
			return "0" + milliseconds;
		}
		return asString;
	}
}
