package yucatalks.micronaut.filters

import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import jakarta.inject.Inject
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import yucatalks.micronaut.clients.AnalyticsKafkaClient
import yucatalks.micronaut.models.Book

@Filter("/books/?*")
class AnalyticsFilter: HttpServerFilter {
    @Inject
    lateinit var analyticsKafkaClient: AnalyticsKafkaClient

    override fun doFilter(request: HttpRequest<*>?, chain: ServerFilterChain?): Publisher<MutableHttpResponse<*>> =
        Flux.from(chain?.proceed(request))
            .flatMap {
                response: MutableHttpResponse<*> ->
                val book = response.getBody(Book::class.java).orElse(null)
                if (book == null) {
                    Flux.just(response)
                } else {
                    Flux.from(analyticsKafkaClient.updateAnalytics(book)).map { b -> response }
                }
            }

}