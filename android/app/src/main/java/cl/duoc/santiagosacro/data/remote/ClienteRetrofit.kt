package cl.duoc.santiagosacro.data.remote

import cl.duoc.santiagosacro.BuildConfig
import cl.duoc.santiagosacro.core.SesionLocal
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Construye el cliente HTTP.
 * El interceptor agrega automaticamente la cabecera Authorization
 * cuando hay una sesion iniciada, asi ningun repositorio tiene que recordarlo.
 */
object ClienteRetrofit {

    fun crear(sesion: SesionLocal): ApiSantiagoSacro {

        val interceptorToken = Interceptor { cadena ->
            val original = cadena.request()
            val token = sesion.token

            val peticion = if (token.isNullOrBlank()) {
                original
            } else {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build()
            }

            cadena.proceed(peticion)
        }

        val registro = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val cliente = OkHttpClient.Builder()
            .addInterceptor(interceptorToken)
            .addInterceptor(registro)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.URL_API)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiSantiagoSacro::class.java)
    }
}
