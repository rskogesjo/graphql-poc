package poc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.graphql.spring.boot.test.GraphQLTestTemplate
import graphql.ExecutionInput
import org.assertj.core.api.Assertions.assertThat
import org.dataloader.DataLoader
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import poc.model.Bet
import poc.model.Person
import util.TestUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull

@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class GraphQlTest {
    @Autowired
    private lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Autowired
    private lateinit var personDataLoader: DataLoader<Int, Person>

    private val objectMapper = jacksonObjectMapper().registerKotlinModule()

    @Test
    internal fun `create person`() {
        val rawResponse = graphQLTestTemplate.postForResource("graphql/create.graphql")

        val response = rawResponse.readTree()
        val createdPersonNode = response.path("data").path("createPerson")

        assertTrue(rawResponse.isOk)
        assertThat(createdPersonNode.path("id").asInt()).isEqualTo(1)
        assertThat(createdPersonNode.path("name").asText()).isEqualTo("Robin")
        assertThat(createdPersonNode.path("age").asInt()).isEqualTo(34)
    }

    @Test
    internal fun `get several people`() {
        val expectedNumberOfPeople = 5
        repeat(expectedNumberOfPeople, createPerson())

        val rawResponse = graphQLTestTemplate.postForResource("graphql/get-all.graphql")
        val response = rawResponse.readTree()

        assertTrue(rawResponse.isOk)
        assertThat(response.path("data").path("getAll").size()).isEqualTo(expectedNumberOfPeople)
    }

    @Test
    fun `get using data loader`() {
        val numberOfPersons = 5.toLong()
        repeat(numberOfPersons.toInt(), createPerson())

        val rawResponse = graphQLTestTemplate.postForResource("graphql/get-using-data-loader.graphql")
        assertTrue(rawResponse.isOk)

        val response = rawResponse.readTree()
        val persons = response.path("data").path("getByDataLoader")

        persons.toList().forEachIndexed { index, person ->
            assertThat(person.path("id").asInt()).isEqualTo(index + 1)
            assertThat(person.path("name").asText()).isEqualTo("Robin")
        }

        assertThat(personDataLoader.statistics.cacheHitCount).isZero
        assertTrue(graphQLTestTemplate.postForResource("graphql/get-using-data-loader.graphql").isOk)
        assertThat(personDataLoader.statistics.cacheHitCount).isEqualTo(numberOfPersons)
    }

    @Test
    internal fun `get single person`() {
        graphQLTestTemplate.postForResource("graphql/create.graphql")

        val rawResponse = graphQLTestTemplate.postForResource("graphql/get-one.graphql")

        val response = rawResponse.readTree()
        val singlePersonNode = response.path("data").path("getOne")

        assertTrue(rawResponse.isOk)
        assertThat(singlePersonNode.path("id").asInt()).isEqualTo(1)
        assertThat(singlePersonNode.path("name").asText()).isEqualTo("Robin")
        assertThat(singlePersonNode.path("age").asInt()).isEqualTo(34)
    }

    @Test
    internal fun `subscribe over websocket`() {
        assertNotNull(subscriptionResult())
    }

    private fun subscriptionResult(): Bet? {
        val token = "Robin-Token"
        val result = CompletableFuture<Bet>()

        val manager = WebSocketConnectionManager(
            StandardWebSocketClient(),
            handler(result),
            "ws://localhost:8080/subscriptions"
        ).apply { headers[HttpHeaders.AUTHORIZATION] = listOf(token) }

        manager.start()

        return result.get(5, TimeUnit.SECONDS)
    }

    private fun handler(result: CompletableFuture<Bet>) = object : TextWebSocketHandler() {
        @Override
        override fun afterConnectionEstablished(session: WebSocketSession) {
            val executionInput = ExecutionInput
                .newExecutionInput()
                .query(TestUtils.readTestData("graphql/subscription.graphql"))
                .build()

            session.sendMessage(TextMessage(objectMapper.writeValueAsString(executionInput)))
        }

        @Override
        override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
            val asJson = objectMapper.readValue<JsonNode>(message.payload as String)
                .path("data")
                .path("bet")
                .toString()

            val bet = objectMapper.readValue(asJson, Bet::class.java)
            result.complete(bet)
            session.close(CloseStatus.NORMAL)
        }
    }

    private fun createPerson(): (Int) -> Unit = { graphQLTestTemplate.postForResource("graphql/create.graphql") }
}
