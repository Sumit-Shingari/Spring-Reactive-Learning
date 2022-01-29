package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class VirtualTimeTest {

	@Test
	public void testingWithoutVirtualTime() {
		
		Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
				.take(3).log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.expectNext(0L, 1L, 2L)
					.verifyComplete();
	}
	
	@Test
	public void testingWithVirtualTime() {
		
		VirtualTimeScheduler.getOrSet();
		
		Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
				.take(3).log();
		
		StepVerifier.withVirtualTime(() -> flux)
					.expectSubscription()
					.thenAwait(Duration.ofSeconds(3))
					.expectNext(0L, 1L, 2L)
					.verifyComplete();
					
	}
}
