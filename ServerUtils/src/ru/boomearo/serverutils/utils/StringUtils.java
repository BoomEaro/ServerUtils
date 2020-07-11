package ru.boomearo.serverutils.utils;


import java.util.Collection;

public class StringUtils {

	public static void init() {
	}

	public static String join(Collection<String> args, String join) {
		return join(args.toArray(new String[0]), join);
	}

	public static String join(String[] args, String join) {
		if (args.length == 0) {
			return "";
		}
		if (args.length == 1) {
			return args[0];
		}
		StringBuilder sb = new StringBuilder(50);
		sb.append(args[0]);
		for (int i = 1; i < args.length; i++) {
			sb.append(join);
			sb.append(args[i]);
		}
		return sb.toString();
	}

	public static String eraseRight(String str, int eraseLength) {
		return str.substring(0, str.length() - eraseLength);
	}

	public static boolean isNullOrEmpty(String str) {
		return (str == null) || str.isEmpty();
	}

}
