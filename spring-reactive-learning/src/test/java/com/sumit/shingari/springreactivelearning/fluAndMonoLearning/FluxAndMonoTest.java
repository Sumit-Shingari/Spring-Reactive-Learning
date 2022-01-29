package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
	
	@Test
	public void fluxTest() {
		
		Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
//				.concatWith(Flux.error(new RuntimeException("Error thrown explicitly")))
				.concatWith(Flux.just("After Error"))
				.log();
		stringFlux
				.subscribe(System.out::println
						, e -> System.err.println(e.getMessage())
						, () -> System.out.println("Completed"));
	}
	
	@Test
	public void fluxTestElement_WithoutError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
				.log();
		StepVerifier.create(stringFlux)
		.expectNext("Spring")
		.expectNext("Spring boot")
		.expectNext("Reactive Spring")
		.verifyComplete();
	}
	
	@Test
	public void fluxTestElement_WithError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Error thrown explicitly")))
				.log();
		StepVerifier.create(stringFlux)
		.expectNext("Spring")
		.expectNext("Spring boot")
		.expectNext("Reactive Spring")
//		.expectError(RuntimeException.class)
		.expectErrorMessage("Error thrown explicitly")
		.verify();
	}

	@Test
	public void fluxTestElementCount_WithError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Error thrown explicitly")))
				.log();
		StepVerifier.create(stringFlux)
		.expectNextCount(3)
//		.expectError(RuntimeException.class)
		.expectErrorMessage("Error thrown explicitly")
		.verify();
	}
	
	@Test
	public void fluxTestElement_WithError1() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Error thrown explicitly")))
				.log();
		StepVerifier.create(stringFlux)
		.expectNext("Spring", "Spring boot", "Reactive Spring")
//		.expectError(RuntimeException.class)
		.expectErrorMessage("Error thrown explicitly")
		.verify();
	}
	
	@Test
	public void monoTest() {
		
		Mono<String> stringMono = Mono.just("Spring")
				.log();
		StepVerifier.create(stringMono)
					.expectNext("Spring")
					.verifyComplete();
	}
	
	@Test
	public void monoTest_WithError() {
		
		StepVerifier.create(Mono.error(new RuntimeException("Exception Generated!")))
					.expectError(RuntimeException.class)
					.verify();
	}
}
