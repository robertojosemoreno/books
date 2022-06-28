package yucatalks.micronaut.clients

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.Topic
import reactor.core.publisher.Mono
import yucatalks.micronaut.models.Book

@KafkaClient
interface AnalyticsKafkaClient {
    @Topic("analytics")
    fun updateAnalytics(book: Book): Mono<Book>
}