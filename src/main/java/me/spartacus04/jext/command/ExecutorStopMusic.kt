package me.spartacus04.jext.command

import me.spartacus04.jext.config.ConfigData.Companion.DISCS
import me.spartacus04.jext.config.ConfigData.Companion.LANG
import me.spartacus04.jext.config.send
import me.spartacus04.jext.disc.DiscContainer
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class ExecutorStopMusic : ExecutorAdapter("stopmusic") {
    init {
        addParameter(ParameterPlayer(true))
        addParameter(ParameterDisc(false))
    }

    override fun executePlayer(sender: Player, args: Array<String>): Boolean {
        return mergedExecute(sender, args)
    }

    override fun executeCommand(sender: CommandSender, args: Array<String>): Boolean {
        return mergedExecute(sender, args)
    }

    private fun mergedExecute(sender: CommandSender, args: Array<String>): Boolean {
        val players = ParameterPlayer.getPlayers(args[0], sender)

        val namespaces: MutableSet<String>

        if (args.size <= 1) {
            namespaces = DISCS.map { it.DISC_NAMESPACE }.toMutableSet()
        } else {
            val disc = ParameterDisc.getDisc(args[1])

            if (disc == null) {
                LANG.format(sender, "disc-namespace-not-found")
                    .replace("%namespace%", args[1])
                    .let { sender.send(it) }

                return true
            }

            namespaces = HashSet()

            namespaces.add(DiscContainer(disc).namespace)
        }

        for (player in players) {

            for (namespace in namespaces) {

                player.stopSound(namespace, SoundCategory.RECORDS)
                if (namespaces.size == 1) {
                    LANG.format(sender, "stopped-music")
                        .replace("%name%", DISCS.find { it.DISC_NAMESPACE == namespace }!!.TITLE)
                        .let { sender.send(it) }
                }
            }

            if (namespaces.size > 1) {
                LANG.format(sender, "stopped-all-music")
                    .let { sender.send(it) }
            }
        }

        val playerCount = players.size

        if (playerCount >= 2) {
            LANG.format(sender, "stopped-music-for-multiple")
                .replace("%playercount%", playerCount.toString())
                .let { sender.send(it) }
        } else if (playerCount == 1) {
            LANG.format(sender, "stopped-music-for")
                .replace("%player%", players[0].name)
                .let { sender.send(it) }
        } else {
            LANG.format(sender, "stopped-music-for-no-one")
                .let { sender.send(it) }
        }

        return true
    }
}