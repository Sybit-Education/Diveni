package de.htwg.aume.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class Controller {

	@RequestMapping("/")
	public @ResponseBody String greeting() {
		return "Hello, World";
	}

}