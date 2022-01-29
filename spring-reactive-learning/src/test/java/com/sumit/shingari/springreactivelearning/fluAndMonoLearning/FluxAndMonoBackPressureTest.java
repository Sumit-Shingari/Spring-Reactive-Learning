package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

	@Test
	public void fluxBackPressure() {
		
		Flux<Integer> flux = Flux.range(1, 10).log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.thenRequest(1)
					.expectNext(1)
					.thenRequest(1)
					.expectNext(2)
					.thenCancel()
					.verify();
	}
	
	@Test
	public void backPressure() {
		
		Flux<Integer> flux = Flux.range(1, 10).log();
		
		flux.subscribe(elem -> System.out.println("Element is : " + elem),
				(e) -> System.err.println("Exception is " + e)
				, () -> System.out.println("Completed")
				, subscription -> subscription.request(2));
	}
	
	
	@Test
	public void backPressure_cancel() {
		
		Flux<Integer> flux = Flux.range(1, 10).log();
		
		flux.subscribe(elem -> System.out.println("Element is : " + elem),
				(e) -> System.err.println("Exception is " + e)
				, () -> System.out.println("Completed")
				, subscription -> subscription.cancel());
	}
	
	@Test
	public void customizedBackPressure() {
		
		Flux<Integer> flux = Flux.range(1, 10).log();
		
		flux.subscribe(new BaseSubscriber<Integer>() {
			
			@Override
			protected void hookOnNext(Integer value) {
				request(1);
				System.out.println("Value is : " + value);
				if (value == 4) {
					cancel();
				}
			}
		});
	}
	
	
}
