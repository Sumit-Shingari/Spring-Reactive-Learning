package com.sumit.shingari.springreactivelearning.controller.v1;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.sumit.shingari.springreactivelearning.constants.ItemConstants;
import com.sumit.shingari.springreactivelearning.document.ItemCapped;
import com.sumit.shingari.springreactivelearning.repository.ItemCappedReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class ItemStreamControllerTest {

	@Autowired
	ItemCappedReactiveRepository repository;
	
	@Autowired
	ReactiveMongoOperations mongoOperations;
	
	@Autowired
	WebTestClient webTestClient;
	
	@BeforeEach
	public void setup() {
		mongoOperations.dropCollection(ItemCapped.class)
		.then(mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped())).subscribe();
	  
		Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
			    .map(i ->  new ItemCapped(null, "Item No " + i, 100.00 + i)).take(5);
			
			repository.insert(itemCappedFlux)
					  .doOnNext(itemCapped -> log.info("Item Capped is : " + itemCapped))
					  .blockLast();
	}
	
	@Test
	public void testStreamAllItem() {
		
		Flux<ItemCapped> take = webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
							.exchange()
							.expectStatus().isOk()
							.returnResult(ItemCapped.class)
							.getResponseBody()
							.take(5);
		
		StepVerifier.create(take)
					.expectNextCount(5)
					.thenCancel()
					.verify();
	}
}
