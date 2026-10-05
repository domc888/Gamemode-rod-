package com.domc888.gmdrod;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GMDRodCommand implements TabExecutor {

    private static final List<String> MODES = List.of("survival", "creative", "spectator", "adventure");

    private final RodItem rodItem;

    public GMDRodCommand(RodItem rodItem) {
        this.rodItem = rodItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command."));
            return true;
        }
        if (args.length != 2) {
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found. They must be online."));
            return true;
        }

        GameMode mode = parseMode(args[1]);
        if (mode == null) {
            player.sendMessage(Component.text("Invalid gamemode. Use survival, creative, spectator or adventure."));
            return true;
        }

        ItemStack rod = rodItem.create(target, mode);
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(rod);
        for (ItemStack left : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), left);
        }

        player.sendMessage(Component.text("Gave you a GM Rod for " + target.getName()
                + " (" + mode.name().toLowerCase(Locale.ROOT) + ")."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                names.add(p.getName());
            }
            return filter(names, args[0]);
        }
        if (args.length == 2) {
            return filter(MODES, args[1]);
        }
        return List.of();
    }

    private GameMode parseMode(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "survival" -> GameMode.SURVIVAL;
            case "creative" -> GameMode.CREATIVE;
            case "spectator" -> GameMode.SPECTATOR;
            case "adventure" -> GameMode.ADVENTURE;
            default -> null;
        };
    }

    private List<String> filter(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lower)) {
                out.add(option);
            }
        }
        return out;
    }
}
