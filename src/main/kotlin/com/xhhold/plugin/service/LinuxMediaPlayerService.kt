package com.xhhold.plugin.service

import com.intellij.openapi.diagnostic.thisLogger
import com.xhhold.plugin.entity.PlayerProperties
import com.xhhold.plugin.player.LinuxMediaPlayer
import org.freedesktop.DBus
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.interfaces.Properties

class LinuxMediaPlayerService : MediaPlayerService() {
    var dBusConnection: DBusConnection? = null
    private val mprisPlayers = HashMap<String, LinuxMediaPlayer>()

    companion object {
        const val DBUS_PATH = "/org/freedesktop/DBus"
        const val DBUS_INTERFACE = "org.freedesktop.DBus"
        const val DBUS_PROPS_INTERFACE = "org.freedesktop.DBus.Properties"
        const val MPRIS2_PATH = "/org/mpris/MediaPlayer2"
        const val MPRIS2_ROOT_INTERFACE = "org.mpris.MediaPlayer2"
        const val MPRIS2_PLAYER_INTERFACE = "org.mpris.MediaPlayer2.Player"

        const val MPRIS2_PREFIX = "org.mpris.MediaPlayer2."
    }

    override fun connect() = synchronized(this) {
        if (connected) {
            return@synchronized
        }
        dBusConnection = runCatching {
            DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION)
        }.onSuccess {
            connected = true
            onConnected?.invoke()
            thisLogger().info("DBus connected.")
        }.onFailure {
            thisLogger().error(it)
        }.getOrNull()
        listenMprisServices()
    }

    override fun disconnect() = synchronized(this) {
        if (!connected) {
            return@synchronized
        }
        runCatching {
            dBusConnection?.close()
            dBusConnection = null
        }.onSuccess {
            connected = false
            onDisconnected?.invoke()
            thisLogger().info("DBus disconnected.")
        }.onFailure {
            thisLogger().error(it)
        }
    }

    private fun listenMprisServices() = runCatching {
        dBusConnection?.run {
            addSigHandler(DBus.NameAcquired::class.java) {
                if (!it.name.startsWith(MPRIS2_PREFIX)) {
                    return@addSigHandler
                }
                runCatching {
                    val dbus = getRemoteObject(DBUS_INTERFACE, "/", DBus::class.java)
                    val mprisProperties = getRemoteObject(it.name, MPRIS2_PATH, Properties::class.java)
                    val identityVariant = mprisProperties.GetAll(MPRIS2_ROOT_INTERFACE)["Identity"]
                    val identity = identityVariant?.value as String?
                    val player = mprisPlayers[it.name]
                    player?.close()
                    mprisPlayers[it.name] =
                        LinuxMediaPlayer(
                            this@LinuxMediaPlayerService,
                            onPlayerChanged,
                            dbus.GetNameOwner(it.name),
                            PlayerProperties(id = it.name, name = identity ?: it.name)
                        )
                    onPlayersChanged?.invoke(mprisPlayers.values.toMutableList())
                }.onFailure { err ->
                    thisLogger().error(err)
                }
            }
            addSigHandler(DBus.NameLost::class.java) {
                if (!it.name.startsWith(MPRIS2_PREFIX)) {
                    return@addSigHandler
                }
                runCatching {
                    val player = mprisPlayers.remove(it.name)
                    player?.close()
                    onPlayersChanged?.invoke(mprisPlayers.values.toMutableList())
                }.onFailure { err ->
                    thisLogger().error(err)
                }
            }
            refreshPlayers()
        }
    }.onFailure {
        thisLogger().error(it)
    }

    override fun refreshPlayers() {
        runCatching {
            dBusConnection?.run {
                val dbus = getRemoteObject(DBUS_INTERFACE, "/", DBus::class.java)
                dbus.ListNames()?.filter { it.startsWith(MPRIS2_PREFIX) }?.map {
                    runCatching {
                        val mprisProperties = getRemoteObject(it, MPRIS2_PATH, Properties::class.java)
                        val identityVariant = mprisProperties.GetAll(MPRIS2_ROOT_INTERFACE)["Identity"]
                        val identity = identityVariant?.value as String?
                        val player = mprisPlayers[it]
                        player ?: LinuxMediaPlayer(
                            this@LinuxMediaPlayerService,
                            onPlayerChanged,
                            dbus.GetNameOwner(it),
                            PlayerProperties(id = it, name = identity ?: it)
                        )
                    }.onFailure {
                        thisLogger().error(it)
                    }
                }?.mapNotNull { it.getOrNull() }
            }
        }.onSuccess {
            mprisPlayers.clear()
            it?.forEach { player -> mprisPlayers[player.playerProperties.id] = player }
            onPlayersChanged?.invoke(mprisPlayers.values.toMutableList())
        }.onFailure {
            thisLogger().error(it)
        }
    }
}