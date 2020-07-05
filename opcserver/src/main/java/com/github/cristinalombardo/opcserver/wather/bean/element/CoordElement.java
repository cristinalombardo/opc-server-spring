package com.github.cristinalombardo.opcserver.wather.bean.element;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CoordElement {

	private Double lon;
	private Double lat;
	
	public CoordElement() {
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
		return "CoordElement [lon=" + lon + ", lat=" + lat + "]";
	}

}
