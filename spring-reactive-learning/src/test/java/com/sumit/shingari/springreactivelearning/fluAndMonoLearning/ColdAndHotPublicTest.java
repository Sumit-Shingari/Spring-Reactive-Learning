package com.sumit.shingari.springreactivelearning.fluAndMonoLearning;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdAndHotPublicTest {

	@Test
	public void coldPublisherTest() throws InterruptedException {
		
		Flux<String> flux = Flux.just("A", "B", "C", "D", "E")
				.delayElements(Duration.ofSeconds(1)).log();
		
		flux.subscribe(s -> System.out.println("Subscription 1 "+ s));
		
		Thread.sleep(2000);
		
        flux.subscribe(s -> System.out.println("Subscription 2 "+ s));
		
		Thread.sleep(4000);
	}
	
	@Test
	public void hotPublisherTest() throws InterruptedException {
		
		Flux<String> flux = Flux.just("A", "B", "C", "D", "E")
				.delayElements(Duration.ofSeconds(1)).log();
		
		ConnectableFlux<String> connectableFlux = flux.publish();
		connectableFlux.connect();
		
		connectableFlux.subscribe(s -> System.out.println("Subscription 1 "+ s));
		
		Thread.sleep(3000);
		
		connectableFlux.subscribe(s -> System.out.println("Subscription 2 "+ s)); // does not emit values from start
		
		Thread.sleep(3000);
		
	}
}
