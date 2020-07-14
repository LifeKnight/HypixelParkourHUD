package com.lifeknight.hypixelparkourhud.utilities;

import net.minecraft.client.Minecraft;

public class User {
	public static String getUsername() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}
}
