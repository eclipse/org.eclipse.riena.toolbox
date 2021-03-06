/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package $packageName$.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import $packageName$.common.Address;
import $packageName$.common.BankData;
import $packageName$.common.Birth;
import $packageName$.common.Customer;
import $packageName$.common.ICustomerService;

/**
 * Customers Service Class that is exposed as Webservice. It implements
 * ICustomers (the customer maintaince interface) and ICustomerSearch (the
 * interface for search on the customer data)
 */
public class CustomerService implements ICustomerService {

	private Map<Integer, Customer> customers;
	private int nextUniqueCustomerNumber;

	public CustomerService() {
		customers = new HashMap<Integer, Customer>();

		nextUniqueCustomerNumber = 1;
		initializeCustomers();
	}

	public Customer[] findCustomer(Customer searchedCustomer) {
		List<Customer> l = new ArrayList<Customer>();

		for (Customer c : customers.values()) {
			if (isIdentical(c, searchedCustomer)) {
				l.add(c);
			}
		}

		return l.toArray(new Customer[l.size()]);
	}


	public void store(Customer customer) {
		storeInternal(customer);
	}

	// helping methods
	//////////////////

	private boolean contains(String original, String other) {
		if (other == null || other.equals("")) {
			return true;
		}
		return original.toUpperCase().contains(other.toUpperCase());
	}

	private Integer getNextUniqueCustomerNumber() {
		return nextUniqueCustomerNumber++;
	}

	private void initializeCustomerNumber(Customer customer) {
		customer.setCustomerNumber(getNextUniqueCustomerNumber());
		storeInternal(customer);
	}

	private void initializeCustomers() {
		Customer customer = new Customer();
		customer.setFirstName("Han");
		customer.setLastName("Solo");
		Address address = new Address();
		address.setCity("Frankfurt am Main");
		address.setStreet("Am Main 233");
		address.setZipCode("61236");
		address.setCountry("Germany");
		customer.setAddress(address);

		customer.setBirth(new Birth());
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		try {
			customer.getBirth().setBirthDay(format.parse("01.04.1962"));
		} catch (ParseException e) {
			// TODO Throw exception
			e.printStackTrace();
		}
		customer.getBirth().setBirthPlace("Frankfurt");
		customer.setBankData(new ArrayList<BankData>());
		initializeCustomerNumber(customer);

		customer = new Customer();
		customer.setFirstName("Luke");
		customer.setLastName("Skywalker");
		address = new Address();
		address.setCity("Washington");
		address.setStreet("Any Road 845");
		address.setZipCode("98123898");
		address.setCountry("USA");
		customer.setAddress(address);
		customer.setBirth(new Birth());
		try {
			customer.getBirth().setBirthDay(format.parse("01.04.1963"));
		} catch (ParseException e) {
			// TODO Throw exception
			e.printStackTrace();
		}
		customer.getBirth().setBirthPlace("Frankfurt");
		customer.setBankData(new ArrayList<BankData>());
		initializeCustomerNumber(customer);

		customer = new Customer();
		customer.setFirstName("Frodo");
		customer.setLastName("Baggins");
		address = new Address();
		address.setCity("Hanau");
		address.setStreet("Gr�ner Weg 3");
		address.setZipCode("62342");
		address.setCountry("Germany");
		customer.setAddress(address);
		customer.setBirth(new Birth());
		try {
			customer.getBirth().setBirthDay(format.parse("01.04.1964"));
		} catch (ParseException e) {
			// TODO Throw exception
			e.printStackTrace();
		}
		customer.getBirth().setBirthPlace("Frankfurt");
		customer.setBankData(new ArrayList<BankData>());
		initializeCustomerNumber(customer);
	}

	private boolean isIdentical(Customer customer, Customer searchedCustomer) {
		if (searchedCustomer.getCustomerNumber() != null
				&& !searchedCustomer.getCustomerNumber().equals(customer.getCustomerNumber())) {
			return false;
		}

		if (!contains(customer.getLastName(), searchedCustomer.getLastName())) {
			return false;
		}

		if (!contains(customer.getFirstName(), searchedCustomer.getFirstName())) {
			return false;
		}

		return true;
	}

	/**
	 * This method is also used internally to store customers. No security is
	 * checked.
	 *
	 * @param customer
	 */
	private void storeInternal(Customer customer) {
		customer.setId(customer.getCustomerNumber());
		customers.put(customer.getCustomerNumber(), customer);
	}
}
