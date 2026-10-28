package com.jagsnet.minecraft.plugins.quests.otherStuff.utils;

import com.jagsnet.minecraft.plugins.quests.Quests;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class Completion {
    // ----------------- Update lines --------------------
    public static boolean updateNumLine(ArrayList<String> lore, Player p, int newNum, int lineNum){
        try {
            String strike = ChatColor.STRIKETHROUGH + "";
            if (lore.get(lineNum).contains(strike)) {return false;}
            if (!parseModifiers(p, lore)) {return false;}
            String[] loreLine = lore.get(lineNum).split(": ", 0);


            int lastCodeIndex = loreLine[1].lastIndexOf('§');
            int textStartIndex = (lastCodeIndex != -1) ? lastCodeIndex + 2 : 0;

            String prefix = loreLine[1].substring(0, textStartIndex);
            String number = loreLine[1].substring(textStartIndex);

            int x = parseInt(number) + newNum;
            lore.set(lineNum, loreLine[0] + ": " + prefix + x);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(lore.get(lineNum)));

            try {
                if (x >= parseInt(loreLine[0].split(" ", 0)[1])) {
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "locked");
                    if (p.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        if (lore.get(lineNum + 1).contains(strike)) {
                            lore.set(lineNum + 1, lore.get(lineNum + 1).replace(strike, ""));
                        }
                    }
                }
            } catch (Exception ee) {}

            boolean isOff = Scroll.isScrollOff(p);
            ItemStack itemStack;
            if (isOff) {
                itemStack = p.getInventory().getItemInOffHand();
            } else {
                itemStack = p.getInventory().getItemInMainHand();
            }
            ItemMeta m = itemStack.getItemMeta();
            m.setLore(lore);
            itemStack.setItemMeta(m);

            if (x >= parseInt(loreLine[0].split(" ", 0)[1])) {
                doComplete(p, itemStack.getItemMeta(), lore, itemStack);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateTxtLine(ArrayList<String> lore, Player p, int lineNum, Boolean isOff){
        try {
            String strike = ChatColor.STRIKETHROUGH + "";
            if (lore.get(lineNum).contains(strike)) {return false;}
            if (!parseModifiers(p, lore)) {return false;}
            if (!parseBiomes(p, lore)) {return false;}
            String[] loreLine = lore.get(lineNum).split(": ", 0);

            int lastCodeIndex = loreLine[1].lastIndexOf('§');
            int textStartIndex = (lastCodeIndex != -1) ? lastCodeIndex + 2 : 0;

            String prefix = loreLine[1].substring(0, textStartIndex);

            lore.set(lineNum, loreLine[0] + ": " + prefix + "Complete");
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(lore.get(lineNum)));
            if (lore.get(lineNum).contains("Complete")) {
                NamespacedKey key = new NamespacedKey(Quests.getInstance(), "locked");
                if (p.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    if (lore.get(lineNum + 1).contains(strike)) {
                        lore.set(lineNum + 1, lore.get(lineNum + 1).replace(strike, ""));
                    }
                }
            }

            ItemStack itemStack;
            if (isOff) {
                itemStack = p.getInventory().getItemInOffHand();
            } else {
                itemStack = p.getInventory().getItemInMainHand();
            }
            ItemMeta m = itemStack.getItemMeta();
            m.setLore(lore);
            itemStack.setItemMeta(m);

            if (lore.get(lineNum).contains("Complete")) {
                doComplete(p, itemStack.getItemMeta(), lore, itemStack);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseModifiers(Player p, ArrayList<String> lore) {
        for (int i = 1; i < 3; i++) {
            String s = ChatColor.stripColor(lore.get(i));
            if (s.contains("Modifiers: ")) {
                String[] modList = {""};

                // get secret modifier list
                if (s.toLowerCase().contains("secret")) {
                    NamespacedKey key = new NamespacedKey(Quests.getInstance(), "rewardLocation");
                    ItemStack itemStack;
                    if (Scroll.isScrollOff(p)) {
                        itemStack = p.getInventory().getItemInOffHand();
                    } else {
                        itemStack = p.getInventory().getItemInMainHand();
                    }
                    ItemMeta questMeta = itemStack.getItemMeta();
                    if (questMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        String[] location = questMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING).split("_");
                        Configs.load(location[0]);
                        modList = Configs.get().getString(location[1] + ".hiddenModifiers").toLowerCase().split(", ");
                    }
                // Else do regular modifier list
                } else {
                    modList = s.split(": ")[1].toLowerCase().split(", ");
                }
                for (String modifier : modList) {

                    // Wear X modifier
                    if (modifier.contains("helmet")) {
                        if (!(p.getInventory().getHelmet().getType().toString().equals(modifier.toUpperCase().replace(" ", "_")))) {
                            return false;
                        }
                    }
                    if (modifier.contains("chestplate")) {
                        if (!(p.getInventory().getChestplate().getType().toString().equals(modifier.toUpperCase().replace(" ", "_")))) {
                            return false;
                        }
                    }
                    if (modifier.contains("leggings")) {
                        if (!(p.getInventory().getLeggings().getType().toString().equals(modifier.toUpperCase().replace(" ", "_")))) {
                            return false;
                        }
                    }
                    if (modifier.contains("boots")) {
                        if (!(p.getInventory().getBoots().getType().toString().equals(modifier.toUpperCase().replace(" ", "_")))) {
                            return false;
                        }
                    }

                    // Ride x modifiers
                    if (modifier.contains("ride a ")) {
                        String mob = modifier.replace("ride a ", "");
                        if (p.getVehicle() != null) {
                            if (!(p.getVehicle().getType().toString().equals(mob.toUpperCase().replace(" ", "_")))) {
                                return false;
                            }
                        } else {
                            // return false if ride a modifier exists and not riding
                            return false;
                        }
                    }

                    // Potion modifiers
                    if (PotionEffectType.getByName(modifier.toUpperCase().replace(" ", "_")) != null) {
                        if (!p.hasPotionEffect(PotionEffectType.getByName(modifier.toUpperCase().replace(" ", "_")))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean parseBiomes(Player p, ArrayList<String> lore) {
        for (int i = 1; i < 3; i++) {
            String s = ChatColor.stripColor(lore.get(i));
            if (s.contains("Biomes: ")) {
                String[] biomeList = s.split(": ")[1].toUpperCase().replace(" ", "_").split(",_");
                if (Arrays.stream(biomeList).noneMatch(biome -> p.getLocation().getBlock().getBiome().name().equals(biome))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void doComplete(Player p, ItemMeta m, ArrayList<String> lore, ItemStack i) {
        NamespacedKey key = new NamespacedKey(Quests.getInstance(), "autocomplete");
        if (m.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            if (m.getPersistentDataContainer().get(key, PersistentDataType.STRING).equals("true")) {
                if (Quests.auto) {
                    if (isComplete(m)) {
                        Claim.claim(p, false, ".rewards", false);
                    }
                }
            }
        }
    }

    public static boolean isComplete(ItemMeta mainHandItem) {
        int loreLength = mainHandItem.getLore().size();
        int totalLines = 0;
        int totalComplete = 0;
        for (int i = 0; i < loreLength; i++) {
            String loreLineC = ChatColor.stripColor(mainHandItem.getLore().get(i));
            String[] loreLine = loreLineC.split(" ", 0);
            if (loreLine.length > 2) {
                String target = loreLine[1];
                String current = loreLine[loreLine.length - 1];

                int targetCount;
                int currentCount;

                if (current.equals("Incomplete")) {
                    totalLines++;
                    return false;
                } else if (current.equals("Complete")) {
                    totalLines++;
                    totalComplete++;
                }

                try {
                    targetCount = parseInt(target);
                    try {
                        currentCount = parseInt(current);
                        totalLines++;
                        if (currentCount >= targetCount) {
                            totalComplete++;
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                }
            }
        }

        if (totalLines == totalComplete && totalComplete != 0) {
            return true;
        }
        return false;
    }

    public static boolean isWild(CreatureSpawnEvent.SpawnReason spawnReason) {
        if (spawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER &&
                spawnReason != CreatureSpawnEvent.SpawnReason.COMMAND &&
                spawnReason != CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER &&
                spawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            return true;
        }
        return false;
    }
}
