package com.github.cristinalombardo.opcserver.wather.bean.element;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CloudsElement {

	private Double all;

	public Double getAll() {
		return all;
	}

	public void setAll(Double all) {
		this.all = all;
	}

	@Override
	public String toString() {
		return "CloudsElement [all=" + all + "]";
	}

	
	
	
}
