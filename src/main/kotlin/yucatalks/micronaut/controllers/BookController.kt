package yucatalks.micronaut.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject
import yucatalks.micronaut.models.Book
import yucatalks.micronaut.services.BookService
import java.util.Optional

@Controller("/books")
class BookController() {
    @Inject
    lateinit var bookService: BookService

    @Get
    fun listAll(): List<Book> = bookService.listAll();

    @Get("/{isbn}")
    fun findBook(isbn: String): Optional<Book> = bookService.findByIsbn(isbn)

    @Get("/kafkaBookLog")
    fun getKafkaBookLog(): List<Book> = bookService.getKafkaBookLog()

}