package org.example.Util;

public class Notification {
    public static void successMessage(String message) {
        System.out.println(ColorText.GREEN_BRIGHT + message + ColorText.RESET);
        System.out.println();// blank space
    }

    public static void waringMessage(String message) {
        System.out.println(ColorText.YELLOW_BRIGHT + message + ColorText.RESET);
        System.out.println();// blank space

    }

    public static void errorMessage(String message) {
        System.err.println(message + '!');
        System.out.println();// blank space
    }

    public static void inputPrint(String message) {
        System.out.println(ColorText.CYAN_BRIGHT + message + ColorText.RESET);
        System.out.println();// blank space
    }
}
