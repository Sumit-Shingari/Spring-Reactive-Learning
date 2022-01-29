package com.sumit.shingari.springreactivelearning.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.sumit.shingari.springreactivelearning.document.Item;
import com.sumit.shingari.springreactivelearning.document.ItemCapped;
import com.sumit.shingari.springreactivelearning.repository.ItemCappedReactiveRepository;
import com.sumit.shingari.springreactivelearning.repository.ItemRepository;

import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

	static Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	
	@Autowired
	ItemRepository repository;
	
	@Autowired
	ItemCappedReactiveRepository cappedRepository;
	
	public Mono<ServerResponse> getAllItems(ServerRequest request) {
		
		return ServerResponse.ok()
							 .contentType(MediaType.APPLICATION_JSON)
							 .body(repository.findAll(), Item.class);					 
	}
	
	@SuppressWarnings("deprecation")
public Mono<ServerResponse> getOneItem(ServerRequest request) {
		
		String id = request.pathVariable("id");
		Mono<Item> monoItem = repository.findById(id);
		
		return monoItem.flatMap(item ->
			ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON)
			 .body(fromObject(item))			  
		)
		.switchIfEmpty(notFound);		
		
//		return ServerResponse.ok()
//				 .contentType(MediaType.APPLICATION_JSON)
//				 .body(monoItem, Item.class)
//				 .switchIfEmpty(notFound);
		
							 
	}
	
	public Mono<ServerResponse> createItem(ServerRequest request) {
		
		Mono<Item> mono = request.bodyToMono(Item.class);
		
		return mono.flatMap(item -> 
		ServerResponse.status(HttpStatus.CREATED)
					 .contentType(MediaType.APPLICATION_JSON_UTF8)
					 .body(repository.save(item), Item.class)
					 
				);
	}
	
	public Mono<ServerResponse> deleteItem(ServerRequest request)  {
		
		String id = request.pathVariable("id");
		Mono<Void> deleteById = repository.deleteById(id);
		
		return
			ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON)
			 .body(deleteById, Void.class);
	}
	
	public Mono<ServerResponse> updateItem(ServerRequest request) {
		
		String id = request.pathVariable("id");
		
		Mono<Item> updatedItem = request.bodyToMono(Item.class)
				.flatMap(item -> {
					Mono<Item> monoItem = repository.findById(id)
					.flatMap(currentItem -> {
						item.setId(currentItem.getId());
						return repository.save(item);
					});
					return monoItem;
				});
		return updatedItem.flatMap(item -> 
		                  ServerResponse.ok()
		                  .contentType(MediaType.APPLICATION_JSON_UTF8)
		                  .body(fromObject(item)))
						  .switchIfEmpty(notFound);
	}
	
	public Mono<ServerResponse> itemEx(ServerRequest request) {
		
		throw new RuntimeException("Runtime Exception is thrown!");
	}
	
	public Mono<ServerResponse> itemStream(ServerRequest request) {
		
		return ServerResponse.ok()
							 .contentType(MediaType.APPLICATION_STREAM_JSON)
							 .body(cappedRepository.findItemsBy(), ItemCapped.class);
	}
	
	
}
