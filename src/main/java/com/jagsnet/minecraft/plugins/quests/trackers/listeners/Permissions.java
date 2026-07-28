package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.jagsnet.minecraft.plugins.quests.Quests;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class Permissions {
    public static void register() {
        LuckPerms luckPerms = LuckPermsProvider.get();
        luckPerms.getEventBus().subscribe(Quests.getInstance(), NodeAddEvent.class, e -> {
            if (!e.isUser()) return;
            Node node = e.getNode();
            if (!node.getValue()) return;
            UUID uuid = ((User) e.getTarget()).getUniqueId();
            String permKey = node.getKey();
            Bukkit.getScheduler().runTask(Quests.getInstance(), () -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return;
                if (Scroll.isScrollOff(player)) {
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "permissionLines");
                    if (!player.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) return;
                    String permissionLines = player.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    for (String entry : permissionLines.split(",")) {
                        String[] parts = entry.split("\\|");
                        int lineNum = Integer.parseInt(parts[0]);
                        String perm = parts[1];
                        if (!permKey.contains(perm)) continue;
                        ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();
                        String line = Strings.cleanLore(lore.get(lineNum), false, false);
                        if (line.contains("incomplete")) {
                            Completion.updateTxtLine(lore, player, lineNum, true);
                        }
                    }
                }
            });
        });
    }
}
