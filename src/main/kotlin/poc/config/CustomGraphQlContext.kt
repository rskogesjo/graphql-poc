package poc.config

import graphql.servlet.DefaultGraphQLContextBuilder
import graphql.servlet.GraphQLContext
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import poc.model.Person
import javax.servlet.http.HttpServletRequest


@Component
open class CustomGraphQlContext(
    @Autowired private val dataLoader: DataLoader<Int, Person>
) : DefaultGraphQLContextBuilder() {

    companion object {
        const val PERSON_DATA_LOADER = "personDataLoader"
    }

    @Override
    override fun build(httpServletRequest: HttpServletRequest): GraphQLContext {
        val context = GraphQLContext(httpServletRequest)
        context.setDataLoaderRegistry(createDataLoaderRegistry())

        return context
    }

    private fun createDataLoaderRegistry(): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        registry.register(PERSON_DATA_LOADER, dataLoader)

        return registry
    }
}
