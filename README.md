# Модуль с задачами

Сетевые модели синхронизации и мапперы версии 1 находятся в `api/v1`.
`SyncRepository` создаёт UUID `syncId` один раз на одну синхронизацию и отправляет
его вместе с `syncProtocolVersion` в `/sync/start` и `/sync/finish`.

Если сервер возвращает `426 APP_UPDATE_REQUIRED`, фоновая задача завершается
без повторов с данными `sync_error_code`, `sync_user_message` и
`minimum_sync_protocol_version`. Приложение, запускающее WorkManager, может
показать сообщение пользователю, наблюдая за результатом задачи и читая
`outputData`.
