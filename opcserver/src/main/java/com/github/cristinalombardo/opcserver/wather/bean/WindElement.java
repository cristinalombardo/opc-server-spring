package com.github.cristinalombardo.opcserver.wather.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WindElement {

	private Double speed;
	
	private Double deg;

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getDeg() {
		return deg;
	}

	public void setDeg(Double deg) {
		this.deg = deg;
	}

	@Override
	public String toString() {
		return "WindElement [speed=" + speed + ", deg=" + deg + "]";
	}
	
}
