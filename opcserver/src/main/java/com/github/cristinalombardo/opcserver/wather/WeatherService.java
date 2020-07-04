package com.github.cristinalombardo.opcserver.wather;

import com.github.cristinalombardo.opcserver.wather.bean.CurrentWeatherBean;

public interface WeatherService {
	
	/**
	 * Return the current weather of pre-configured city 
	 * @return CurrentWeatherBean that contains the last read weather
	 */
	CurrentWeatherBean getCurrentWeather();
	
	String getCity();
	
	String getApiCall();
	
	void setCity(String city);
	
	String getLang();

	void setLang(String lang);
}
