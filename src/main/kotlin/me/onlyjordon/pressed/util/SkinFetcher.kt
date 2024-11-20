package me.onlyjordon.pressed.util

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.util.MojangAPIUtil
import java.util.*
import kotlin.collections.ArrayList

object SkinFetcher {
    fun fetchSkin(name: String) : MutableList<TextureProperty> {
        return try { MojangAPIUtil.requestPlayerTextureProperties(MojangAPIUtil.requestPlayerUUID(name)) } catch (exception: Exception) { return ArrayList<TextureProperty>() }
    }
}