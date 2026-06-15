package com.devsuperior.dsmeta.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import jakarta.persistence.EntityNotFoundException;
import com.twilio.exception.TwilioException;
import com.twilio.http.TwilioRestClient;
import com.twilio.http.Response;
import com.twilio.http.Request;
import com.twilio.Twilio;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;

@ExtendWith(MockitoExtension.class)
public class SmsServiceTests {

    @InjectMocks
    private SmsService service;

    @Mock
    private SaleRepository repository;

    private Sale sale;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;

        sale = new Sale();
        sale.setId(existingId);
        sale.setSellerName("Kal-El");
        sale.setVisited(50);
        sale.setDeals(25);
        sale.setAmount(10000.0);
        sale.setDate(LocalDate.of(2022, 10, 15));

        // Injecting the mocked/dummy Twilio credentials via ReflectionTestUtils
        ReflectionTestUtils.setField(service, "twilioSid", "mock_sid");
        ReflectionTestUtils.setField(service, "twilioKey", "mock_key");
        ReflectionTestUtils.setField(service, "twilioPhoneFrom", "+1234567890");
        ReflectionTestUtils.setField(service, "twilioPhoneTo", "+1987654321");
    }

    @Test
    public void sendSmsShouldExecuteSuccessfullyWhenSaleExists() {
        when(repository.getReferenceById(existingId)).thenReturn(sale);

        // When/Then - Should run with no errors, activating mock check in SmsService
        service.sendSms(existingId);

        verify(repository, times(1)).getReferenceById(existingId);
    }

    @Test
    public void sendSmsShouldThrowEntityNotFoundExceptionWhenSaleDoesNotExist() {
        when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        // When/Then - Should propagate error from repository
        assertThrows(EntityNotFoundException.class, () -> {
            service.sendSms(nonExistingId);
        });

        verify(repository, times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void sendSmsShouldExecuteSuccessfullyWhenTwilioSidIsNull() {
        ReflectionTestUtils.setField(service, "twilioSid", null);
        when(repository.getReferenceById(existingId)).thenReturn(sale);

        service.sendSms(existingId);

        verify(repository, times(1)).getReferenceById(existingId);
    }

    @Test
    public void sendSmsShouldExecuteSuccessfullyWhenTwilioSidIsEmpty() {
        ReflectionTestUtils.setField(service, "twilioSid", "   ");
        when(repository.getReferenceById(existingId)).thenReturn(sale);

        service.sendSms(existingId);

        verify(repository, times(1)).getReferenceById(existingId);
    }

    @Test
    public void sendSmsShouldExecuteSuccessfullyWhenTwilioSidContainsDummy() {
        ReflectionTestUtils.setField(service, "twilioSid", "dummy_key_test");
        when(repository.getReferenceById(existingId)).thenReturn(sale);

        service.sendSms(existingId);

        verify(repository, times(1)).getReferenceById(existingId);
    }

    @Test
    public void sendSmsShouldExecuteSuccessfullyWhenTwilioSidContainsYour() {
        ReflectionTestUtils.setField(service, "twilioSid", "YOUR_TWILIO_SID");
        when(repository.getReferenceById(existingId)).thenReturn(sale);

        service.sendSms(existingId);

        verify(repository, times(1)).getReferenceById(existingId);
    }

    @Test
    public void sendSmsShouldAttemptTwilioCallAndThrowExceptionWhenCredentialsAreNotMocked() {
        // Credentials that bypass our sandbox check to cover the rest of the lines
        ReflectionTestUtils.setField(service, "twilioSid", "AC_TEST_ACCOUNT_SID_SAFE_FROM_GITHUB_DETECTION");
        ReflectionTestUtils.setField(service, "twilioKey", "real_key_format_here");
        ReflectionTestUtils.setField(service, "twilioPhoneFrom", "+1234567890");
        ReflectionTestUtils.setField(service, "twilioPhoneTo", "+1987654321");

        when(repository.getReferenceById(existingId)).thenReturn(sale);

        // Should try to reach Twilio API and fail with TwilioException (due to invalid credentials or network)
        // This executes and covers Twilio.init and Message.creator lines in the service class.
        assertThrows(TwilioException.class, () -> {
            service.sendSms(existingId);
        });

        verify(repository, times(1)).getReferenceById(existingId);
    }

    @Test
    public void sendSmsShouldExecuteRealTwilioPathSuccessfullyWithMockedClient() {
        // Credentials that bypass our sandbox check
        ReflectionTestUtils.setField(service, "twilioSid", "AC_TEST_ACCOUNT_SID_SAFE_FROM_GITHUB_DETECTION");
        ReflectionTestUtils.setField(service, "twilioKey", "real_key_format_here");
        ReflectionTestUtils.setField(service, "twilioPhoneFrom", "+1234567890");
        ReflectionTestUtils.setField(service, "twilioPhoneTo", "+1987654321");

        when(repository.getReferenceById(existingId)).thenReturn(sale);

        // Mock TwilioRestClient and Response
        TwilioRestClient mockClient = org.mockito.Mockito.mock(TwilioRestClient.class);
        
        // Stub ObjectMapper to prevent NullPointerException on Message.fromJson
        when(mockClient.getObjectMapper()).thenReturn(new ObjectMapper());
        
        Response mockResponse = new Response("{\"sid\":\"SM1234567890abcdef1234567890abcdef\"}", 201);
        when(mockClient.request(any(Request.class))).thenReturn(mockResponse);
        
        // Configure Twilio to use our mocked client
        Twilio.setRestClient(mockClient);

        // Execute service call - this runs the real code block entirely (lines 48-55) without real HTTP requests
        service.sendSms(existingId);

        verify(repository, times(1)).getReferenceById(existingId);
    }
}
