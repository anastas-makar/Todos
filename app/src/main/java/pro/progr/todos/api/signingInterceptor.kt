package pro.progr.todos.api

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import pro.progr.diamondapi.AuthInterface
import java.util.Locale

fun signingInterceptor(auth: AuthInterface): Interceptor = Interceptor { chain ->
    val orig = chain.request()

    // нет сессии — ничего не подписываем
    val sid = auth.getSessionId()
    if (sid.isNullOrBlank()) return@Interceptor chain.proceed(orig)

    val did = auth.getDeviceId()
    val nonce = auth.getNonce()
    val method = orig.method.uppercase(Locale.ROOT)

    val url = orig.url
    val pathQuery = buildString {
        append(url.encodedPath)
        url.encodedQuery?.let { append('?').append(it) }
    }

    // читаем тело в байты (если есть)
    val (bodyBytes, rebuiltBody) = readBodyOnce(orig.body)

    // текущее время устройства (в секундах); для логов/слабой диагностики
    val nowSec = auth.getEpochSecond()

    // считаем подпись
    val sign = auth.getHash(
        sessionId = sid,
        deviceId = did,
        nonce = nonce,
        method = method,
        pathQuery = pathQuery,
        epochSecond = nowSec,
        bodyBytes = bodyBytes
    )

    // пересобираем запрос: если было тело — кладём восстановленное
    val reqBuilder = orig.newBuilder()
        .method(method, rebuiltBody)
        .header("Authorization", "Bearer $sid")
        .header("X-Device-Id", did)
        .header("X-Nonce", nonce)
        .header("X-Time", nowSec.toString())
        .header("X-Sign", sign)
        .header("X-Sign-Alg", auth.getSignAlg())

    chain.proceed(reqBuilder.build())
}

private fun readBodyOnce(body: RequestBody?): Pair<ByteArray, RequestBody?> {
    if (body == null) return 0.toByteArray() to null
    // multipart/streaming: для твоих JSON-запросов ок; если будут гигабайты — нужно другое решение
    val buf = Buffer()
    body.writeTo(buf)
    val bytes = buf.readByteArray()
    val ct: MediaType? = body.contentType()
    val rebuilt = bytes.toRequestBody(ct)
    return bytes to rebuilt
}

private fun Int.toByteArray(): ByteArray = ByteArray(0)
