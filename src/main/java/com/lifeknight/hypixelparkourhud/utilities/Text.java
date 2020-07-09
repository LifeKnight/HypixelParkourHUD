package com.lifeknight.hypixelparkourhud.utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Text {

	public static List<String> returnStartingEntries(String[] strings, String input, boolean ignoreCase) {
		if (input == null || input.isEmpty()) return Arrays.asList(strings);
		List<String> result = new ArrayList<>();
			for (String string : strings) {
				if (ignoreCase) {
					if (string.toLowerCase().startsWith(input.toLowerCase())) result.add(string);
				} else {
					if (string.startsWith(input)) result.add(string);
				}
			}
		return result;
	}

	public static String removeAllPunctuation(String text) {
		return text.replaceAll("\\W", "");
	}

	public static int countWords(String msg) {
		int count = 0;
		for (int x = 0; x < msg.length(); x++) {
			if (msg.charAt(x) == ' ') {
				count++;
			}
		}
		return ++count;
	}
	
	public static String removeAll(String msg, String rmv) {
		msg = msg.replaceAll(rmv, "");
		return msg;
	}

	public static String removeFormattingCodes(String input) {
		return input.replaceAll("[" + '\u00A7' + "][\\w]", "");
	}

	public static String multiplyString(String string, int times) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < times; i++) {
			result.append(string);
		}
		return result.toString();
	}

	public static String parseTextToIndexOfTextAfter(String text, String firstIndexText, String secondIndexText) {
        if (text.contains(firstIndexText) && text.contains(secondIndexText)) {
			return text.substring((firstIndexText.indexOf(firstIndexText) + firstIndexText.length() + 1), (text.indexOf(secondIndexText) - 1));
        }
        return null;
	}

	public static String shortenDouble(double value, int decimalDigits) {
		String asString = String.valueOf(value);
		int wholeDigits = asString.substring(0, asString.indexOf(".")).length();
		return new DecimalFormat(multiplyString("#", wholeDigits) + "." + multiplyString("#", decimalDigits)).format(value);
	}
}
