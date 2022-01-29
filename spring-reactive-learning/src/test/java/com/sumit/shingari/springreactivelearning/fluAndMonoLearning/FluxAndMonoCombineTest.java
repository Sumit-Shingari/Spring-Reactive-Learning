package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoCombineTest {

	@Test
	public void combineUsingMerge() {
		
		Flux<String> flux1 = Flux.just("A", "B", "C");
		Flux<String> flux2 = Flux.just("D", "E", "F");
		
		Flux<String> fluxMerge = Flux.merge(flux1, flux2).log();
		
		StepVerifier.create(fluxMerge)
					.expectSubscription()
					.expectNext("A", "B", "C", "D", "E", "F")
					.verifyComplete();
	}
	
	@Test
	public void combineUsingMerge_withDelay() {
		
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> fluxMerge = Flux.merge(flux1, flux2).log();
		
		StepVerifier.create(fluxMerge)
					.expectSubscription()
					//.expectNext("A", "B", "C", "D", "E", "F")
					.expectNextCount(6)
					.verifyComplete();
	}
	
	@Test
	public void combineUsingConcat() {
		
		Flux<String> flux1 = Flux.just("A", "B", "C");
		Flux<String> flux2 = Flux.just("D", "E", "F");
		
		Flux<String> fluxMerge = Flux.concat(flux1, flux2).log();
		
		StepVerifier.create(fluxMerge)
					.expectSubscription()
					.expectNext("A", "B", "C", "D", "E", "F")
					.verifyComplete();
	}
	
	@Test
	public void combineUsingConcat_withDelay() {
		
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> fluxMerge = Flux.concat(flux1, flux2).log();
		
		StepVerifier.create(fluxMerge)
					.expectSubscription()
					.expectNext("A", "B", "C", "D", "E", "F")
					.verifyComplete();
	}
	
	@Test
	public void combineUsingZip() {
		
		Flux<String> flux1 = Flux.just("A", "B", "C");
		Flux<String> flux2 = Flux.just("D", "E", "F");
		
		Flux<String> fluxMerge = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2)).log();
		
		StepVerifier.create(fluxMerge)
					.expectSubscription()
					.expectNext("AD", "BE", "CF")
					.verifyComplete();
	}
}
