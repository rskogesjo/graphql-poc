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
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
import util.CustomMetricsInstrumentation
import util.TestUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.assertNull

@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [CustomMetricsInstrumentation::class, Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class GraphQlTest {
    @Autowired
    private lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Autowired
    private lateinit var instrumentation: CustomMetricsInstrumentation

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
        repeat(5, createPerson())

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
        assertThat(personDataLoader.statistics.cacheHitCount).isGreaterThan(0)
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
        val desiredElements = 10

        assertThat(subscriptionResult(desiredElements, "Lucky").size).isEqualTo(desiredElements)
        assertThat(instrumentation.map["Lucky"]).isEqualTo(desiredElements)
    }

    @Test
    internal fun `No emitted elements if subscriber immediately disconnects`() {
        val desiredElements = 10

        assertThat(subscriptionResult(desiredElements, "Crazy", true).size).isZero
        assertNull(instrumentation.map["Crazy"])
    }

    private fun subscriptionResult(
        desiredNumberOfElements: Int,
        subscriptionItem: String,
        disconnectImmediately: Boolean = false
    ): List<Bet> {
        val result = CompletableFuture<List<Bet>>()
        val bets = mutableListOf<Bet>()

        WebSocketConnectionManager(StandardWebSocketClient(), object : TextWebSocketHandler() {
            @Override
            override fun afterConnectionEstablished(session: WebSocketSession) {
                val executionInput = ExecutionInput
                    .newExecutionInput()
                    .query(TestUtils.readTestData("graphql/subscription.graphql", subscriptionItem))
                    .build()

                session.sendMessage(TextMessage(objectMapper.writeValueAsString(executionInput)))

                if (disconnectImmediately) {
                    session.close(CloseStatus.NORMAL)
                    result.complete(bets)
                }
            }

            @Override
            override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
                val asJson = objectMapper.readValue<JsonNode>(message.payload as String)
                    .path("data")
                    .path("bet")
                    .toString()

                val bet = objectMapper.readValue(asJson, Bet::class.java)
                bets.add(bet)

                if (bet.amount == desiredNumberOfElements) {
                    session.close(CloseStatus.NORMAL)
                    result.complete(bets)
                }
            }
        }, "ws://localhost:8080/subscriptions").start()

        return result.get(5, TimeUnit.SECONDS)
    }

    private fun createPerson(): (Int) -> Unit = { graphQLTestTemplate.postForResource("graphql/create.graphql") }
}
