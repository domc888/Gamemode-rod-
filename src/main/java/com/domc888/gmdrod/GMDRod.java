package com.domc888.gmdrod;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class GMDRod extends JavaPlugin {

    @Override
    public void onEnable() {
        RodItem rodItem = new RodItem(this);

        getServer().getPluginManager().registerEvents(new RodListener(rodItem), this);

        PluginCommand command = Objects.requireNonNull(getCommand("gmdrod"));
        command.setExecutor(new GMDRodCommand(rodItem));
    }
}
