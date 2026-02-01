package com.example.bluetooth_chat.domain.model.bluetooth.packets

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
class AdvertisePacket(
    val id: String,
    val type: String = "advertise"
){

    companion object {
        private val json = Json {
            encodeDefaults = true
        }

        fun deserialize(json: JsonObject): AdvertisePacket {
            return Json.decodeFromJsonElement<AdvertisePacket>(json)
        }
    }

    fun serialize(): JsonObject {
        return json.encodeToJsonElement(this).jsonObject
    }
}