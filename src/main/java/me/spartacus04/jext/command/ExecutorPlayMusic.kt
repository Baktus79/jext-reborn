package me.spartacus04.jext.command

import me.spartacus04.jext.config.ConfigData.Companion.LANG
import me.spartacus04.jext.config.send
import me.spartacus04.jext.disc.DiscContainer
import me.spartacus04.jext.disc.DiscPlayer
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class ExecutorPlayMusic : ExecutorAdapter("playmusic") {
    init {
        addParameter(ParameterPlayer(true))
        addParameter(ParameterDisc(true))
        addParameter(ParameterNumber(false, 0.5f, 1.0f, 1.5f).setName("pitch"))
        addParameter(ParameterNumber(false, 4.0f, 1.0f, 0.5f).setName("volume"))
    }

    override fun executePlayer(sender: Player, args: Array<String>): Boolean {
        return mergedExecute(sender, args)
    }

    override fun executeCommand(sender: CommandSender, args: Array<String>): Boolean {
        return mergedExecute(sender, args)
    }

    private fun mergedExecute(sender: CommandSender, args: Array<String>): Boolean {
        val players = ParameterPlayer.getPlayers(args[0], sender)

        val disc = ParameterDisc.getDisc(args[1])
        if (disc == null) {
            LANG.format(sender, "disc-namespace-not-found")
                .replace("%namespace%", args[1])
                .let { sender.send(it) }

            return true
        }

        val discPlayer = DiscPlayer(DiscContainer(disc))

        var pitch = 1.0f
        if (args.size >= 3) {
            pitch = try {
                args[2].toFloat()
            } catch (e: NumberFormatException) {
                LANG.format(sender, "wrong-number-format")
                    .replace("%param%", "pitch")
                    .let { sender.send(it) }

                return true
            }
        }

        var isMusic = true

        if (args.size >= 4) {
            isMusic = false

            try {
                val volume = args[3].toFloat()
                discPlayer.setPitch(pitch)
                discPlayer.setVolume(volume)
            } catch (e: NumberFormatException) {
                LANG.format(sender, "wrong-number-format")
                    .replace("%param%", "volume")
                    .let { sender.send(it) }

                return true
            }
        }

        for (player in players) {
            if (isMusic) {
                player.playSound(player.location, DiscContainer(disc).namespace, SoundCategory.RECORDS, Float.MAX_VALUE, pitch)
                LANG.format(sender, "music-now-playing")
                    .replace("%name%", disc.TITLE)
                    .let { sender.send(it) }

            } else {
                discPlayer.play(player.location)
            }
        }

        val playerCount = players.size

        if (playerCount >= 2) {
            LANG.format(sender, "played-music-to-multiple")
                .replace("%name%", disc.TITLE)
                .replace("%playercount%", playerCount.toString())
                .let { sender.send(it) }
        } else if (playerCount == 1) {
            LANG.format(sender, "played-music-to")
                .replace("%name%", disc.TITLE)
                .replace("%player%", players[0].name)
                .let { sender.send(it) }
        } else {
            LANG.format(sender, "played-music-to-no-one")
                .let { sender.send(it) }
        }

        return true
    }
}