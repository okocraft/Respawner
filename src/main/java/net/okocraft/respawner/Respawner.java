package net.okocraft.respawner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;

public class Respawner extends JavaPlugin implements CommandExecutor, TabCompleter {

	private FileConfiguration config;
	private FileConfiguration defaultConfig;

	@Override
	public void onEnable() {
		PluginCommand command = Objects.requireNonNull(getCommand("respawn"));
		command.setExecutor(this);
		command.setTabCompleter(this);
		saveDefaultConfig();

		config = getConfig();
		defaultConfig = getDefaultConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(getMessage("specify-player"));
			return false;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(getMessage("player-not-found"));
			return false;
		}

		if (!target.isDead()) {
			sender.sendMessage(getMessageWithPlayerName("player-is-alive", target.getName()));
			return false;
		}

		target.spigot().respawn();
		sender.sendMessage(getMessageWithPlayerName("respawned-player", target.getName()));
		target.sendMessage(getMessageWithPlayerName("respawned-by", target.getName()));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet()), new ArrayList<>());
		}
		return List.of();
	}

	private FileConfiguration getDefaultConfig() {
		InputStream is = getResource("config.yml");
		return YamlConfiguration.loadConfiguration(new InputStreamReader(is));
	}

	private String getMessage(String key) {
		String fullKey = "messages." + key;
		return ChatColor.translateAlternateColorCodes('&', config.getString(fullKey, defaultConfig.getString(fullKey)));
	}

	private String getMessageWithPlayerName(String key, String player) {
		return getMessage(key).replaceAll("%player_name%", player);
	}
}
