package org.example.nmegtaskbackend.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.example.nmegtaskbackend.exception.ResourceNotFoundException;
import org.example.nmegtaskbackend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class GraphQLConfig {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLConfig.class);

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {
        return (ex, env) -> {
            logger.error("GraphQL Exception occurred: {}", ex.getClass().getSimpleName(), ex);
            GraphQLError error = null;
            
            if (ex instanceof ValidationException) {
                error = GraphqlErrorBuilder.newError()
                        .errorType(ErrorType.BAD_REQUEST)
                        .message(ex.getMessage())
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .build();
            } else if (ex instanceof ResourceNotFoundException) {
                error = GraphqlErrorBuilder.newError()
                        .errorType(ErrorType.NOT_FOUND)
                        .message(ex.getMessage())
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .build();
            } else if (ex instanceof IllegalArgumentException) {
                error = GraphqlErrorBuilder.newError()
                        .errorType(ErrorType.BAD_REQUEST)
                        .message(ex.getMessage())
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .build();
            } else if (ex instanceof RuntimeException) {
                error = GraphqlErrorBuilder.newError()
                        .errorType(ErrorType.INTERNAL_ERROR)
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .build();
            }
            
            return error != null ? Mono.just(List.of(error)) : Mono.empty();
        };
    }
}
