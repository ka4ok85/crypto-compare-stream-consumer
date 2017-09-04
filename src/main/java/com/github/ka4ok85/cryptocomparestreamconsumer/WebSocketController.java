package com.github.ka4ok85.cryptocomparestreamconsumer;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;
import com.github.ka4ok85.cryptocomparestreamconsumer.repository.LiveRateRepository;
import com.github.ka4ok85.cryptocomparestreamconsumer.service.CryptocompareUtils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class WebSocketController {

	@Autowired
	private LiveRateRepository liveRateRepository;
	
	@Autowired
	private CryptocompareUtils cryptocompareUtils;

	@GetMapping("/websocket")
	public void websocket() throws URISyntaxException, JSONException {
		Socket socket = IO.socket("https://streamer.cryptocompare.com/");

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray jarr = new JSONArray();
				// jarr.put("5~CCCAGG~ETH~USD"); // aggregate
				jarr.put("2~Poloniex~BTC~USD"); // single Poloniex
				JSONObject obj = new JSONObject();
				try {
					obj.put("subs", jarr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				socket.emit("SubAdd", obj);
			}
		}).on("m", new Emitter.Listener() { // that's what we get back
			@Override
			public void call(Object... args) {
				String message = (String) args[0];
				LiveRate newLiveRate = CryptocompareUtils.stringToLiveRate(message);
				if (newLiveRate != null) {
					System.out.println(newLiveRate);
					liveRateRepository.save(newLiveRate).subscribe();
				}
			}
		}).on(Socket.EVENT_DISCONNECT, (Object... args) -> System.out.println("EVENT_DISCONNECT"));
		
		socket.connect();
	}

	@GetMapping("/websocket2")
	public Flux<LiveRate> websocket2() {

		Flux<LiveRate> res = liveRateRepository.findByExchangeName("Poloniex").log();

		
		Flux<Long> durationFlux = Flux.interval(Duration.ofSeconds(1));
		
		return Flux.zip(res, durationFlux).
			map(t->t.getT1());
		
		// res = liveRateRepository.findAll().log();

		// Disposable c =
		// liveRateRepository.findAll().log().map(LiveRate::getExchangeName).subscribe(System.out::println);
		// System.out.println(c);

		// Predicate<? super Throwable> predicate;
		// LiveRate fallbackValue;
		// res.onErrorReturn(predicate, fallbackValue);

		//return res;
	}

}
