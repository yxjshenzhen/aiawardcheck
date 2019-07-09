package cn.com.xiaofabo.scia.aiawardcheck.entity;

import java.util.LinkedList;
import java.util.List;

public class Party {
	private List<Pair> propertyList;
	private String name;

	public Party() {
		this.propertyList = new LinkedList<Pair>();
	}

	public Party(String name) {
		this.name = name;
		this.propertyList = new LinkedList<Pair>();
	}

	public List<Pair> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(List<Pair> propertyList) {
		this.propertyList = propertyList;
	}
	public void addProperty(Pair property) {
		propertyList.add(property);
	}
	public Pair getProperty(int i) {
		return propertyList.get(i);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
