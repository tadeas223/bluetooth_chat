package com.macatadeas.bluetooth_chat.data.repository

import com.macatadeas.bluetooth_chat.domain.model.Contact

fun ContactEntity.toContact(): Contact {
    return Contact(
        id = this.id,
        username = this.username,
        address = this.address,
    )
}

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        id = this.id,
        username = this.username,
        address = this.address,
    )
}
