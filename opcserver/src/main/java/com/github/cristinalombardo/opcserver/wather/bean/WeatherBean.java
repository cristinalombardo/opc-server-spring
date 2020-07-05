package com.github.cristinalombardo.opcserver.wather.bean;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.cristinalombardo.opcserver.wather.bean.element.CloudsElement;
import com.github.cristinalombardo.opcserver.wather.bean.element.CoordElement;
import com.github.cristinalombardo.opcserver.wather.bean.element.MainElement;
import com.github.cristinalombardo.opcserver.wather.bean.element.SysElement;
import com.github.cristinalombardo.opcserver.wather.bean.element.WeatherElement;
import com.github.cristinalombardo.opcserver.wather.bean.element.WindElement;

/**
 * Contains the result of calling Weather API
 * @author Cristina Lombardo
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class WeatherBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private CoordElement coord;
	
	private List<WeatherElement> weather;
	
	private String base;
	
	private MainElement main;
	
	private Integer visibility;
	
	private WindElement wind;
	
	private CloudsElement clouds;
	
	private Long dt;
	
	private SysElement sys;
	
	private Long timezone;
	
	private Integer id;
	
	private String name;
	
	private Integer code;

	public CoordElement getCoord() {
		return coord;
	}

	public void setCoord(CoordElement coord) {
		this.coord = coord;
	}

	public List<WeatherElement> getWeather() {
		return weather;
	}

	public void setWeather(List<WeatherElement> weather) {
		this.weather = weather;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public MainElement getMain() {
		return main;
	}

	public void setMain(MainElement main) {
		this.main = main;
	}

	public Integer getVisibility() {
		return visibility;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}

	public WindElement getWind() {
		return wind;
	}

	public void setWind(WindElement wind) {
		this.wind = wind;
	}

	public CloudsElement getClouds() {
		return clouds;
	}

	public void setClouds(CloudsElement clouds) {
		this.clouds = clouds;
	}

	public Long getDt() {
		return dt;
	}

	public void setDt(Long dt) {
		this.dt = dt;
	}

	public SysElement getSys() {
		return sys;
	}

	public void setSys(SysElement sys) {
		this.sys = sys;
	}

	public Long getTimezone() {
		return timezone;
	}

	public void setTimezone(Long timezone) {
		this.timezone = timezone;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "WeatherBean [coord=" + coord + ", weather=" + weather + ", base=" + base + ", main=" + main
				+ ", visibility=" + visibility + ", wind=" + wind + ", clouds=" + clouds + ", dt=" + dt + ", sys=" + sys
				+ ", timezone=" + timezone + ", id=" + id + ", name=" + name + ", code=" + code + "]";
	}

	
}
