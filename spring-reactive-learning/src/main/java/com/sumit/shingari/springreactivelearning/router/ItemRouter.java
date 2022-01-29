package com.sumit.shingari.springreactivelearning.router;

import static com.sumit.shingari.springreactivelearning.constants.ItemConstants.ITEM_FUNC_END_POINT_V1;
import static com.sumit.shingari.springreactivelearning.constants.ItemConstants.ITEM_STREAM_FUNC_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.sumit.shingari.springreactivelearning.handler.ItemHandler;

@Configuration
public class ItemRouter {

	@Bean
	RouterFunction<ServerResponse> itemsRouter(ItemHandler handler) {
		
		return RouterFunctions
				     .route(GET(ITEM_FUNC_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON_UTF8)),
				    		 handler::getAllItems)
				     .andRoute(GET(ITEM_FUNC_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON_UTF8)),
				    		 handler::getOneItem)
				     .andRoute(POST(ITEM_FUNC_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON_UTF8)),
				    		 handler::createItem)
				     .andRoute(DELETE(ITEM_FUNC_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON_UTF8)),
				    		 handler::deleteItem)
				     .andRoute(PUT(ITEM_FUNC_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON_UTF8)),
				    		 handler::updateItem);
	}
	
	@Bean
	RouterFunction<ServerResponse> errorRouter(ItemHandler handler) {
		
		return RouterFunctions
				.route(GET("/fun/runtimeException").and(accept(MediaType.APPLICATION_JSON_UTF8)),
			    		 handler::itemEx);
	}
	
	@Bean
	RouterFunction<ServerResponse> itemStreamRouter(ItemHandler handler) {
		
		return RouterFunctions
				.route(GET(ITEM_STREAM_FUNC_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON_UTF8)),
			    		 handler::itemStream);
	}
}
