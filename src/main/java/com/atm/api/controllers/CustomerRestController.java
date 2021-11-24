package com.atm.api.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atm.api.models.entity.Customer;
import com.atm.api.models.services.ICustomerService;

@RestController
@RequestMapping("/api")
public class CustomerRestController {

	@Autowired
	private ICustomerService customerService;


	@GetMapping("/customers/{accountNumber}")
	public ResponseEntity<Map<String, Object>> show(@RequestParam String pin, @PathVariable String accountNumber) {
		Customer customer = null;
		Map<String, Object> response = new HashMap<>();
		try {
			customer = customerService.findByAccountNumber(accountNumber);
		} catch (DataAccessException ex) {
			response.put("message", "Error in DB");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (customer == null || !pin.equals(customer.getPin())) {
			response.put("message", "Bank account : " + accountNumber + " and PIN: " + pin + " doesn´t match");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		response.put("balance", customer.getBalance());
		response.put("maximumWithdrawal", customerService.maximumWithdrawal(customer));
		response.put("atmNotes", customerService.atmNotes());
		response.put("atmAmount", customerService.atmAmount());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PutMapping("/customers/{accountNumber}")
	public ResponseEntity<Map<String, Object>> update(@RequestParam String pin, @RequestParam Integer amount,
			@PathVariable String accountNumber) {
		Customer currentCustomer = customerService.findByAccountNumber(accountNumber);
		Customer updatedCustomer = null;
		boolean atmEmpty = false;
		Map<String, Object> response = new HashMap<>();

		if (currentCustomer == null || !pin.equals(currentCustomer.getPin())) {
			response.put("message", "Bank account : " + accountNumber + " and PIN: " + pin + " doesn´t match");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if (!atmEmpty == customerService.atmIsEmpty()) {
			response.put("message", "ATM is empty");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if (currentCustomer.getBalance() + currentCustomer.getOverdraft() >= amount
				&& amount <= customerService.atmAmount()) {
			currentCustomer.setBalance(customerService.withdrawManager(amount, currentCustomer.getBalance()));
			try {
				updatedCustomer = customerService.save(currentCustomer);

			} catch (DataAccessException ex) {
				response.put("message", "Error in insert DB");
				response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			response.put("new balance", updatedCustomer.getBalance());
			response.put("dispensedNotes", customerService.atmNotesDispensed(amount));
			response.put("message", "Client updated successfully");

		} else if (amount > customerService.atmAmount()) {
			response.put("message", "Atm doesn´t have that amount of money");
		} else if (currentCustomer.getBalance() + currentCustomer.getOverdraft() < amount) {
			response.put("message", "You cannot withdraw that amount of money");
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
