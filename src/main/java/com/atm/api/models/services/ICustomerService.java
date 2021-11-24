package com.atm.api.models.services;

import java.util.List;
import java.util.TreeMap;

import com.atm.api.models.entity.Customer;

public interface ICustomerService {

	public Customer findByAccountNumber(String accountNumber);
	public Customer save(Customer client);
	Integer withdrawManager(Integer amount, Integer currentBalance);
	TreeMap<Integer, Integer> atmNotesDispensed(Integer amount);
	public Boolean atmIsEmpty();
	TreeMap<Integer, Integer> atmNotes();
	Integer atmAmount();
	public Integer maximumWithdrawal(Customer customer);
}
