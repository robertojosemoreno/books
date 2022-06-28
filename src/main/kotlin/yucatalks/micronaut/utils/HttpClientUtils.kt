package yucatalks.micronaut.utils

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import yucatalks.micronaut.models.Book
import java.util.*

class HttpClientUtils {
    companion object {
        fun retrieveGet(url: String, client: HttpClient): Optional<*>? = client
            .toBlocking()
            .retrieve(
                HttpRequest.GET<Any>(url),
                Argument.of(Optional::class.java, Book::class.java))
    }
}