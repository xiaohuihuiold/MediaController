package com.xhhold.plugin.player

import com.intellij.openapi.diagnostic.thisLogger
import com.xhhold.plugin.entity.MediaMetadata
import com.xhhold.plugin.entity.MediaPlayState
import com.xhhold.plugin.entity.PlayerProperties
import com.xhhold.plugin.service.LinuxMediaPlayerService
import com.xhhold.plugin.service.OnPlayerChanged
import org.freedesktop.dbus.DBusMap
import org.freedesktop.dbus.interfaces.Properties
import org.freedesktop.dbus.types.Variant

class LinuxMediaPlayer(
    private val playerService: LinuxMediaPlayerService,
    private val onPlayerChanged: OnPlayerChanged?,
    private val busName: String,
    playerProperties: PlayerProperties
) :
    MediaPlayer(playerProperties) {

    init {
        runCatching {
            playerService.dBusConnection?.apply {
                addSigHandler(Properties.PropertiesChanged::class.java, busName) {
                    onPropertiesChanged(it.propertiesChanged)
                }
            }
        }.onFailure {
            thisLogger().error(it)
        }
        refreshPlayer()
    }

    private fun refreshPlayer() = runCatching {
        playerService.dBusConnection?.run {
            val mprisProperties =
                getRemoteObject(playerProperties.id, LinuxMediaPlayerService.MPRIS2_PATH, Properties::class.java)
            val properties = mprisProperties.GetAll(LinuxMediaPlayerService.MPRIS2_PLAYER_INTERFACE)
            properties
        }
    }.onSuccess {
        if (it == null) {
            return@onSuccess
        }
        onPropertiesChanged(it)
    }.onFailure {
        thisLogger().error(it)
    }


    private fun onPropertiesChanged(properties: Map<String, Variant<*>>) = runCatching {

        val canControl = properties["CanControl"]?.value as Boolean?
        val canPlay = properties["CanPlay"]?.value as Boolean?
        val canPause = properties["CanPause"]?.value as Boolean?
        val canGoPrevious = properties["CanGoPrevious"]?.value as Boolean?
        val canGoNext = properties["CanGoNext"]?.value as Boolean?
        val canSeek = properties["CanSeek"]?.value as Boolean?
        val playStatus = properties["PlaybackStatus"]?.value as String?
        val position = properties["Position"]?.value as Long?
        val metadata = properties["Metadata"]?.value as DBusMap<*, *>?
        val playState = when (playStatus) {
            "Playing" -> MediaPlayState.PLAYING
            "Paused" -> MediaPlayState.PAUSED
            else -> null
        }
        val playerChanged = canControl != playerProperties.canControl
                || canPlay != playerProperties.canPlay
                || canPause != playerProperties.canPause
                || canGoPrevious != playerProperties.canGoPrevious
                || canGoNext != playerProperties.canGoNext
                || canSeek != playerProperties.canSeek
                || playState != playerProperties.playState
                || position != playerProperties.position

        playerProperties.canControl = canControl ?: playerProperties.canControl
        playerProperties.canPlay = canPlay ?: playerProperties.canPlay
        playerProperties.canPause = canPause ?: playerProperties.canPause
        playerProperties.canGoPrevious = canGoPrevious ?: playerProperties.canGoPrevious
        playerProperties.canGoNext = canGoNext ?: playerProperties.canGoNext
        playerProperties.canSeek = canSeek ?: playerProperties.canSeek
        playerProperties.playState = playState ?: playerProperties.playState
        playerProperties.position = position ?: playerProperties.position

        var metadataChanged = false
        metadata?.also { data ->
            val title = (data["xesam:title"] as Variant<*>?)?.value as String?
            val album = (data["xesam:album"] as Variant<*>?)?.value as String?
            val artUrl = (data["mpris:artUrl"] as Variant<*>?)?.value as String?
            val artists = ((data["xesam:artist"] as Variant<*>?)?.value as ArrayList<*>?)
                ?.map { artist -> artist.toString() }
                ?.toMutableList()
            var duration = (data["mpris:length"] as Variant<*>?)?.value as Long?
            val updateTime = System.currentTimeMillis()
            val metadataObject = playerProperties.metadata ?: MediaMetadata()
            if (duration != null) {
                duration /= 1000
            }

            var artistsChanged = false
            if (artists != null && metadataObject.artists == null) {
                artistsChanged = true
            }
            if (artists == null && metadataObject.artists != null) {
                artistsChanged = true
            }
            metadataObject.artists?.also {
                if (artists != null) {
                    val tempList = artists.toMutableList()
                    tempList.removeAll(it)
                    artistsChanged = tempList.isNotEmpty()
                }
            }
            metadataChanged = title != metadataObject.title
                    || album != metadataObject.album
                    || artUrl != metadataObject.artUrl
                    || artistsChanged
                    || duration != metadataObject.duration

            playerProperties.metadata = metadataObject.copy(
                title = title ?: metadataObject.title,
                album = album ?: metadataObject.album,
                artUrl = artUrl ?: metadataObject.artUrl,
                artists = artists ?: metadataObject.artists,
                duration = duration ?: metadataObject.duration,
                updateTime = updateTime
            )
        }

        if (playerChanged || metadataChanged) {
            onPlayerChanged?.invoke(this)
        }
    }.onFailure { err ->
        thisLogger().error(err)
    }

    fun close() {}

}