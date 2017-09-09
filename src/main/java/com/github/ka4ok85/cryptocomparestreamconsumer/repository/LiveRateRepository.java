package com.github.ka4ok85.cryptocomparestreamconsumer.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;

import reactor.core.publisher.Flux;

public interface LiveRateRepository extends ReactiveMongoRepository<LiveRate, ObjectId> {

	@Tailable
	public Flux<LiveRate> findByFromCurrency(String fromCurrency);
}
