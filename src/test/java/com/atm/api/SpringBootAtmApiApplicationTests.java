package com.atm.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.web.servlet.MockMvc;
import com.atm.api.controllers.CustomerRestController;
import com.atm.api.models.dao.ICustomerDao;
import com.atm.api.models.entity.Customer;
import com.atm.api.models.services.CustomerServiceImpl;


@SpringBootTest
@AutoConfigureMockMvc
class SpringBootAtmApiApplicationTests {

	Integer atmAmountMoney = 0;

	@Autowired
	MockMvc mockMvc;
	@Autowired
	CustomerRestController customerController;

	@MockBean
	ICustomerDao customerDao;

	private CustomerServiceImpl customerService = null;

	@BeforeEach
	void setUpCustomerService() {
		customerService = new CustomerServiceImpl();
	}

	@Test
	void withdrawManagerTest() {
		Integer amount = 300;
		Integer currentBalance = 800;
		assertEquals(customerService.withdrawManager(amount, currentBalance), Integer.valueOf(500));
	}

	@Test
	void maximumWithdrawalTest() {
		Customer customer = new Customer();
		customer.setBalance(800);
		customer.setOverdraft(200);
		assertEquals(customerService.maximumWithdrawal(customer),
				Integer.valueOf(customer.getBalance() + customer.getOverdraft()));
	}

	@Test
	void atmNotesDispensedTest() {
		Map<Integer, Integer> expectedAtmMoney = new HashMap<>();
		CustomerServiceImpl customerService = new CustomerServiceImpl();
		expectedAtmMoney.put(50, 2);
		expectedAtmMoney.put(20, 1);
		expectedAtmMoney.put(10, 1);
		expectedAtmMoney.put(5, 0);

		assertEquals(expectedAtmMoney, customerService.atmNotesDispensed(130));
	}

	@Test
	void atmEmptyTest() {
		customerService.atmNotesDispensed(1500);
		assertTrue(customerService.atmIsEmpty());

	}

	@Test
	void atmNotEmptyTest() {
		customerService.atmNotesDispensed(100);
		assertFalse(customerService.atmIsEmpty());
	}

	@Test
	void atmNotesTest() {
		Map<Integer, Integer> atmNotes = new HashMap<>();
		atmNotes.put(50, 10);
		atmNotes.put(20, 30);
		atmNotes.put(10, 30);
		atmNotes.put(5, 20);

		assertEquals(customerService.atmNotes(), atmNotes);
	}

	@Test
	void atmAmountTest() {
		assertEquals(customerService.atmAmount(), Integer.valueOf(1500));
	}

	@Test
	void getCustomerTest() throws Exception {
		Customer customer = new Customer();
		customer.setAccountNumber("123456789");
		customer.setPin("1234");
		customer.setBalance(800);
		customer.setOverdraft(200);
		Mockito.when(customerDao.findByAccountNumber(customer.getAccountNumber())).thenReturn(customer);
		this.mockMvc.perform(get("/api/customers/" + customer.getAccountNumber()).param("pin", customer.getPin()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("maximumWithdrawal").value(customer.getBalance() + customer.getOverdraft()))
				.andExpect(jsonPath("balance").value(customer.getBalance())).andReturn();
	}

	@Test
	void getErrorDifferentAccountNumberCustomerTest() throws Exception {
		Customer customer = new Customer();
		customer.setAccountNumber("123456789");
		customer.setPin("1234");
		customer.setBalance(800);
		customer.setOverdraft(200);
		Mockito.when(customerDao.findByAccountNumber(customer.getAccountNumber())).thenReturn(customer);
		this.mockMvc.perform(get("/api/customers/" + 74125).param("pin", customer.getPin()))
				.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	void getErrorNullAccountNumberCustomerTest() throws Exception {
		Customer customer = null;
		Mockito.when(customerDao.findByAccountNumber(Mockito.anyString())).thenReturn(customer);
		this.mockMvc.perform(get("/api/customers/" + 74125).param("pin", "11")).andExpect(status().isNotFound())
				.andReturn();
	}

}
