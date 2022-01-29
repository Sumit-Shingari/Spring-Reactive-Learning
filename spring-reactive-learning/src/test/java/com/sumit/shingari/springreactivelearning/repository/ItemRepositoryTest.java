package com.sumit.shingari.springreactivelearning.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.sumit.shingari.springreactivelearning.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemRepositoryTest {

	@Autowired
	ItemRepository repository;
	
	List<Item> itemList = Arrays.asList(new Item(null, "First prod", 400.00)
										,new Item(null, "Second prod", 800.00)
										,new Item(null, "Third prod", 1200.00)
										,new Item(null, "Forth prod", 160.00)
										,new Item("ABC", "Fifth prod", 2000.00));
	
	@BeforeEach
	public void setup() {
		repository.deleteAll()
					.thenMany(Flux.fromIterable(itemList))
					.flatMap(repository::save)
					.doOnNext(item -> System.out.println("Inserted Item is :" + item))
					.blockLast();
		
	}
	
	@Test
	public void getAllItems() {
		
		StepVerifier.create(repository.findAll())
					.expectSubscription()
					.expectNextCount(5)
					.verifyComplete();
	}
	
	@Test
	public void getItemById() {
		
		StepVerifier.create(repository.findById("ABC"))
					.expectSubscription()
					.expectNextMatches(item -> item.getDescription().equals("Fifth prod"))
					.verifyComplete();
	}
	
	@Test
	public void getItemByDescription() {
		
		StepVerifier.create(repository.findByDescription("Fifth prod"))
					.expectSubscription()
					.expectNextMatches(item -> item.getId().equals("ABC"))
					.verifyComplete();
	}
	
	@Test
	public void saveItem() {
		
		Item item = new Item(null, "New Item", 5000.00);
		Mono<Item> mono = repository.save(item);
		
		StepVerifier.create(mono)
					.expectSubscription()
					.expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("New Item"))
					.verifyComplete();
									
	}
	
	@Test
	public void updateItem() {
		
		Double newPrice = 555.00;
		Mono<Item> updateItem = repository.findByDescription("Fifth prod")
										.map(item -> { item.setPrice(newPrice);
														return item;
										})
										.flatMap(item -> repository.save(item));
		
		StepVerifier.create(updateItem)
					.expectSubscription()
					.expectNextMatches(item -> item.getPrice().equals(555.00))
					.verifyComplete();
	}
	
	@Test
	public void deleteItemById() {
		
		Mono<Void> mono = repository.findById("ABC")
				  .map(Item::getId)
				  .flatMap(id -> repository.deleteById(id));
		
		StepVerifier.create(mono)
					.expectSubscription()
					.verifyComplete();
		
		StepVerifier.create(repository.findAll())
					.expectSubscription()
					.expectNextCount(4)
					.verifyComplete();
	}
	
	@Test
	public void deleteItem() {
		
		Mono<Void> mono = repository.findByDescription("Fifth prod")
				  .flatMap(item -> repository.delete(item));
		
		StepVerifier.create(mono)
					.expectSubscription()
					.verifyComplete();
		
		StepVerifier.create(repository.findAll())
					.expectSubscription()
					.expectNextCount(4)
					.verifyComplete();
	}
}
