package com.github.cristinalombardo.opcserver.wather.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CoordWeatherBean {

	private Double lon;
	private Double lat;
	
	public CoordWeatherBean() {
		super();
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	@Override
	public String toString() {
		return "CoordWeatherBean [lon=" + lon + ", lat=" + lat + "]";
	}
	
}
