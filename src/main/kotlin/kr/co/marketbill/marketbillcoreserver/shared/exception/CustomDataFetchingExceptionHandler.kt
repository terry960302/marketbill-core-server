package kr.co.marketbill.marketbillcoreserver.shared.exception

import com.netflix.graphql.types.errors.ErrorType
import com.netflix.graphql.types.errors.TypedGraphQLError
import com.netflix.graphql.types.errors.TypedGraphQLError.newBuilder
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import kr.co.marketbill.marketbillcoreserver.shared.exception.InternalErrorException
import kr.co.marketbill.marketbillcoreserver.shared.exception.NotFoundException
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class CustomDataFetchingExceptionHandler : DataFetcherExceptionHandler {
    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        return when (handlerParameters.exception) {
            is InternalErrorException -> {
                val debugInfo: MutableMap<String, Any> = HashMap()
                val graphqlError: GraphQLError = TypedGraphQLError.newInternalErrorBuilder()
                    .message(handlerParameters.exception.message)
                    .debugInfo(debugInfo)
                    .path(handlerParameters.path).build()
                val result: DataFetcherExceptionHandlerResult = DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError)
                    .build()
                CompletableFuture.completedFuture(result)
            }
            is NotFoundException -> {
                val debugInfo: MutableMap<String, Any> = HashMap()
                val graphqlError: GraphQLError = TypedGraphQLError.newNotFoundBuilder()
                    .message(handlerParameters.exception.message)
                    .debugInfo(debugInfo)
                    .path(handlerParameters.path).build()
                val result: DataFetcherExceptionHandlerResult = DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError)
                    .build()
                CompletableFuture.completedFuture(result)
            }
            is CustomException -> {
                val exp = handlerParameters.exception as CustomException
                val extensions: MutableMap<String, Any> = HashMap()
                extensions["code"] = exp.errorCode.name
                val debugInfo: MutableMap<String, Any> = HashMap()
                val customGraphqlErrBuilder = newBuilder().errorType(exp.errorType)
                val graphqlError: GraphQLError = customGraphqlErrBuilder
                    .message(handlerParameters.exception.message)
                    .extensions(extensions)
                    .debugInfo(debugInfo)
                    .path(handlerParameters.path).build()
                val result: DataFetcherExceptionHandlerResult = DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError)
                    .build()
                CompletableFuture.completedFuture(result)
            }
            else -> {
                val extensions: MutableMap<String, Any> = HashMap()
                val debugInfo: MutableMap<String, Any> = HashMap()
                val unknownError = newBuilder().errorType(ErrorType.UNKNOWN)

                val graphqlError: GraphQLError = unknownError
                    .message(handlerParameters.exception.message ?: "")
                    .extensions(extensions)
                    .debugInfo(debugInfo)
                    .path(handlerParameters.path).build()
                val result: DataFetcherExceptionHandlerResult = DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError)
                    .build()
                CompletableFuture.completedFuture(result)
            }
//            else -> {
//                super.handleException(handlerParameters)
//            }
        }
    }
}