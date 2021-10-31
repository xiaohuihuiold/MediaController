package com.xhhold.plugin.service

import com.intellij.openapi.diagnostic.thisLogger
import com.xhhold.plugin.player.MediaPlayer
import org.freedesktop.DBus
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.interfaces.Properties

class LinuxMediaPlayerService : MediaPlayerService() {
    private var dBusConnection: DBusConnection? = null
    private val mprisServices = HashMap<String, MediaPlayer>()

    companion object {
        const val DBUS_PATH = "/org/freedesktop/DBus"
        const val DBUS_IFACE = "org.freedesktop.DBus"
        const val MPRIS2_PATH = "/org/mpris/MediaPlayer2"
        const val MPRIS2_PLAYER_IFACE = "org.mpris.MediaPlayer2.Player"
        const val DBUS_PROPS_IFACE = "org.freedesktop.DBus.Properties"

        const val MPRIS2_PREFIX = "org.mpris.MediaPlayer2."
    }

    override fun connect() {
        dBusConnection = runCatching {
            DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION)
        }.onSuccess {
            connected = true
            thisLogger().info("DBus connected.")
        }.onFailure {
            thisLogger().error(it)
        }.getOrNull()
        listenMprisServices()
    }

    override fun disconnect() {
        runCatching {
            dBusConnection?.close()
            dBusConnection = null
        }.onSuccess {
            thisLogger().info("DBus disconnected.")
        }.onFailure {
            thisLogger().error(it)
        }
    }

    private fun listenMprisServices() = runCatching {
        dBusConnection?.run {
            val iface = getRemoteObject(DBUS_IFACE, "/", DBus::class.java)
            iface.ListNames()?.filter { it.startsWith(MPRIS2_PREFIX) }?.map {
                val mprisIface = getRemoteObject(it, MPRIS2_PATH, Properties::class.java)
                mprisIface.GetAll(MPRIS2_PLAYER_IFACE)
            }
        }
    }.onSuccess {
        println(it)
    }.onFailure {
        thisLogger().error(it)
    }

    override fun refreshPlayers() {

    }
}