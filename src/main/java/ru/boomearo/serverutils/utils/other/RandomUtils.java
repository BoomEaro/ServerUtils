package ru.boomearo.serverutils.utils.other;

import java.util.Random;

public class RandomUtils {
    private static final String CHAR_LIST = "1234567890";

    public static String generateRandomString(int length) {
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private static int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        }
        else {
            return randomInt - 1;
        }
    }

    public static int getRandomNumberRange(int min, int max) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt((max - min) + 1) + min;
    }
}
