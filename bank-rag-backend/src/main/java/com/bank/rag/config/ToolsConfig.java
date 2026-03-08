package com.bank.rag.config;

import com.bank.rag.entity.Customer;
import com.bank.rag.service.BankingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class ToolsConfig {

    public record CustomerRequest(String firstName, String lastName, String ssnLast4) {}
    public record CustomerResponse(String details) {}

    @Bean
    @Description("Get a customer's account and balance details using their first name, last name, and the last 4 digits of their SSN.")
    public Function<CustomerRequest, CustomerResponse> getCustomerDetails(BankingService bankingService) {
        return request -> {
            Customer customer = bankingService.getCustomerByDetails(request.firstName(), request.lastName(), request.ssnLast4());
            if (customer == null) {
                return new CustomerResponse("No customer found with the provided details.");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Customer Information:\n");
            sb.append("Name: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
            sb.append("Email: ").append(customer.getEmail()).append("\n");
            sb.append("Phone: ").append(customer.getPhoneNumber()).append("\n");
            if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
                sb.append("Accounts:\n");
                customer.getAccounts().forEach(a -> {
                    sb.append("- ").append(a.getAccountType())
                      .append(" (").append(a.getAccountNumber()).append("): $")
                      .append(a.getBalance()).append("\n");
                });
            } else {
                sb.append("No active accounts found.\n");
            }
            return new CustomerResponse(sb.toString());
        };
    }
}
