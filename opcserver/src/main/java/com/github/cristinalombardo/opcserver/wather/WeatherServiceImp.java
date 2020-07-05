package com.github.cristinalombardo.opcserver.wather;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.cristinalombardo.opcserver.wather.bean.WeatherBean;

@Service
public class WeatherServiceImp implements WeatherService{

	private Log logger = LogFactory.getLog(getClass());

	//Contains the URL to call
	private final String apiUrl;
	
	//Contains the URL with the Query string
	private String apiCompleteUrl;
	
	//Contains the city parameter
	private String city;

	//Contains the ApiKey
	private String weatherApikey;

	//Contains the language for the forecast
	private String lang;

	//Execute HTTP call with rest criteria
	private RestTemplate restTemplate;

	//Store the last weather downloaded
	private WeatherBean currentWeatherBean;

	//Inject parameter on Constructor from Application Properties with @Value annotation
	public WeatherServiceImp(	
			@Value("${weather.apiurl}") String apiurl, //Api key allowing the access to the weather service
			@Value("${weather.apikey}") String weatherApikey, //Api key allowing the access to the weather service
			@Value("${weather.city}") String city, //Represent the city target for the weather
			@Value("${weather.lang}") String lang //Represent the lang to call the weather
			) {
		super();

		this.weatherApikey = weatherApikey;
		this.apiUrl = apiurl;
		this.lang = lang;
		this.city = city;
		
		this.createOrRefreshCompleteUrl();
		//Initialize Rest Template
		this.restTemplate = new RestTemplate();
	}

	@Override
	public WeatherBean getCurrentWeather() {
		return this.currentWeatherBean;
	}


	@Scheduled(fixedRateString = "${weather.callinterval}")
	public void refreshCurrentWeatherBean() {
		//Get the data from URL,  mapping the response on class CurrentWeatherBean, store on local attribute currentWeatherBean
		this.currentWeatherBean = this.restTemplate.getForObject(this.apiCompleteUrl, WeatherBean.class);

		logger.info("\nCall: " + this.apiCompleteUrl  + "\nResponse -> " + this.currentWeatherBean);
	}

	@Override
	public String getCity() {
		return this.city;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
		this.createOrRefreshCompleteUrl();
		this.refreshCurrentWeatherBean();
	}
	
	@Override
	public String getLang() {
		return lang;
	}

	@Override
	public void setLang(String lang) {
		this.lang = lang;
		this.createOrRefreshCompleteUrl();
		this.refreshCurrentWeatherBean();
	}

	@Override
	public String getApiCall() {
		return this.apiCompleteUrl;
	}

	private void createOrRefreshCompleteUrl() {
		//Define the remote weather url to call
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(this.apiUrl)
				.queryParam("q", city)
				.queryParam("appid", weatherApikey)
				.queryParam("lang", lang);
		this.apiCompleteUrl = builder.toUriString();
	}
}
