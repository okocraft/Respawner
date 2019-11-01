package net.okocraft.respawner

import org.bukkit.plugin.java.JavaPlugin

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil

import hazae41.minecraft.kutils.bukkit.command
import hazae41.minecraft.kutils.bukkit.info
import hazae41.minecraft.kutils.bukkit.msg

class Respawner: JavaPlugin(), TabCompleter {
    override fun onEnable() {
        command("respawn") { sender, args ->
            if (args.isEmpty()) {
                sender.msg("&7* プレイヤーを指定してください。")

                return@command
            }

            val request = args[0]
            val target = Bukkit.getPlayer(request) ?: run {
                sender.msg("&7* &b$request&7 は存在しません。")

                return@command
            }

            if (target.isDead) {
                target.spigot().respawn()
                sender.msg("&7* &b${target.name}&7 を復活させました。")
                target.msg("&7* あなたは &b${sender.name}&7 によって復活させられました。")

                return@command
            }

            sender.msg("&7* &b${target.name}&7 は生きています。")
        }

        info("Respawner is enabled.")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.size == 1) {
            val dead = Bukkit.getOnlinePlayers()
                .filter { it.isDead }
                .map { it.name }
                .toMutableList()

            return StringUtil.copyPartialMatches(
                args[0], dead, mutableListOf()
            )
        }

        return mutableListOf()
    }
}