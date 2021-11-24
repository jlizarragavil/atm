package com.atm.api.models.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

public class Atm {

	private Map<Integer, Integer> money = new HashMap<Integer, Integer>();
	
	public Atm() {
		money.put(50, 10);
		money.put(20, 30);
		money.put(10, 30);
		money.put(5, 20);
	}

	public Map<Integer, Integer> getMoney() {
		return money;
	}

	public void setMoney(Map<Integer, Integer> money) {
		this.money = money;
	}
	
}
