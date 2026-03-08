package com.bank.rag.controller;

import com.bank.rag.entity.Customer;
import com.bank.rag.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banking")
@CrossOrigin(origins = "*")
public class BankingController {

    @Autowired
    private BankingService bankingService;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(bankingService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(bankingService.getCustomerById(id));
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(bankingService.createCustomer(customer));
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seed() {
        bankingService.seedData();
        return ResponseEntity.ok("Seeded");
    }
}
