package com.a_team.taskmanager.utils;

import java.util.UUID;

class UniqueRequestCodeGenerator {
    static int getUniqueRequestCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.hashCode();
    }
}
