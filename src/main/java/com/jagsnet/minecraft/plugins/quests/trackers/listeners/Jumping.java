package com.jagsnet.minecraft.plugins.quests.trackers.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.jagsnet.minecraft.plugins.quests.otherStuff.messages.Strings;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Completion;
import com.jagsnet.minecraft.plugins.quests.otherStuff.utils.Scroll;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class Jumping implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerJumpEvent event) {
        if (event.isCancelled()) {return;}
        if (event.getPlayer() instanceof Player) {
            Player player = event.getPlayer();
            if (Scroll.isScrollOff(player)) {
                ArrayList<String> lore = (ArrayList<String>) player.getInventory().getItemInOffHand().getItemMeta().getLore();

                for (int i = 0; i < lore.size(); i++) {
                    String line = Strings.cleanLore(lore.get(i), true, false);

                    if ((line.split(" ", 0).length == 4) &&
                            (line.contains("jump ") &&
                                    line.contains(" time"))) {
                        if (Completion.updateNumLine(lore, player, 1, i)) {
                            return;
                        }
                    }
                }
            }
        }
    }
}
