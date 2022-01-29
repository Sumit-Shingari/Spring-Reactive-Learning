package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.time.Duration;

import javax.management.RuntimeErrorException;

import org.junit.jupiter.api.Test;

import com.sumit.shingari.springreactivelearning.fluAndMonoLearning.exception.CustomException;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

public class MonoAndFluxErrorTest {

	@Test
	public void fluxErrorHandling() {
		
		Flux<String> flux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception Occured!")))
				.concatWith(Flux.just("D"))
				.onErrorResume(e -> {
					System.out.println("Exception occured : " + e);
					return Flux.just("default");
				})
				.log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.expectNext("A", "B", "C")
//					.expectError(RuntimeException.class)
//					.verify();
					.expectNext("default")
					.verifyComplete();
	}
	
	@Test
	public void fluxErrorReturn() {
		
		Flux<String> flux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception Occured!")))
				.concatWith(Flux.just("D"))
				.onErrorReturn("default")
				.log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.expectNext("A", "B", "C")
//					.expectError(RuntimeException.class)
//					.verify();
					.expectNext("default")
					.verifyComplete();
	}
	
	@Test
	public void fluxErrorMap() {
		
		Flux<String> flux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception Occured!")))
				.concatWith(Flux.just("D"))
				.onErrorMap(e -> new CustomException(e))
				.log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.expectNext("A", "B", "C")
  					.expectError(CustomException.class)
					.verify();
	}
	
	@Test
	public void fluxErrorMap_withError() {
		
		Flux<String> flux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception Occured!")))
				.concatWith(Flux.just("D"))
				.onErrorMap(e -> new CustomException(e))
				.retry(2)
				.log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
  					.expectError(CustomException.class)
					.verify();
	}
	
	@Test
	public void fluxErrorMap_withErrorBackOff() {
		
		Flux<String> flux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception Occured!")))
				.concatWith(Flux.just("D"))
				.onErrorMap(e -> new CustomException(e))
				.retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(5)))
				.log();
		
		StepVerifier.create(flux)
					.expectSubscription()
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
  					.expectError(IllegalStateException.class)
					.verify();
	}
}

