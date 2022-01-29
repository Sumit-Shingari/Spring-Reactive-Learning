package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {

	List<String> names = Arrays.asList("Tom", "prick", "Harry");
	
	@Test
	public void fluxUsingIterable() {
		
		Flux<String> fluxNames = Flux.fromIterable(names);
		
		StepVerifier.create(fluxNames.log())
					.expectNext("Tom", "prick", "Harry")
					.verifyComplete();
	}
	
	@Test
	public void fluxUsingArray() {
		
		String[] names  = new String[] {"Tom", "prick", "Harry"};
		
		Flux<String> fluxNames = Flux.fromArray(names).log();
		
		StepVerifier.create(fluxNames)
					.expectNext("Tom", "prick", "Harry")
					.verifyComplete();
	}
	
	@Test
	public void fluxUsingStream() {
		
		Flux<String> fluxNames = Flux.fromStream(names.stream()).log();
		
		StepVerifier.create(fluxNames)
		.expectNext("Tom", "prick", "Harry")
		.verifyComplete();
	}
	
	@Test
	public void MonoUsingJustOrEmpty() {
		
		Mono<Object> mono = Mono.justOrEmpty(null).log();
		
		StepVerifier.create(mono)
					.verifyComplete();
	}
	
	@Test
	public void monoUsingSupplier() {
		
		Supplier<String> stringSupplier = () -> "Adam";
		
		Mono<String> mono = Mono.fromSupplier(stringSupplier).log();
		
		StepVerifier.create(mono)
					.expectNext("Adam")
					.verifyComplete();
	}
	
	@Test
	public void fluxUsingRange() {
		
		Flux<Integer> flux = Flux.range(1, 5).log();
		
		StepVerifier.create(flux)
					.expectNext(1, 2, 3, 4,5)
					.verifyComplete();
		
	}
}
