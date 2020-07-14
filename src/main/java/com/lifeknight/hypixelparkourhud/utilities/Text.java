package com.lifeknight.hypixelparkourhud.utilities;

public class Text {
	public static String removeAllPunctuation(String text) {
		return text.replaceAll("\\W", "");
	}

	public static String removeFormattingCodes(String input) {
		return input.replaceAll("[" + '\u00A7' + "][\\w]", "");
	}
}
