package util

import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.schema.DataFetcher
import org.springframework.stereotype.Component
import poc.model.Bet

@Component
class CustomMetricsInstrumentation : TracingInstrumentation() {
    val map = mutableMapOf<String, Int>()

    @Override
    override fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>?,
        parameters: InstrumentationFieldFetchParameters?
    ): DataFetcher<*> {

        if (parameters?.environment?.getSource<Any>() is Bet) {
            val bet = parameters.environment?.getSource() as Bet

            map[bet.horse] = bet.amount
        }

        return super.instrumentDataFetcher(dataFetcher, parameters)
    }


}
