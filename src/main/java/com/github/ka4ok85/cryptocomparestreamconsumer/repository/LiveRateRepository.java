package com.github.ka4ok85.cryptocomparestreamconsumer.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;

import reactor.core.publisher.Flux;

@Document(collection = "live_rates")
public interface LiveRateRepository extends ReactiveMongoRepository<LiveRate, ObjectId> {

	public Flux<LiveRate> findByExchangeName(final String firstName);
}
