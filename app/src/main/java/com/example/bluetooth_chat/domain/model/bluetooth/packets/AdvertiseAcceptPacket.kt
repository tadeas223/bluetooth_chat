package com.example.bluetooth_chat.domain.model.bluetooth.packets

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
class AdvertiseAcceptPacket(
    val accepted: Boolean
){
    val type: String = "advertise"

    companion object {
        fun deserialize(json: JsonObject): AdvertiseAcceptPacket {
            return Json.decodeFromJsonElement<AdvertiseAcceptPacket>(json)
        }
    }

    fun serialize(): JsonObject {
        return Json.encodeToJsonElement(this).jsonObject
    }
}
