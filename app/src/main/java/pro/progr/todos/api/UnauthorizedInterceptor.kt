package pro.progr.todos.api

import okhttp3.Interceptor
import okhttp3.Response
import pro.progr.diamondapi.AuthInterface


class UnauthorizedInterceptor(private val auth: AuthInterface) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val resp = chain.proceed(chain.request())
        if (resp.code == 401 || resp.code == 403) {
            // сессия битая/отозвана — локально выходим
            auth.clearSession()
        }
        return resp
    }
}