package com.github.cristinalombardo.opcserver.wather;

import com.github.cristinalombardo.opcserver.wather.bean.WeatherBean;

public interface WeatherService {
	
	/**
	 * Return the current weather of pre-configured city 
	 * @return CurrentWeatherBean that contains the last read weather
	 */
	WeatherBean getCurrentWeather();
	
	String getCity();
	
	String getApiCall();
	
	void setCity(String city);
	
	String getLang();

	void setLang(String lang);
}
