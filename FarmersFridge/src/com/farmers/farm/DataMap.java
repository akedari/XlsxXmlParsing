package com.farmers.farm;

import java.util.HashMap;

public class DataMap {

	private HashMap<String, HashMap<String, HashMap<String, Double>>> data;
	
	public DataMap() {
		data = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
	}
	
	public HashMap<String, HashMap<String, HashMap<String, Double>>> getData() {
		return data;
	}

	public void getData(HashMap<String, HashMap<String, HashMap<String, Double>>> data) {
		this.data = data;
	}
}
