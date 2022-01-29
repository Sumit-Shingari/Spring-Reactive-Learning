package com.sumit.shingari.itemclient.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.sumit.shingari.itemclient.domain.Item;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {

	WebClient webClient = WebClient.create("http://localhost:8080");
	
	@GetMapping("/client/retrieve")
	public Flux<Item> getAllItemsRetrieve() {
		
	return webClient.get().uri("/v1/items")
				   .retrieve()
				   .bodyToFlux(Item.class)
				   .log("Items in client project!");
	}
	
	@GetMapping("/client/exchange")
	public Flux<Item> getAllItemsExchange() {
		
	return webClient.get().uri("/v1/items")
				   .exchange()
				   .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
				   .log("Items in client project!");
	}
	
	@GetMapping("/client/retrieve/singleItem")
	public Mono<Item> getSingleItemsRetrieve() {
		
	return webClient.get().uri("/v1/items{id}", "ABC")
				   .retrieve()
				   .bodyToMono(Item.class)
				   .log("Items in client project for single item!");
	}
	
	@GetMapping("/client/exchange/singleItem")
	public Mono<Item> getSingleItemsExchange() {
		
	return webClient.get().uri("/v1/items{id}", "ABC")
				   .exchange()
				   .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
				   .log("Items in client project for single item!");
	}
	
	@PostMapping("/client/createItem")
	public Mono<Item> createItem(@RequestBody Item item) {
		
		Mono<Item> monoItem = Mono.just(item);
		return webClient.post().uri("/v1/items")
						.contentType(MediaType.APPLICATION_JSON)
						.body(monoItem, Item.class)
						.retrieve()
						.bodyToMono(Item.class)
						.log("Created Item");
						
	}
	
	@PutMapping("/client/updateItem/{id}")
	public Mono<Item> updateItem(@PathVariable(value = "id") String id, @RequestBody Item item) {
		
		Mono<Item> monoItem = Mono.just(item);
		return webClient.put().uri("/v1/items{id}", id)
						.body(monoItem, Item.class)
						.retrieve()
						.bodyToMono(Item.class)
						.log("Updated Item");
						
	}
	
	@GetMapping("/client/retrieve/error")
	public Flux<Item> retrieveError() {

		return webClient.get().uri("/v1/items/runtimeException").retrieve()
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> {
					Mono<String> errorMono = clientResponse.bodyToMono(String.class);
					return errorMono.flatMap(errorMessage -> {
						log.error("The error message is " + errorMessage);
						throw new RuntimeException(errorMessage);
					});
				}).bodyToFlux(Item.class);

	}
	
	@GetMapping("/client/exchange/error")
	public Flux<Item> exchangeError() {

		return webClient.get().uri("/v1/items/runtimeException")
				.exchange()
				.flatMapMany(clientRes -> {
					if (clientRes.statusCode().is5xxServerError()) {
						return clientRes.bodyToMono(String.class)
										.flatMap(errorMsg -> {
											log.error("The error message in exchangeError : " + errorMsg);
											throw new RuntimeException(errorMsg);
										});
					} else {
						return clientRes.bodyToFlux(Item.class);
					}
				});
				

	}
	
	@DeleteMapping("/client/deleteItem/{id}")
	public Mono<Void> getSingleItemsExchange(@PathVariable String id) {
		
	return webClient.delete().uri("/v1/items{id}", id)
				   			.retrieve()
				   			.bodyToMono(Void.class)
				   			.log("Deleted Item!");
	}
}
