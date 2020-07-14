package com.lifeknight.hypixelparkourhud.utilities;

import java.util.regex.Pattern;

public class Logic {

	public static boolean containsNonWhiteSpace(String input) {
		return Pattern.compile("[\\S]").matcher(input).find();
	}
}
