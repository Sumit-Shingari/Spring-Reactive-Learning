package com.sumit.shingari.springreactivelearning.comtroller;

import static com.sumit.shingari.springreactivelearning.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sumit.shingari.springreactivelearning.document.ItemCapped;
import com.sumit.shingari.springreactivelearning.repository.ItemCappedReactiveRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ItemStreamController {

	private final ItemCappedReactiveRepository repository;
	
	@GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<ItemCapped> getAllCappedItems() {
		
		return repository.findItemsBy();
	}
	
	
}
