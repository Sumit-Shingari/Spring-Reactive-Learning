package com.sumit.shingari.springreactivelearning.comtroller.v1;

import static com.sumit.shingari.springreactivelearning.constants.ItemConstants.ITEM_END_POINT_V1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sumit.shingari.springreactivelearning.document.Item;
import com.sumit.shingari.springreactivelearning.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemController {
	
	private final ItemRepository repository;
	
	@GetMapping(ITEM_END_POINT_V1)
	public Flux<Item> getAllItems() {
		
		return repository.findAll();
	}
	
	@GetMapping(ITEM_END_POINT_V1+"{id}")
	public Mono<ResponseEntity<Item>> getItemById(@PathVariable(value = "id") String id) {
		
		return repository.findById(id)
							.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
							.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping(ITEM_END_POINT_V1)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> createItem(@RequestBody Item item) {
		
		return repository.save(item);
	}
	
	@DeleteMapping(ITEM_END_POINT_V1+"{id}")
	public  Mono<Void> deleteItemById(@PathVariable(value = "id") String id) {
		
		return repository.deleteById(id);
	}
	
	@GetMapping(ITEM_END_POINT_V1+ "/runtimeException")
	public Flux<Item> runtimeException() {
		
		return repository.findAll()
						 .concatWith(Mono.error(new RuntimeException("Runtime Exception is thrown!")));
	}
 	
	@PutMapping(ITEM_END_POINT_V1+"{id}")
	public Mono<ResponseEntity<Item>> updateItem(@PathVariable("id") String id, @RequestBody Item item) {
		
		return repository.findById(id)
				  .flatMap(currentItem -> {
					  item.setId(id);
					 return repository.save(item);
				  })
				  .map(resp -> new ResponseEntity<>(resp, HttpStatus.OK))
				  .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
