package com.oheers.fish.database.model.user;


import java.util.UUID;

public class EmptyUserReport extends UserReport{
    public EmptyUserReport(UUID uuid) {
        super(uuid, "", "", "", "",0, 0, 0, 0, -1, 0, 0, 0);
    }
}
