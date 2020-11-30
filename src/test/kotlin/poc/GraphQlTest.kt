package poc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.graphql.spring.boot.test.GraphQLTestTemplate
import graphql.ExecutionInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import util.TestUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class GraphQlTest {
    @Autowired
    private lateinit var graphQLTestTemplate: GraphQLTestTemplate

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

        repeat(expectedNumberOfPeople) { graphQLTestTemplate.postForResource("graphql/create.graphql") }

        val rawResponse = graphQLTestTemplate.postForResource("graphql/get-all.graphql")
        val response = rawResponse.readTree()

        assertTrue(rawResponse.isOk)
        assertThat(response.path("data").path("getAll").size()).isEqualTo(expectedNumberOfPeople)
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
    internal fun `subscribe to data`() {
        val response = CompletableFuture<String>()

        WebSocketConnectionManager(StandardWebSocketClient(), object : TextWebSocketHandler() {
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
                response.complete(objectMapper.readValue<JsonNode>(message.payload as String)
                        .path("data")
                        .path("bet")
                        .path("horse")
                        .asText()
                )
            }
        }, "ws://localhost:8080/subscriptions").start()

        assertThat(response.get(5, TimeUnit.SECONDS)).isEqualTo("Lucky")
    }
}
