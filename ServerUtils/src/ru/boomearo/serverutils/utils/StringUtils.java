package ru.boomearo.serverutils.utils;

public class StringUtils {

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


}
