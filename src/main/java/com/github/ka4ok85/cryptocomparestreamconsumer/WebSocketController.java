package com.github.ka4ok85.cryptocomparestreamconsumer;

import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@RestController
public class WebSocketController {

	@GetMapping("/websocket")
	public void websocket() throws URISyntaxException, JSONException {
		Socket socket = IO.socket("https://streamer.cryptocompare.com/");

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				System.out.println("EVENT_CONNECT");

				JSONArray jarr = new JSONArray();
				//jarr.put("5~CCCAGG~ETH~USD"); // aggregate
				jarr.put("2~Poloniex~BTC~USD"); // single
				
				
				JSONObject obj = new JSONObject(); 
				try {
					obj.put("subs", jarr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(jarr);
				socket.emit("SubAdd", obj);
				System.out.println("EVENT_CONNECT 2");
			}

		}).on("event", new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				System.out.println("event");
			}
		}).on("m", new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				System.out.println("m");
				System.out.println(args[0]);
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				System.out.println("EVENT_DISCONNECT");
			}

		}).on(Socket.EVENT_ERROR, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				System.out.println("ERROR");
			}

		}).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				System.out.println("EVENT_MESSAGE");
			}
		});

		socket.connect();

		

		// socket.disconnect();
		System.out.println("done");

	}


}
