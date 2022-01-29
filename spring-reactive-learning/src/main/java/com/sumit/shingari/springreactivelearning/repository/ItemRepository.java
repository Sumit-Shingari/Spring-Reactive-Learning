package com.sumit.shingari.springreactivelearning.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.sumit.shingari.springreactivelearning.document.Item;

import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

	
	Mono<Item> findByDescription(String description);
}
