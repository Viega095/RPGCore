package com.antigravity.rpg.commands;

import com.antigravity.rpg.RPGCore;
import com.antigravity.rpg.models.RPGClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RPGTabCompleter implements TabCompleter {

    private final RPGCore core;

    public RPGTabCompleter(RPGCore core) {
        this.core = core;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmdName = command.getName().toLowerCase();

        if (cmdName.equals("rpg")) {
            if (args.length == 1) {
                List<String> list = new ArrayList<>(Arrays.asList("guide", "test", "lab", "demo", "ascend", "class", "craft", "crow", "talents", "dungeon", "altar", "pet", "forge", "reforge", "socket", "stats", "abyss", "artifacts"));
                if (sender.hasPermission("rpg.admin")) {
                    list.addAll(Arrays.asList("update", "reload", "item", "spawnmob", "spawnboss", "spawnelite", "addxp", "keystone", "runes"));
                }
                return filter(list, args[0]);
            }
            if (args.length == 2) {
                String sub = args[0].toLowerCase();
                if (sub.equals("update") && sender.hasPermission("rpg.admin")) {
                    return filter(Arrays.asList("check", "apply", "download"), args[1]);
                }
                if (sub.equals("abyss")) {
                    return filter(Arrays.asList("start", "leave", "record"), args[1]);
                }
                if (sub.equals("item") && sender.hasPermission("rpg.admin")) {
                    return filter(new ArrayList<>(core.getItemManager().getAllItemIds()), args[1]);
                }
                if ((sub.equals("spawnmob") || sub.equals("spawnelite")) && sender.hasPermission("rpg.admin")) {
                    return filter(new ArrayList<>(core.getMobManager().getAllMobIds()), args[1]);
                }
                if (sub.equals("spawnboss") && sender.hasPermission("rpg.admin")) {
                    return filter(Arrays.asList("INFERNAL_DRAGON", "VOID_REAPER", "ABYSSAL_LEVIATHAN"), args[1]);
                }
                if (sub.equals("addxp") && sender.hasPermission("rpg.admin")) {
                    return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                }
                if (sub.equals("class")) {
                    return filter(Arrays.stream(RPGClass.values()).map(Enum::name).collect(Collectors.toList()), args[1]);
                }
                if (sub.equals("dungeon")) {
                    return filter(Arrays.asList("start", "leave", "status"), args[1]);
                }
                if (sub.equals("pet")) {
                    return filter(Arrays.asList("spawn", "dismiss", "info", "levelup"), args[1]);
                }
                if (sub.equals("runes")) {
                    return filter(Arrays.asList("list", "give"), args[1]);
                }
            }
            if (args.length == 3) {
                String sub = args[0].toLowerCase();
                if (sub.equals("spawnelite") && sender.hasPermission("rpg.admin")) {
                    return filter(Arrays.asList("MOLTEN", "VORTEX", "ELECTRIFIED", "SHIELDED", "VAMPIRIC"), args[2]);
                }
                if (sub.equals("item") && sender.hasPermission("rpg.admin")) {
                    return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[2]);
                }
                if (sub.equals("addxp") && sender.hasPermission("rpg.admin")) {
                    return Arrays.asList("100", "500", "1000", "5000", "10000");
                }
                if (sub.equals("keystone") && sender.hasPermission("rpg.admin")) {
                    return Arrays.asList("1", "2", "5", "10", "15");
                }
            }
        } else if (cmdName.equals("abyss")) {
            if (args.length == 1) {
                return filter(Arrays.asList("start", "leave", "record"), args[0]);
            }
        } else if (cmdName.equals("artifacts") || cmdName.equals("relic")) {
            return new ArrayList<>();
        } else if (cmdName.equals("class")) {
            if (args.length == 1) {
                return filter(Arrays.stream(RPGClass.values()).map(Enum::name).collect(Collectors.toList()), args[0]);
            }
        } else if (cmdName.equals("dungeon")) {
            if (args.length == 1) {
                return filter(Arrays.asList("start", "leave", "status"), args[0]);
            }
        } else if (cmdName.equals("pet")) {
            if (args.length == 1) {
                return filter(Arrays.asList("spawn", "dismiss", "info", "levelup"), args[0]);
            }
        } else if (cmdName.equals("keystone")) {
            if (args.length == 1) {
                return filter(Arrays.asList("give", "info"), args[0]);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                return Arrays.asList("1", "2", "5", "10", "15");
            }
        } else if (cmdName.equals("runes") || cmdName.equals("runewords")) {
            if (args.length == 1) {
                return filter(Arrays.asList("list", "forge", "give"), args[0]);
            }
        } else if (cmdName.equals("rpgcontract")) {
            if (args.length == 1) {
                return filter(Arrays.asList("list", "accept", "abandon", "spawn"), args[0]);
            }
        }

        return new ArrayList<>();
    }

    private List<String> filter(List<String> list, String query) {
        String q = query.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(q)).collect(Collectors.toList());
    }
}
