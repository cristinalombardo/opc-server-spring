package com.github.cristinalombardo.opcserver.wather.bean.element;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MainElement {

	private Double temp;
	
	private Double pressure;
	
	private Double humidity;
	
	@JsonProperty("feels_like")
	private Double feelsLike;
	
	@JsonProperty("temp_min")
	private Double tempMin;
	
	@JsonProperty("temp_max")
	private Double tempMax;

	public Double getTemp() {
		return temp;
	}
	
	public Double getTempAsCentigrate() {
		return this.temp == null ? -1000.0: this.temp - 273.15;
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

	public Double getFeelsLike() {
		return feelsLike;
	}
	
	public Double getFeelsLikeAsCentigrate() {
		return this.feelsLike == null ? -1000.0: this.feelsLike - 273.15;
	}

	public void setFeelsLike(Double feelsLike) {
		this.feelsLike = feelsLike;
	}

	public Double getTempMin() {
		return tempMin;
	}
	
	public Double getTempMinAsCentigrate() {
		return this.tempMin == null ? -1000.0: this.tempMin - 273.15;
	}

	public void setTempMin(Double tempMin) {
		this.tempMin = tempMin;
	}

	public Double getTempMax() {
		return tempMax;
	}
	
	public Double getTempMaxAsCentigrate() {
		return this.tempMax == null ? -1000.0: this.tempMax - 273.15;
	}

	public void setTempMax(Double tempMax) {
		this.tempMax = tempMax;
	}

	@Override
	public String toString() {
		return "MainElement [temp=" + temp + ", pressure=" + pressure + ", humidity=" + humidity + ", feelsLike="
				+ feelsLike + ", tempMin=" + tempMin + ", tempMax=" + tempMax + "]";
	}


}
