package com.lifeknight.hypixelparkourhud.utilities;

import java.util.regex.Pattern;

public class Logic {

	public static boolean equalsAny(String text, String[] strings, boolean ignoreCase) {
		for (String string : strings) {
			if (ignoreCase) {
				if (string.equalsIgnoreCase(text)) return true;
			} else {
				if (string.equals(text)) return true;
			}
		}
		return false;
	}

	public static boolean containsAny(String text, String[] strings, boolean ignoreCase) {
		for (String string : strings) {
			if (ignoreCase) {
				if (text.toLowerCase().contains(string.toLowerCase())) return true;
			} else {
				if (text.contains(string)) return true;
			}
		}
		return false;
	}

	public static boolean startsWithAny(String text, String[] strings, boolean ignoreCase) {
		for (String string : strings) {
			if (ignoreCase) {
				if (text.toLowerCase().startsWith(string.toLowerCase())) return true;
			} else {
				if (text.startsWith(string)) return true;
			}
		}
		return false;
	}

	public static boolean endsWithAny(String text, String[] strings, boolean ignoreCase) {
		for (String string : strings) {
			if (ignoreCase) {
				if (text.toLowerCase().endsWith(string.toLowerCase())) return true;
			} else {
				if (text.endsWith(string)) return true;
			}
		}
		return false;
	}

	public static boolean containsLetters(String input) {
		return Pattern.compile("[a-zA-Z]").matcher(input).find();
	}

	public static boolean containsNonWhiteSpace(String input) {
		return Pattern.compile("[\\S]").matcher(input).find();
	}
}
