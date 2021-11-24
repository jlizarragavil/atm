package com.atm.api.models.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atm.api.models.dao.ICustomerDao;
import com.atm.api.models.entity.Atm;
import com.atm.api.models.entity.Customer;

@Service
public class CustomerServiceImpl implements ICustomerService {
	private int moneyToWithdraw;
	private boolean empty;
	private int notesAcumulator = 0, atmAmountMoney = 0;
	@Autowired
	private ICustomerDao customerDao;
	
	Atm atm = new Atm();




	@Override
	public Customer findByAccountNumber(String accountNumber) {
		return customerDao.findByAccountNumber(accountNumber);
	}

	@Override
	@Transactional
	public Customer save(Customer client) {
		return customerDao.save(client);
	}

	//Gets the new balance
	public Integer withdrawManager(Integer amount, Integer currentBalance) {

		return (currentBalance - amount);
	}
	
	//Gets the maximum amount of money available
	public Integer maximumWithdrawal(Customer customer) {
		return customer.getBalance()+customer.getOverdraft();
	}

	//Calculate the minimum notes to dispense
	public TreeMap<Integer, Integer> atmNotesDispensed(Integer amount) {
		Map<Integer, Integer> atmMoney = atm.getMoney();

		moneyToWithdraw = amount;
		
		TreeMap<Integer, Integer> notesDispensed = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		notesDispensed.putAll(atmMoney);
		notesDispensed.forEach((noteValue, numberOfNotes) -> {
			numberOfNotes = 0;
			while (noteValue <= moneyToWithdraw && atmMoney.get(noteValue) > 0) {
				moneyToWithdraw = moneyToWithdraw - noteValue;
				numberOfNotes = numberOfNotes + 1;
				atmMoney.put(noteValue, atmMoney.get(noteValue) - 1);
			}

			notesDispensed.put(noteValue, numberOfNotes);
		});
		atm.setMoney(atmMoney);
		return notesDispensed;
	}

	//Checks if the ATM is empty
	public Boolean atmIsEmpty() {
		Map<Integer, Integer> atmEmptyCheck = atm.getMoney();
		TreeMap<Integer, Integer> notesDispensed = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		notesDispensed.putAll(atmEmptyCheck);
		Entry<Integer, Integer> lastEntry = notesDispensed.lastEntry();
		
		return lastEntry.getValue()==0;
	}

	//Returns number of notes available in the ATM
	@Override
	public TreeMap<Integer, Integer> atmNotes() {
		Map<Integer, Integer> atmNotes = atm.getMoney();
		TreeMap<Integer, Integer> notes = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		notes.putAll(atmNotes);
		return notes;
	}
	
	//returns the money inside the ATM
	@Override
	public Integer atmAmount() {
		Map<Integer, Integer> atmNotes = atm.getMoney();
		atmAmountMoney = 0;
		atmNotes.forEach((noteValue, numberOfNotes)->{
			atmAmountMoney = atmAmountMoney + noteValue*numberOfNotes;
		});
		return atmAmountMoney;
	}


}
