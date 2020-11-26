package poc

import com.graphql.spring.boot.test.GraphQLTestTemplate
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class GraphQlIT {
    @Autowired
    private lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Test
    fun `can get several people`() {
        val expectedNumberOfPeople = 5

        repeat(expectedNumberOfPeople) { graphQLTestTemplate.postForResource("graphql/create.graphql") }

        val rawResponse = graphQLTestTemplate.postForResource("graphql/get-all.graphql")
        val response = rawResponse.readTree()

        assertTrue(rawResponse.isOk)
        assertThat(response.path("data").path("getAll").size(), equalTo(expectedNumberOfPeople))
    }

    @Test
    fun `can get single person`() {
        graphQLTestTemplate.postForResource("graphql/create.graphql")

        val rawResponse = graphQLTestTemplate.postForResource("graphql/get-one.graphql")

        val response = rawResponse.readTree()
        val singlePersonNode = response.path("data").path("getOne")

        assertTrue(rawResponse.isOk)
        assertThat(singlePersonNode.path("id").asInt(), equalTo(1))
        assertThat(singlePersonNode.path("name").asText(), equalTo("Robin"))
        assertThat(singlePersonNode.path("age").asInt(), equalTo(34))
    }
}
