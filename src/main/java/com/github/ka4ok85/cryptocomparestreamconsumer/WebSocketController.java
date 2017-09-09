package com.github.ka4ok85.cryptocomparestreamconsumer;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;


import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;
import com.github.ka4ok85.cryptocomparestreamconsumer.repository.LiveRateRepository;
import com.github.ka4ok85.cryptocomparestreamconsumer.service.CryptocompareUtils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;

@RestController
public class WebSocketController {

	private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
	@Autowired
	private LiveRateRepository liveRateRepository;

	@GetMapping("/websocket")
	public void websocket() throws URISyntaxException, JSONException {
		Socket socket = IO.socket("https://streamer.cryptocompare.com/");

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray jarr = new JSONArray();
				// jarr.put("5~CCCAGG~ETH~USD"); // aggregate
				// jarr.put("2~Poloniex~BTC~USD"); // single Poloniex
				jarr.put("5~CCCAGG~BTC~USD"); // aggregate BTC
				JSONObject obj = new JSONObject();
				try {
					obj.put("subs", jarr);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				socket.emit("SubAdd", obj);
			}
		}).on("m", new Emitter.Listener() { // that's what we get back
			@Override
			public void call(Object... args) {
				String message = (String) args[0];
				String mask;
				String[] items = message.split(CryptocompareUtils.separator);

				if (items[0].equals(CryptocompareUtils.acceptedType) && !items[4].equals(CryptocompareUtils.unacceptedFlag)) {
					mask = CryptocompareUtils.hexStringToBinaryString(items[items.length - 1]);
					if (CryptocompareUtils.validateMask(mask)) {
						System.out.println(mask);
						LiveRate newLiveRate = CryptocompareUtils.stringArrayToLiveRate(items, mask);
						if (newLiveRate != null) {
							liveRateRepository.save(newLiveRate).subscribe();
						}
					}
				}
			}
		}).on(Socket.EVENT_DISCONNECT, (Object... args) -> System.out.println("EVENT_DISCONNECT"));

		socket.connect();
	}

	@GetMapping(value = "/s9", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<LiveRate> s2() {
		return liveRateRepository.findByFromCurrency("BTC").log().share();
	}

}
