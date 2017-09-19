package com.github.ka4ok85.cryptocomparestreamconsumer;

import java.util.List;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.github.ka4ok85.cryptocomparestreamconsumer.entity.LiveRate;
import com.github.ka4ok85.cryptocomparestreamconsumer.repository.LiveRateReactiveRepository;
import com.github.ka4ok85.cryptocomparestreamconsumer.service.CryptocompareUtils;

import reactor.core.publisher.Flux;

@Controller
public class ThymeController {

	@Autowired
	private LiveRateReactiveRepository liveRateReactiveRepository;

/*
	@GetMapping("/biglist")
	public String bigListDataDriven(final Model model) throws UnsupportedEncodingException {
		//Flux<LiveRate> stream = liveRateReactiveRepository.findAll().log();
		Flux<LiveRate> stream = liveRateReactiveRepository.findByFromCurrencyAndToCurrency("BTC", "USD").share();
		model.addAttribute("dataSource", new ReactiveDataDriverContextVariable(stream, 1000));
		
        return "biglist-datadriven";


	    }
	*/
}
