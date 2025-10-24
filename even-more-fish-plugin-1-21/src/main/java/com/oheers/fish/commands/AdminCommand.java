package com.oheers.fish.commands;

import net.strokkur.commands.annotations.Permission;
import net.strokkur.commands.annotations.Subcommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AdminCommand {

    @Subcommand("database")
    @Permission("emf.admin.debug.database")
    AdminDatabaseCommand adminDatabaseCommand;


}
