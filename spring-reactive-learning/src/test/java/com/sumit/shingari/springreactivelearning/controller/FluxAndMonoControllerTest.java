package com.sumit.shingari.springreactivelearning.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
public class FluxAndMonoControllerTest {

	@Autowired
	WebTestClient webTestClient;
	
	@Test
	public void flux_approach1() {
		
		Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
							.accept(MediaType.APPLICATION_JSON_UTF8)
							.exchange()
							.expectStatus().isOk()
							.returnResult(Integer.class)
							.getResponseBody();
		
			StepVerifier.create(integerFlux)
						.expectSubscription()
						.expectNext(1)
						.expectNext(2)
						.expectNext(3)
						.expectNext(4)
						.verifyComplete();
	}
	
	@Test
	public void flux_approach2() {
		
		 webTestClient.get().uri("/flux")
							.accept(MediaType.APPLICATION_JSON_UTF8)
							.exchange()
							.expectStatus().isOk()
							.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
							.expectBodyList(Integer.class)
							.hasSize(4);
		
	}
	
	@Test
	public void flux_approach3() {
		
		List<Integer> expected = Arrays.asList(1, 2, 3, 4);
		
		 EntityExchangeResult<List<Integer>> returnResult = webTestClient.get().uri("/flux")
							.accept(MediaType.APPLICATION_JSON_UTF8)
							.exchange()
							.expectStatus().isOk()
							.expectBodyList(Integer.class)
							.returnResult();
							
		Assert.assertEquals(expected, returnResult.getResponseBody());
	}
	
//	@Test
//	public void flux_approach4() {
//		
//		List<Integer> expected = Arrays.asList(1, 2, 3, 4);
//		
//		 EntityExchangeResult<List<Integer>> returnResult = webTestClient.get().uri("/flux")
//							.accept(MediaType.APPLICATION_JSON_UTF8)
//							.exchange()
//							.expectStatus().isOk()
//							.expectBodyList(Integer.class)
//							.consumeWith(response -> 
//							Assert.assertEquals(expected, response.getResponseBody()));
//	}
	
	@Test
	public void fluxStream() {
		
		Flux<Long> longFlux = webTestClient.get().uri("/fluxstream")
							.accept(MediaType.APPLICATION_STREAM_JSON)
							.exchange()
							.expectStatus().isOk()
							.returnResult(Long.class)
							.getResponseBody();
		
			StepVerifier.create(longFlux)
						.expectNext(0L)
						.expectNext(1L)
						.expectNext(2L)
						.thenCancel()
						.verify();
	}
	
	@Test
	public void mono() {
		
		Integer expect = new Integer(1);
		
		webTestClient.get()
					.uri("/mono")
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.exchange()
					.expectStatus().isOk()
					.expectBody(Integer.class)
					.consumeWith(response -> Assert.assertEquals(expect, response.getResponseBody()));
	}
}
