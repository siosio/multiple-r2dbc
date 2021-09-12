package siosio.multipler2dbc.db2

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class Db2Handler(private val fugaRepository: FugaRepository) {
    suspend fun post(request: ServerRequest): ServerResponse {
        val fuga = fugaRepository.save(Fuga(name = "fuga")).awaitSingle()
        return ServerResponse.ok().bodyValueAndAwait(mapOf("id" to fuga.id))
    }
}

@Repository
interface FugaRepository : ReactiveCrudRepository<Fuga, Int>

//create table fuga (id serial not null, name varchar(100), primary key(id))
data class Fuga(
    @Id val id: Int? = null,
    val name: String
)
