package com.bank.rag.service;

import com.bank.rag.entity.Account;
import com.bank.rag.entity.Customer;
import com.bank.rag.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer createCustomer(Customer customer) {
        // Simple logic for accounts if passed
        if (customer.getAccounts() != null) {
            for (Account acc : customer.getAccounts()) {
                acc.setCustomer(customer);
            }
        }
        return customerRepository.save(customer);
    }

    // Seed some data if empty (for demo)
    public void seedData() {
        if (customerRepository.count() == 0) {
            Customer c1 = new Customer();
            c1.setFirstName("John");
            c1.setLastName("Doe");
            c1.setEmail("john.doe@example.com");
            c1.setPhoneNumber("555-0100");

            Account a1 = new Account(null, "chk_123456", "CHECKING", new BigDecimal("1500.00"), c1);
            Account a2 = new Account(null, "sav_123456", "SAVINGS", new BigDecimal("5000.00"), c1);

            c1.setAccounts(List.of(a1, a2));
            customerRepository.save(c1);

            Customer c2 = new Customer();
            c2.setFirstName("Jane");
            c2.setLastName("Smith");
            c2.setEmail("jane.smith@example.com");
            c2.setPhoneNumber("555-0200");

            Account a3 = new Account(null, "chk_987654", "CHECKING", new BigDecimal("2800.50"), c2);
            c2.setAccounts(List.of(a3));
            customerRepository.save(c2);

            System.out.println("Seeded customer data");
        }
    }
}
