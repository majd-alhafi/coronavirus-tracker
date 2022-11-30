package com.example.coronavirustracker.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.coronavirustracker.models.LocationStats;
import com.example.coronavirustracker.services.CoronaVirusDataService;

@Controller
public class HomeConroller {
	@Autowired 
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		List<LocationStats> allStats = coronaVirusDataService.getAllStats();
		int sum = allStats.stream().mapToInt(s -> s.getLatestTotalCases()).sum();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("allCasesSum", sum);
		return "home";
	}
}
