package com.sumit.shingari.springreactivelearning.handler;

import static com.sumit.shingari.springreactivelearning.constants.ItemConstants.ITEM_FUNC_END_POINT_V1;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.sumit.shingari.springreactivelearning.document.Item;
import com.sumit.shingari.springreactivelearning.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	ItemRepository repository;
	
	private List<Item> data() {
		
		return Arrays.asList(new Item(null, "First prod", 400.00)
				,new Item(null, "Second prod", 800.00)
				,new Item(null, "Third prod", 1200.00)
				,new Item(null, "Forth prod", 160.00)
				,new Item("ABC", "Fifth prod", 2000.00));
	}
	
	@BeforeEach
	public void setup() {
		
		repository.deleteAll()
				  .thenMany(Flux.fromIterable(data()))
				  .flatMap(repository::save)
				  .doOnNext(item -> System.out.println("Item is :" + item))
				  .blockLast();
	}
	
	@Test
	public void getAllItems() {
		
		webTestClient.get().uri(ITEM_FUNC_END_POINT_V1)
					 .exchange()
					 .expectStatus().isOk()
					 .expectHeader().contentType(MediaType.APPLICATION_JSON)
					 .expectBodyList(Item.class)
					 .hasSize(5);
	}
	
	@Test
	public void getAllItems_approach2() {
		
		webTestClient.get().uri(ITEM_FUNC_END_POINT_V1)
					 .exchange()
					 .expectStatus().isOk()
					 .expectHeader().contentType(MediaType.APPLICATION_JSON)
					 .expectBodyList(Item.class)
					 .hasSize(5)
					 .consumeWith(
							 response -> {List<Item> itemList = response.getResponseBody();
							 itemList.forEach(item -> assertTrue(item.getId() != null));
							 });
	}
	
	@Test
	public void getAllItems_approach3() {
		
		Flux<Item> responseBody = webTestClient.get().uri(ITEM_FUNC_END_POINT_V1)
					 .exchange()
					 .expectStatus().isOk()
					 .expectHeader().contentType(MediaType.APPLICATION_JSON)
					 .returnResult(Item.class)
					 .getResponseBody();
		
		StepVerifier.create(responseBody.log("Value from network :"))
					.expectNextCount(5)
					.verifyComplete();
	}
	
	@Test
	public void getItemById() {
		
		webTestClient.get().uri(ITEM_FUNC_END_POINT_V1.concat("/{id}"), "ABC")
							.exchange()
							.expectStatus().isOk()
							.expectBody()
							.jsonPath("$.price", 2000.00);
	}
	
	@Test
	public void getItemById_approach2() {
		
		webTestClient.get().uri(ITEM_FUNC_END_POINT_V1.concat("/{id}"), "ABCD")
							.exchange()
							.expectStatus().isNotFound();
	}
	
	@Test
	public void createItem() {
		
		Item item = new Item(null, "Iphone Tablet", 999.99);
		
		webTestClient.post().uri(ITEM_FUNC_END_POINT_V1)
							.contentType(MediaType.APPLICATION_JSON_UTF8)
							.body(Mono.just(item), Item.class)
							.exchange()
							.expectStatus().isCreated()
							.expectBody()
							.jsonPath("$.id").isNotEmpty()
							.jsonPath("$.description").isEqualTo("Iphone Tablet")
							.jsonPath("$.price").isEqualTo(999.99);
	}
	
	@Test
	public void updateItem() {
		double newPrice = 129.99;
		Item item = new Item(null, "Fifth prod", newPrice);
		
		webTestClient.put().uri(ITEM_FUNC_END_POINT_V1.concat("/{id}"), "ABC")
							.contentType(MediaType.APPLICATION_JSON_UTF8)
							.accept(MediaType.APPLICATION_JSON_UTF8)
							.body(Mono.just(item), Item.class)
							.exchange()
							.expectStatus().isOk()
							.expectBody()
							.jsonPath("$.id").isEqualTo("ABC")
							.jsonPath("$.price").isEqualTo(newPrice);
							
		
	}
	
	@Test
	public void updateItem_NotFound() {
		double newPrice = 129.99;
		Item item = new Item(null, "Fifth prod", newPrice);
		
		webTestClient.put().uri(ITEM_FUNC_END_POINT_V1.concat("/{id}"), "DEF")
							.contentType(MediaType.APPLICATION_JSON_UTF8)
							.accept(MediaType.APPLICATION_JSON_UTF8)
							.body(Mono.just(item), Item.class)
							.exchange()
							.expectStatus().isNotFound();					
		
	}
	
	@Test
	public void runtimeException() {
		
		webTestClient.get().uri("/fun/runtimeException")
						  .exchange()
						  .expectStatus().is5xxServerError()
						  .expectBody()
						  .jsonPath("$.message", "Runtime Exception Occured for /fun/runtimeException");
	}
}
