package com.github.ka4ok85.cryptocomparestreamconsumer.service;

import org.springframework.stereotype.Component;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;

@Component
public class CryptocompareUtils {

	public static final String separator = "~";
	public static final String acceptedMask = "ce9";

	public static LiveRate stringToLiveRate(String message) {
		String[] items = message.split(CryptocompareUtils.separator);

		/*
		 * we accept only certain message types if you want to accept all
		 * message types you will need to parse mask see
		 * https://cryptoqween.github.io/streamer/ccc-streamer-utilities.js
		 */
		if (items.length == 13 && items[12].equals(CryptocompareUtils.acceptedMask)) {
			LiveRate newLiveRate = new LiveRate(items[1], items[2], items[3], Byte.parseByte(items[4]),
					Double.parseDouble(items[5]), Long.parseLong(items[6]), Double.parseDouble(items[7]),
					Double.parseDouble(items[8]), Long.parseLong(items[9]), Double.parseDouble(items[10]),
					Double.parseDouble(items[11]));

			return newLiveRate;
		}

		return null;
	}
}
