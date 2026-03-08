package com.bank.rag.repository;

import com.bank.rag.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndSsnLast4(String firstName, String lastName, String ssnLast4);
}
