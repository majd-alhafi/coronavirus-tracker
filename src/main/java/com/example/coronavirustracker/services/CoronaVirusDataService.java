package com.example.coronavirustracker.services;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.coronavirustracker.models.LocationStats;

import jakarta.annotation.PostConstruct;

@Service
public class CoronaVirusDataService {
	private List<LocationStats> allStats = new ArrayList<>();
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	
	private HttpResponse<String> sendARequest() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = (HttpRequest) HttpRequest.newBuilder()
				.uri(URI.create(VIRUS_DATA_URL))
				.build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		return httpResponse;
	}
	
	private List<LocationStats> addToList(Iterable<CSVRecord> records) {
		List<LocationStats> tempList = new ArrayList<>();
        String stateColumnName = "Province/State";
        String countryColumnName = "Country/Region";
		for (CSVRecord record : records) {
		    String state = record.get(stateColumnName);
		    String country = record.get(countryColumnName);
		    int latestTotalCases = Integer.parseInt(record.get(record.size()-1));
		    LocationStats tempLocationStatRecored = new LocationStats(state,country,latestTotalCases);
		    tempList.add(tempLocationStatRecored);
		}
		return tempList;
	}
	
	@SuppressWarnings("deprecation")
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	private void fetchVirusData() throws IOException, InterruptedException {
		HttpResponse<String> httpResponse = sendARequest();
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		this.allStats = addToList(records);
		
	}

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	
}
