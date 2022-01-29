package com.sumit.shingari.springreactivelearning.initialize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;

import com.sumit.shingari.springreactivelearning.document.Item;
import com.sumit.shingari.springreactivelearning.document.ItemCapped;
import com.sumit.shingari.springreactivelearning.repository.ItemCappedReactiveRepository;
import com.sumit.shingari.springreactivelearning.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class ItemDataIntializer implements CommandLineRunner {

	private final ItemRepository repository;
	
	private final ItemCappedReactiveRepository cappedRepo;
	
	private final ReactiveMongoOperations mongoOperations;
	
	@Override
	public void run(String... args) throws Exception {
		
		initialDataSetup();
		createCappedCollection();
		dataSetupForCappedCollection();
	}
	
	private void createCappedCollection() {
		mongoOperations.dropCollection(ItemCapped.class)
		.then(mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped())).subscribe();
	}

	private List<Item> data() {
	
		return Arrays.asList(new Item(null, "First prod", 400.00)
				,new Item(null, "Second prod", 800.00)
				,new Item(null, "Third prod", 1200.00)
				,new Item(null, "Forth prod", 160.00)
				,new Item("ABC", "Fifth prod", 2000.00));
	}

	private void dataSetupForCappedCollection() {
		
		Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
		    .map(i ->  new ItemCapped(null, "Item No " + i, 100.00 + i));
		
		cappedRepo.insert(itemCappedFlux)
				  .subscribe(itemCapped -> log.info("Item Capped is : " + itemCapped));
		
	}
	
	private void initialDataSetup() {
		repository.deleteAll()
				  .thenMany(Flux.fromIterable(data()))
				  .flatMap(repository::save)
				   .thenMany(repository.findAll())
				   .subscribe(item -> System.out.println("Item is :" + item));
		
		
		
	}

	
}
