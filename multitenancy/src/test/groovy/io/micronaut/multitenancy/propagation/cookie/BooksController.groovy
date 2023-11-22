package io.micronaut.multitenancy.propagation.cookie

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Requires(property = 'spec.name', value = 'multitenancy.cookie.gorm')
@CompileStatic
@Controller("/api")
class BooksController {

    private final BookService bookService

    BooksController(BookService bookService) {
        this.bookService = bookService
    }

    @Get("/books")
    List<String> books() {
        List<Book> books = bookService.list()
        books*.title
    }
}
