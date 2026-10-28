package com.jagsnet.minecraft.plugins.quests.otherStuff.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;

public class Perms {
    public static void setPerm(String player, String perm, String time) {
        if (Configs.getGlobal().get("permissions") == null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player + " permission settemp " + perm + " true " + time);
            return;
        }
        String command = Configs.getGlobal().getString("permissions.set");
        while (command.contains("<player>")) command.replace("<player>", player);
        while (command.contains("<permission>")) command.replace("<permission>", perm);
        while (command.contains("<time>")) command.replace("<time>", time);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        return;
    }

    public static void setDisplayPerm(Player p, String displayMode) {
        String playerName = p.getName();
        switch (displayMode) {
            case "full":
                unsetPermPerm(playerName, "quest.display.compact");
                unsetPermPerm(playerName, "quest.display.off");
                setPermPerm(playerName, "quest.display.full");
                break;
            case "compact":
                unsetPermPerm(playerName, "quest.display.full");
                unsetPermPerm(playerName, "quest.display.off");
                setPermPerm(playerName, "quest.display.compact");
                break;
            case "off":
                unsetPermPerm(playerName, "quest.display.compact");
                unsetPermPerm(playerName, "quest.display.full");
                setPermPerm(playerName, "quest.display.off");
                break;
        }
        return;
    }

    public static void setPermPerm(String player, String perm) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player + " permission set " + perm);
        return;
    }

    public static void unsetPermPerm(String player, String perm) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player + " permission unset " + perm);
        return;
    }
}
