package com.liyuan.hong.showbooking.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.liyuan.hong.showbooking.rest.service.ShowService;

import com.liyuan.hong.showbooking.domain.ShowDto;

@RestController
@RequestMapping(path = "/shows")
public class ShowController {
	
	@Autowired
	ShowService showService;
	
	public ShowController() {
		
	}
	
	@PostMapping(path = "/setup")
	@ResponseStatus(HttpStatus.CREATED)
	public void setupShow(@RequestBody ShowDto showDto) {
		showService.setupShow(showDto.getId(), showDto.getRows(), showDto.getRows(), showDto.getCancelWindow());
	}
	
}
