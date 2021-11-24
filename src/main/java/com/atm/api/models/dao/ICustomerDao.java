package com.atm.api.models.dao;
import org.springframework.data.jpa.repository.JpaRepository;

import com.atm.api.models.entity.Customer;
public interface ICustomerDao extends JpaRepository<Customer, Long>{
	//@Query("SELECT u FROM Customer u where u.accountNumber= ?1")
	Customer findByAccountNumber(String pin);
	//int withdrawManager(Integer amount, Integer currentBalance);
}
