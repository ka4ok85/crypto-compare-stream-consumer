package com.github.ka4ok85.cryptocomparestreamconsumer;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;
import com.github.ka4ok85.cryptocomparestreamconsumer.repository.LiveRateReactiveRepository;
import com.github.ka4ok85.cryptocomparestreamconsumer.service.CryptocompareUtils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import reactor.core.publisher.Flux;

@RestController
public class WebSocketController {

	private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
	
	@Autowired
	private LiveRateReactiveRepository liveRateReactiveRepository;
	
	/*
	 * this endpoint triggers consuming process rates through websocket valid
	 * rates stored in MongoDB
	 */
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
				jarr.put("5~CCCAGG~ETH~USD"); // aggregate ETH
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
				System.out.println(message);
				String mask;
				String[] items = message.split(CryptocompareUtils.separator);
				if (items[0].equals(CryptocompareUtils.acceptedType)
						&& !items[4].equals(CryptocompareUtils.unacceptedFlag)) {
					mask = CryptocompareUtils.hexStringToBinaryString(items[items.length - 1]);
					if (CryptocompareUtils.validateMask(mask)) {

						LiveRate newLiveRate = CryptocompareUtils.stringArrayToLiveRate(items, mask);
						if (newLiveRate != null) {
							liveRateReactiveRepository.save(newLiveRate).subscribe();
						}
					}
				}

			}
		}).on(Socket.EVENT_DISCONNECT, (Object... args) -> System.out.println("EVENT_DISCONNECT"));

		socket.connect();
	}

	/*
	 * this endpoint provides live stream reading rates from MongoDB
	 */
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<LiveRate> liveStream(@RequestParam(value = "currency", required = true) String currency) {
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		log.info(Long.toString(timeStampSeconds));
		return liveRateReactiveRepository.findByFromCurrencyAndToCurrencyAndLastUpdateGreaterThan(currency, "USD", timeStampSeconds).log().share();
	}

	/*
	 * this endpoint provides live stream reading rates from MongoDB
	 */
	@GetMapping(value = "/last")
	public Flux<LiveRate> lastRates(@RequestParam(value = "currency", required = true) String currency) {
		
		return liveRateReactiveRepository.findWhateverByFromCurrencyAndToCurrency(currency, "USD");
	}


}
