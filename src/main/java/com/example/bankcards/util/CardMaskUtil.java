package com.example.bankcards.util;

public class CardMaskUtil {

    private CardMaskUtil() {}

    public static String mask(String number){
        if(number == null || number.length() < 4){
            return "****";
        }
        String maskedNumber = number.substring(number.length() - 4);
        return "**** **** **** " + maskedNumber;
    }
}
