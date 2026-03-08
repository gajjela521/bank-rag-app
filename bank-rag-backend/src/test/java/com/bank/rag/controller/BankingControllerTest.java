package com.bank.rag.controller;

import com.bank.rag.entity.Customer;
import com.bank.rag.service.BankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankingController.class)
public class BankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingService bankingService; // This will mock the service including repository dependency chains

    @Autowired
    private ObjectMapper objectMapper;

    // We need to exclude other controllers or context that might require complex
    // beans (like RagService/OpenAI)
    // since @WebMvcTest slices only the web layer.
    // However, since BankingController does not depend on RagService, this should
    // be fine.

    @Test
    public void testGetAllCustomers() throws Exception {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setFirstName("Alice");

        given(bankingService.getAllCustomers()).willReturn(List.of(c1));

        mockMvc.perform(get("/api/banking/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    public void testCreateCustomer() throws Exception {
        Customer c1 = new Customer();
        c1.setFirstName("Bob");

        given(bankingService.createCustomer(any(Customer.class))).willReturn(c1);

        mockMvc.perform(post("/api/banking/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"));
    }
}
