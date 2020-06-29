package com.github.cristinalombardo.opcserver.wather.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WeatherDataBean {

	private Double temp;
	
	private Double pressure;
	
	private Double humidity;
	
	@JsonProperty("feels_like")
	private String feelsLike;
	
	@JsonProperty("temp_min")
	private Double tempMin;
	
	@JsonProperty("temp_max")
	private Double tempMax;

	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	public Double getPressure() {
		return pressure;
	}

	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}

	public String getFeelsLike() {
		return feelsLike;
	}

	public void setFeelsLike(String feelsLike) {
		this.feelsLike = feelsLike;
	}

	public Double getTempMin() {
		return tempMin;
	}

	public void setTempMin(Double tempMin) {
		this.tempMin = tempMin;
	}

	public Double getTempMax() {
		return tempMax;
	}

	public void setTempMax(Double tempMax) {
		this.tempMax = tempMax;
	}

	@Override
	public String toString() {
		return "WeatherDataBean [temp=" + temp + ", pressure=" + pressure + ", humidity=" + humidity + ", feelsLike="
				+ feelsLike + ", tempMin=" + tempMin + ", tempMax=" + tempMax + "]";
	}

}
