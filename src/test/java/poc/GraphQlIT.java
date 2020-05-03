package poc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GraphQlIT {
    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void canCreatePerson() throws IOException {
        final GraphQLResponse rawResponse = graphQLTestTemplate.postForResource("graphql/create.graphql");

        final JsonNode response = parseResponse(rawResponse.getRawResponse().getBody());
        final JsonNode createdPersonNode = response.path("data").path("createPerson");

        assertTrue(rawResponse.isOk());
        assertThat(createdPersonNode.path("id").asInt(), equalTo(1));
        assertThat(createdPersonNode.path("name").asText(), equalTo("Robin"));
        assertThat(createdPersonNode.path("age").asInt(), equalTo(34));
    }

    @Test
    public void canGetSeveralPeople() throws IOException {
        final int expectedNumberOfPeople = 5;

        for (int i = 0; i < expectedNumberOfPeople; i++) {
            graphQLTestTemplate.postForResource("graphql/create.graphql");
        }

        final GraphQLResponse rawResponse = graphQLTestTemplate.postForResource("graphql/get-all.graphql");
        final JsonNode response = parseResponse(rawResponse.getRawResponse().getBody());

        assertTrue(rawResponse.isOk());
        assertThat(response.path("data").path("getAll").size(), equalTo(expectedNumberOfPeople));
    }

    @Test
    public void canGetSinglePerson() throws IOException {
        graphQLTestTemplate.postForResource("graphql/create.graphql");

        final GraphQLResponse rawResponse = graphQLTestTemplate.postForResource("graphql/get-one.graphql");

        final JsonNode response = parseResponse(rawResponse.getRawResponse().getBody());
        final JsonNode getSinglePersonNode = response.path("data").path("getOne");

        assertTrue(rawResponse.isOk());
        assertThat(getSinglePersonNode.path("id").asInt(), equalTo(1));
        assertThat(getSinglePersonNode.path("name").asText(), equalTo("Robin"));
        assertThat(getSinglePersonNode.path("age").asInt(), equalTo(34));
    }

    private JsonNode parseResponse(String rawResponse) throws JsonProcessingException {
        return objectMapper.readTree(rawResponse);
    }
}
