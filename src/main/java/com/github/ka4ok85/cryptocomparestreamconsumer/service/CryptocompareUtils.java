package com.github.ka4ok85.cryptocomparestreamconsumer.service;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;

@Component
public class CryptocompareUtils {

	public static final String separator = "~";

	// public static final String acceptedMask = "ce9";
	/*
	 * public static LiveRate stringToLiveRate(String message) { String[] items
	 * = message.split(CryptocompareUtils.separator);
	 *
	 * 
	 * we accept only certain message types if you want to accept all message
	 * types you will need to parse mask see
	 * https://cryptoqween.github.io/streamer/ccc-streamer-utilities.js
	 *
	 * if (items.length == 13 &&
	 * items[12].equals(CryptocompareUtils.acceptedMask)) { LiveRate newLiveRate
	 * = new LiveRate(items[1], items[2], items[3], Byte.parseByte(items[4]),
	 * Double.parseDouble(items[5]), Long.parseLong(items[6]),
	 * Double.parseDouble(items[7]), Double.parseDouble(items[8]),
	 * Long.parseLong(items[9]), Double.parseDouble(items[10]),
	 * Double.parseDouble(items[11]));
	 * 
	 * return newLiveRate; }
	 * 
	 * return null; }
	 */
	public static String hexStringToBinaryString(String hex) {
		return String.format("%16s", Integer.toBinaryString(Integer.parseInt(hex, 16))).replace(' ', '0');
	}

	public static boolean validateMask(String mask) {
		/*
		 * we need price, volume24Hour and volume24HourTo every other record is
		 * useless for us at this point every record by default has type,
		 * market, fromSymbol, toSymbol and flag
		 */
		if (mask.charAt(15) == '1' && mask.charAt(12) == '1' && mask.charAt(5) == '1' && mask.charAt(4) == '1') {
			return true;
		}

		return false;
	}

	public static LiveRate stringArrayToLiveRate(String[] items) {
		/*
		 * here we replace lastUpdated value with current timestamp. Usually
		 * difference is within 3 seconds range (depending on the network
		 * channel).
		 * 
		 * we can't rely on lastUpdated value provided by service since it
		 * included very rarely and that means we would skip 90%+ of all records
		 */
		LiveRate newLiveRate = new LiveRate(items[1], items[2], items[3], Byte.parseByte(items[4]),
				Double.parseDouble(items[5]), Instant.now().getEpochSecond(), Double.parseDouble(items[7]),
				Double.parseDouble(items[8]));
		/*
0 5
1 CCCAGG
2 BTC
3 USD
4 1
5 4415.43
6 156375.45963145923
7 665018292.0452391
8 c01
*/
		return newLiveRate;
	}
}
