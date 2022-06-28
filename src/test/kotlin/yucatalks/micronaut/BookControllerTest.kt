package yucatalks.micronaut

import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import yucatalks.micronaut.models.Book
import yucatalks.micronaut.services.BookService
import yucatalks.micronaut.utils.HttpClientUtils
import java.util.Optional
import java.util.concurrent.TimeUnit.SECONDS

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerTest {

    @Inject
    lateinit var bookService: BookService;

    @Inject
    lateinit var analyticsListener: AnalyticsListener

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun testMessageIsPublishedToKafkaWhenBookFound(){
        val isbn = "1491950358"
        val result: Optional<Book> = HttpClientUtils.retrieveGet("/books/"+isbn, client ) as Optional<Book>
        assertNotNull(result)
        assertTrue(result.isPresent)
        assertEquals(isbn, result.get().isbn)

        await().atMost(5, SECONDS).until { !bookService.getKafkaBookLog().isEmpty() }

        val bookFromKafka = bookService.getKafkaBookLog().iterator().next()
        assertNotNull(bookFromKafka)
        assertEquals(isbn, bookFromKafka.isbn)
    }

    @Test
    fun testMessageIsNotPublishedToKafkaWhenBookNotFound(){
        assertThrows(HttpClientResponseException::class.java) {
            HttpClientUtils.retrieveGet("/books/INVALID", client)
        }
        Thread.sleep(5_000)
        assertEquals(0, bookService.getKafkaBookLog().size)
    }

    @AfterEach
    fun cleanup() {
        bookService.cleanKafkaBookLog()
    }

    @KafkaListener(offsetReset = OffsetReset.EARLIEST)
    class AnalyticsListener {
        @Inject
        lateinit var bookService: BookService

        @Topic("analytics")
        fun updateAnalytics(book: Book) {
            bookService.pushKafkaBookLog(book);
        }
    }

}