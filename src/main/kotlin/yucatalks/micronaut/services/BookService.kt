package yucatalks.micronaut.services

import jakarta.inject.Singleton
import yucatalks.micronaut.models.Book
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import javax.annotation.PostConstruct

@Singleton
class BookService {

    private val bookStore: MutableList<Book> = mutableListOf();
    private val received: MutableCollection<Book> = ConcurrentLinkedDeque()

    @PostConstruct
    fun init() {
        bookStore.add(Book("1491950358", "Building Microservices"))
        bookStore.add(Book("1680502395", "Release It!"))
        bookStore.add(Book("0321601912", "Continuous Delivery"))
    }

    fun listAll(): List<Book> = bookStore

    fun findByIsbn(isbn: String): Optional<Book> =
        bookStore.stream()
                 .filter{ (i) -> i == isbn}
                 .findFirst()

    fun getKafkaBookLog():List<Book> = received.toMutableList()

    fun pushKafkaBookLog(book: Book) {
        received.add(book);
    }

    fun cleanKafkaBookLog() {
        received.clear();
    }

}