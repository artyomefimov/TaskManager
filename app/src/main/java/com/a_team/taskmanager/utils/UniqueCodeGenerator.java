package com.a_team.taskmanager.utils;

import java.util.UUID;

public class UniqueCodeGenerator {
    public static int getUniqueCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.hashCode();
    }
}
