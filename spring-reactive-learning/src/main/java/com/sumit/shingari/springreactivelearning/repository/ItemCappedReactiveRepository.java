package com.sumit.shingari.springreactivelearning.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.sumit.shingari.springreactivelearning.document.ItemCapped;

import reactor.core.publisher.Flux;

public interface ItemCappedReactiveRepository extends ReactiveMongoRepository<ItemCapped, String> {

	@Tailable
	Flux<ItemCapped> findItemsBy();
}
