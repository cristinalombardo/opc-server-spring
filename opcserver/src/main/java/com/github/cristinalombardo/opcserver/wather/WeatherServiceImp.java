package com.github.cristinalombardo.opcserver.wather;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.cristinalombardo.opcserver.wather.bean.CurrentWeatherBean;

@Service
public class WeatherServiceImp implements WeatherService{

	private Log logger = LogFactory.getLog(getClass());

	//Contains the URL to call
	private String apiUrl;
	
	//Contains the city parameter
	private String city;

	//Contains the ApiKey
	private String weatherApikey;

	//Contains the language for the forecast
	private String lang;

	//Execute HTTP call with rest criteria
	private RestTemplate restTemplate;

	//Store the last weather downloaded
	private CurrentWeatherBean currentWeatherBean;

	public WeatherServiceImp(	
			@Value("${weather.apikey}") String weatherApikey, //Api key allowing the access to the weather service
			@Value("${weather.city}") String city, //Represent the city target for the weather
			@Value("${weather.lang}") String lang //Represent the lang to call the weather
			) {
		super();

		this.weatherApikey = weatherApikey;
		this.lang = lang;
		this.city = city;
		
		this.apiUrl = this.createUrl();
		//Initialize Rest Template
		this.restTemplate = new RestTemplate();
	}

	@Override
	public CurrentWeatherBean getCurrentWeather() {
		return this.currentWeatherBean;
	}


	@Scheduled(fixedRateString = "${weather.callinterval}")
	public void refreshCurrentWeatherBean() {
		//Get the data from URL,  mapping it on class CurrentWeatherBean, store on local attribue currentWeatherBean
		this.currentWeatherBean = this.restTemplate.getForObject(this.apiUrl, CurrentWeatherBean.class);

		logger.info(this.apiUrl  + "\nResponse: \nCurrent weather refreshed -> " + this.currentWeatherBean);
	}

	@Override
	public String getCity() {
		return this.city;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
		this.apiUrl = this.createUrl();
		this.refreshCurrentWeatherBean();
	}
	
	@Override
	public String getLang() {
		return lang;
	}

	@Override
	public void setLang(String lang) {
		this.lang = lang;
		this.apiUrl = this.createUrl();
		this.refreshCurrentWeatherBean();
	}

	@Override
	public String getApiCall() {
		return this.apiUrl;
	}

	private String createUrl() {
		//Define the remote weather url to call
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl("http://api.openweathermap.org/data/2.5/weather")
				.queryParam("q", city)
				.queryParam("appid", weatherApikey)
				.queryParam("lang", lang);
		return builder.toUriString();
	}
}
