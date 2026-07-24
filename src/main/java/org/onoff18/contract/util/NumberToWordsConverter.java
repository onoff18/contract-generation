package org.onoff18.contract.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberToWordsConverter {

    private static final String[] UNITS_MALE = {
            "", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять",
            "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"
    };

    private static final String[] UNITS_FEMALE = {
            "", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять",
            "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"
    };

    private static final String[] TENS = {
            "", "", "двадцать", "тридцать", "сорок", "пятьдесят",
            "шестьдесят", "семьдесят", "восемьдесят", "девяносто"
    };

    private static final String[] HUNDREDS = {
            "", "сто", "двести", "триста", "четыреста", "пятьсот",
            "шестьсот", "семьсот", "восемьсот", "девятьсот"
    };

    public static String convert(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "ноль";
        }

        // ЖЕСТКОЕ округление ВВЕРХ до целого числа (20373.01 -> 20374, 20373.50 -> 20374)
        long rubles = amount.setScale(0, RoundingMode.CEILING).longValue();

        return convertNumber(rubles);
    }

    private static String convertNumber(long number) {
        if (number == 0) return "ноль";

        StringBuilder result = new StringBuilder();

        long billions = number / 1_000_000_000;
        if (billions > 0) {
            result.append(convertTriad(billions, false)).append(" ")
                    .append(getCurrencyWord(billions, "миллиард", "миллиарда", "миллиардов")).append(" ");
        }

        long millions = (number / 1_000_000) % 1000;
        if (millions > 0) {
            result.append(convertTriad(millions, false)).append(" ")
                    .append(getCurrencyWord(millions, "миллион", "миллиона", "миллионов")).append(" ");
        }

        long thousands = (number / 1000) % 1000;
        if (thousands > 0) {
            result.append(convertTriad(thousands, true)).append(" ")
                    .append(getCurrencyWord(thousands, "тысяча", "тысячи", "тысяч")).append(" ");
        }

        long units = number % 1000;
        if (units > 0) {
            result.append(convertTriad(units, false));
        }

        return result.toString().trim();
    }

    private static String convertTriad(long number, boolean female) {
        StringBuilder result = new StringBuilder();

        int hundreds = (int) (number / 100);
        int tens = (int) ((number % 100) / 10);
        int units = (int) (number % 10);
        int lastTwoDigits = (int) (number % 100);

        if (hundreds > 0) {
            result.append(HUNDREDS[hundreds]).append(" ");
        }

        if (lastTwoDigits >= 10 && lastTwoDigits <= 19) {
            result.append(female ? UNITS_FEMALE[lastTwoDigits] : UNITS_MALE[lastTwoDigits]).append(" ");
        } else {
            if (tens >= 2) {
                result.append(TENS[tens]).append(" ");
            }
            if (units > 0) {
                result.append(female ? UNITS_FEMALE[units] : UNITS_MALE[units]).append(" ");
            }
        }

        return result.toString().trim();
    }

    private static String getCurrencyWord(long number, String one, String few, String many) {
        long lastDigit = number % 10;
        long lastTwoDigits = number % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return many;
        }

        if (lastDigit == 1) {
            return one;
        } else if (lastDigit >= 2 && lastDigit <= 4) {
            return few;
        } else {
            return many;
        }
    }
}