package com.github.ka4ok85.cryptocomparestreamconsumer.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;

import reactor.core.publisher.Flux;

public interface LiveRateReactiveRepository extends ReactiveMongoRepository<LiveRate, ObjectId> {

	@Tailable
	public Flux<LiveRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

	@Tailable
	public Flux<LiveRate> findByFromCurrencyAndToCurrencyAndLastUpdateGreaterThan(String fromCurrency,
			String toCurrency, long lastUpdate);

	public Flux<LiveRate> findFirst5ByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

	public Flux<LiveRate> findLast5ByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

	public Flux<LiveRate> findWhateverByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}
