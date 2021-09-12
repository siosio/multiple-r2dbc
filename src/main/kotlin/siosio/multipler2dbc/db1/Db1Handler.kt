package siosio.multipler2dbc.db1

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.UUID

@Component
class Db1Handler(private val hogeRepository: HogeRepository) {
    suspend fun post(request: ServerRequest) : ServerResponse {
        val hoge = hogeRepository.save(Hoge(UUID.randomUUID().toString(), "hoge")).awaitSingle()
        return ServerResponse.ok().bodyValueAndAwait(mapOf("id" to hoge.id))
    }
}

@Repository
interface HogeRepository: ReactiveCrudRepository<Hoge, String>

//create table hoge (id char(36) not null, name varchar(100), primary key(id))
data class Hoge(
    @Id private val id: String,
    val name: String
) : Persistable<String> {
    override fun isNew(): Boolean = true
    override fun getId() = id
}

