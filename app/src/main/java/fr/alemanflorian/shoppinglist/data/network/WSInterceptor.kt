package fr.alemanflorian.shoppinglist.data.network

import android.accounts.NetworkErrorException
import fr.alemanflorian.shoppinglist.common.error.NoInternetConnectionException
import fr.alemanflorian.shoppinglist.common.error.WSException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutionException

class WSApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            val builder = request.newBuilder()
            // Add header
            response = chain.proceed(builder.build())
        } catch (exception : Exception) {
            throw when (exception) {
                is ExecutionException -> NoInternetConnectionException()
                is IOException -> NoInternetConnectionException()
                else -> NetworkErrorException(exception)
            }
        }

        if (!response.isSuccessful) {
            throw WSException(response)
        }

        return response
    }
}