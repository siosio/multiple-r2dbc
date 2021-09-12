package siosio.multipler2dbc

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.function.server.coRouter
import siosio.multipler2dbc.db1.Db1Handler
import siosio.multipler2dbc.db2.Db2Handler

@SpringBootApplication
class MultipleR2dbcApplication

fun main(args: Array<String>) {
    runApplication<MultipleR2dbcApplication>(*args)
}

@Configuration
class Config(
    private val db1Handler: Db1Handler,
    private val db2Handler: Db2Handler
) {
    @Bean
    fun router() = coRouter {
        POST("/db1", db1Handler::post)
        POST("/db2", db2Handler::post)
    }
}

@Configuration
@EnableR2dbcRepositories(
    basePackages = ["siosio.multipler2dbc.db1"],
    entityOperationsRef = "db1EntityTemplate"
)
class Db1Config {

    @Bean
    @ConfigurationProperties(prefix = "db1")
    fun db1Properties(): R2dbcProperties = R2dbcProperties()

    @Bean
    fun db1ConnectionFactory(): ConnectionFactory = db1Properties().toConnectionFactory()

    @Bean
    fun db1EntityTemplate() = R2dbcEntityTemplate(db1ConnectionFactory())
}

@Configuration
@EnableR2dbcRepositories(
    basePackages = ["siosio.multipler2dbc.db2"],
    entityOperationsRef = "db2EntityTemplate"
)
class Db2Config {

    @Bean
    @ConfigurationProperties(prefix = "db2")
    fun db2Properties(): R2dbcProperties = R2dbcProperties()

    @Bean
    fun db2ConnectionFactory(): ConnectionFactory = db2Properties().toConnectionFactory()

    @Bean
    fun db2EntityTemplate() = R2dbcEntityTemplate(db2ConnectionFactory())
}

fun R2dbcProperties.toConnectionFactory(): ConnectionFactory {
    val connectionFactory = ConnectionFactoryOptions.parse(url)
        .mutate()
        .apply {
            username?.let { option(ConnectionFactoryOptions.USER, it) }
            password?.let { option(ConnectionFactoryOptions.PASSWORD, it) }

        }.build().let {
            ConnectionFactories.get(it)
        }
    val poolConfiguration = ConnectionPoolConfiguration
        .builder(connectionFactory)
        .maxSize(pool.maxSize)
        .initialSize(pool.initialSize)
        .build()
    return ConnectionPool(poolConfiguration)
}
