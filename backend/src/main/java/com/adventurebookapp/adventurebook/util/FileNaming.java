package com.adventurebookapp.adventurebook.util;

public final class FileNaming {

    private FileNaming() {}

    public static String sanitize(String title) {
        return title.toLowerCase().replaceAll("\\s+", "_") + ".json";
    }
}
