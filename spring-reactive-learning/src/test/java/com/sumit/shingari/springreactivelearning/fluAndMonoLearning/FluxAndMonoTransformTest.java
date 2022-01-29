package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

	List<String> names = Arrays.asList("adam", "aman", "Tom", "prick", "Harry");
	
	@Test
	public void transformUsingMap() {
		
		Flux<String> flux = Flux.fromIterable(names).map(s -> s.toUpperCase()).log();
		
		StepVerifier.create(flux)
					.expectNext("ADAM", "AMAN", "TOM", "PRICK", "HARRY")
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMap_length() {
		
		Flux<Integer> flux = Flux.fromIterable(names).map(s -> s.length()).log();
		
		StepVerifier.create(flux)
					.expectNext(4, 4, 3, 5, 5)
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMap_lengthRepeat() {
		
		Flux<Integer> flux = Flux.fromIterable(names)
				.map(s -> s.length())
				.repeat(1)
				.log();
		
		StepVerifier.create(flux)
					.expectNext(4, 4, 3, 5, 5, 4, 4, 3, 5, 5)
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMap_filter() {
		
		Flux<String> flux = Flux.fromIterable(names)
				.filter(s -> s.length() > 4)
				.map(s -> s.toUpperCase())
				.log();
		
		StepVerifier.create(flux)
					.expectNext("PRICK", "HARRY")
					.verifyComplete();
	}
	
	@Test
	public void transformUsingFlatMap() {
		
	   Flux<String> characters =  Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
				.flatMap(s -> Flux.fromIterable(convertToFlux(s)))
				.log();
	    
	    StepVerifier.create(characters)
	    			.expectNext("A", "New Value", "B", "New Value", "C", "New Value", "D", "New Value"
	    					, "E", "New Value", "F", "New Value")
	    			.verifyComplete();
	}

	@Test
	public void transformUsingFlatMap_usingParallel() {
		
	   Flux<String> characters =  Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
			    .window(2) // Flux<Flux<String>> -> (A,B) , (C, D), (E, F)
				.flatMap(s -> s.map(this::convertToFlux).subscribeOn(parallel()))
				.flatMap(s -> Flux.fromIterable(s))
				.log();
	    
	    StepVerifier.create(characters)
	    			.expectNextCount(12)
	    			.verifyComplete();
	}
	
	@Test
	public void transformUsingFlatMap_usingParallelMaintainOrder() {
		
	   Flux<String> characters =  Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
			    .window(2) // Flux<Flux<String>> -> (A,B) , (C, D), (E, F)
				.concatMap(s -> s.map(this::convertToFlux).subscribeOn(parallel()))
				.flatMap(s -> Flux.fromIterable(s))
				.log();
	    
	    StepVerifier.create(characters)
	    			.expectNextCount(12)
	    			.verifyComplete();
	}
	
	@Test
	public void transformUsingFlatMap_usingParallelMaintainOrder2() {
		
	   Flux<String> characters =  Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
			    .window(2) // Flux<Flux<String>> -> (A,B) , (C, D), (E, F)
				.flatMapSequential(s -> s.map(this::convertToFlux).subscribeOn(parallel()))
				.flatMap(s -> Flux.fromIterable(s))
				.log();
	    
	    StepVerifier.create(characters)
	    			.expectNextCount(12)
	    			.verifyComplete();
	}
	
	private List<String> convertToFlux(String s) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.asList(s, "New Value");
	}
}
