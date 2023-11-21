package io.micronaut.multitenancy.propagation.cookie

import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn

@Requires(property = 'spec.name', value = 'multitenancy.cookie.gateway')
@Controller("/")
class GatewayController {

    private final BookFetcher bookFetcher

    GatewayController(BookFetcher bookFetcher) {
        this.bookFetcher = bookFetcher
    }

    @Get("/")
    @ExecuteOn(TaskExecutors.BLOCKING)
    List<String> index() {
        List<String> booksNames = bookFetcher.findAll()

        booksNames
    }
}
