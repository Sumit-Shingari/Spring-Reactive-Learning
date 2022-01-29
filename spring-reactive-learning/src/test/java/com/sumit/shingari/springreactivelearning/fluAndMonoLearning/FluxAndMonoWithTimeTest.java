package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoWithTimeTest {

	@Test
	public void infiniteFlux() {
		Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(200)).log();
		
		fluxInterval.subscribe(elem -> System.out.println("Value is : " + elem));
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void infiniteSequenceTest() {
		
		Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
				.take(3)
				.log();
		
		StepVerifier.create(finiteFlux)
					.expectSubscription()
					.expectNext(0L, 1L, 2L)
					.verifyComplete();
	}
	
	@Test
	public void infiniteSequenceMap() {
		
		Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
				.map(l -> l.intValue())
				.take(3)
				.log();
		
		StepVerifier.create(finiteFlux)
					.expectSubscription()
					.expectNext(0, 1, 2)
					.verifyComplete();
	}
	
	@Test
	public void infiniteSequenceMap_withDelay() {
		
		Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
				.delayElements(Duration.ofSeconds(1))
				.map(l -> l.intValue())
				.take(3)
				.log();
		
		StepVerifier.create(finiteFlux)
					.expectSubscription()
					.expectNext(0, 1, 2)
					.verifyComplete();
	}
}
