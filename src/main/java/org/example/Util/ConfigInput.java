package org.example.Util;

public class ConfigInput {
    public static boolean checkUpdateOrAdd(String nameAction) {
        return nameAction.equalsIgnoreCase("thêm mới");
    }
}
