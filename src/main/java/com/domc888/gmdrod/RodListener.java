package com.domc888.gmdrod;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;

public final class RodListener implements Listener {

    private final RodItem rodItem;

    public RodListener(RodItem rodItem) {
        this.rodItem = rodItem;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (!isReelIn(event.getState())) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        RodItem.Binding binding = rodItem.read(inventory.getItemInMainHand());
        if (binding == null) {
            binding = rodItem.read(inventory.getItemInOffHand());
        }
        if (binding == null) {
            return;
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Entity caught = event.getCaught();
            if (caught != null) {
                caught.remove();
            }
            event.setExpToDrop(0);
        }

        Player target = Bukkit.getPlayer(binding.targetId());
        if (target == null) {
            player.sendMessage(Component.text(binding.targetName() + " is not online."));
            return;
        }

        GameMode mode = binding.mode();
        String modeName = mode.name().toLowerCase(Locale.ROOT);
        if (target.getGameMode() == mode) {
            player.sendMessage(Component.text(target.getName() + " is already in " + modeName + "."));
            return;
        }

        target.setGameMode(mode);
        player.sendMessage(Component.text("Set " + target.getName() + " to " + modeName + "."));
    }

    private boolean isReelIn(PlayerFishEvent.State state) {
        return switch (state) {
            case REEL_IN, IN_GROUND, CAUGHT_ENTITY, CAUGHT_FISH, FAILED_ATTEMPT -> true;
            default -> false;
        };
    }
}
