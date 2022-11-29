package kr.co.marketbill.marketbillcoreserver.graphql.error

import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class CustomDataFetchingExceptionHandler : DataFetcherExceptionHandler {
    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        return if (handlerParameters.exception is CustomException) {
            val debugInfo: MutableMap<String, Any> = HashMap()
            debugInfo["cause"] = handlerParameters.exception.cause.toString()
            val graphqlError: GraphQLError = TypedGraphQLError.newInternalErrorBuilder()
                .message(handlerParameters.exception.message)
                .debugInfo(debugInfo)
                .path(handlerParameters.path).build()
            val result: DataFetcherExceptionHandlerResult = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build()
            CompletableFuture.completedFuture<DataFetcherExceptionHandlerResult>(result)
        } else {
            super.handleException(handlerParameters)
        }
    }
}