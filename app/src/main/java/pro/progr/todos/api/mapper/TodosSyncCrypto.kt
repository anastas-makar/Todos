package pro.progr.todos.api.mapper

import pro.progr.personalcrypto.EncryptionContext
import pro.progr.personalcrypto.PersonalCrypto

private val TODOS_SYNC_CONTEXT = EncryptionContext(
    module = "todos",
    entityType = "sync",
    entityId = "payload",
    fieldName = "value"
)

internal fun PersonalCrypto.encryptTodosValue(value: String): String =
    encrypt(value, TODOS_SYNC_CONTEXT)

internal fun PersonalCrypto.decryptTodosValue(value: String): String =
    decrypt(value, TODOS_SYNC_CONTEXT)
