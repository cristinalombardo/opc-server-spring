package com.github.cristinalombardo.opcserver.wather.bean.element;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SysElement {

	private Integer type;
	
	private Integer id;
	
	private String country;
	
	private Long sunrise;
	
	private Long sunset;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getSunrise() {
		return sunrise;
	}

	public void setSunrise(Long sunrise) {
		this.sunrise = sunrise;
	}

	public Long getSunset() {
		return sunset;
	}

	public void setSunset(Long sunset) {
		this.sunset = sunset;
	}

	@Override
	public String toString() {
		return "SysElement [type=" + type + ", id=" + id + ", country=" + country + ", sunrise=" + sunrise + ", sunset="
				+ sunset + "]";
	}
	
	
}
