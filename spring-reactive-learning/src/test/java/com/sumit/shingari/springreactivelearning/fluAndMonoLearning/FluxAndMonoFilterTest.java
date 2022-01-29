package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {
	
	List<String> names = Arrays.asList("adam", "ali", "Tom", "prick", "Harry");
	
	@Test
	public void filterTest() {
		
		Flux<String> flux = Flux.fromIterable(names).filter(s -> s.startsWith("a")).log();
		
		StepVerifier.create(flux)
					.expectNext("adam", "ali")
					.verifyComplete();
		
		
	}
	
	@Test
	public void filterTestLength() {
		
		Flux<String> flux = Flux.fromIterable(names).filter(s -> s.length() > 4).log();
		
		StepVerifier.create(flux)
					.expectNext("prick", "Harry")
					.verifyComplete();
		
		
	}

}
