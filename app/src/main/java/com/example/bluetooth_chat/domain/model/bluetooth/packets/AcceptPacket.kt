package com.example.bluetooth_chat.domain.model.bluetooth.packets

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
class AcceptPacket(
    val id: String,
    val accepted: Boolean,
    val type: String = "accept"
){

    companion object {
        private val json = Json {
            encodeDefaults = true
        }

        fun deserialize(json: JsonObject): AcceptPacket {
            return Json.decodeFromJsonElement<AcceptPacket>(json)
        }
    }

    fun serialize(): JsonObject {
        return json.encodeToJsonElement(this).jsonObject
    }
}
