package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory

internal class OkHttpLinkFetcher private constructor(
        private val client: OkHttpClient) : (Link) -> Result<LinkResponse> {

    override fun invoke(link: Link): Result<LinkResponse> {
        if (link !is Link.Identified) {
            return Result.Failure(Throwable("unidentified link"))
        }
        val request = Request.Builder().url(link.toUrl()).build()
        val response = client.newCall(request).execute()

        val body = response.body()
        if (response.isSuccessful) {
            return Result.Success(LinkResponse(body.byteStream(), body.contentLength()))
        } else {
            body.close()
            return Result.Failure(Throwable(response.message()))
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OkHttpLinkFetcher::class.java)

        fun create(): (Link) -> Result<LinkResponse> {
            val logging = HttpLoggingInterceptor({ LOGGER.debug(it) })
                    .setLevel(HttpLoggingInterceptor.Level.BASIC)
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
            return create(client)
        }

        fun create(client: OkHttpClient): (Link) -> Result<LinkResponse> {
            return OkHttpLinkFetcher(client)
        }
    }
}