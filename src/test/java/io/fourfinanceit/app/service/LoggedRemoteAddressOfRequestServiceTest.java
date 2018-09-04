package io.fourfinanceit.app.service;

import io.fourfinanceit.app.model.domain.LoggedRemoteAddressOfRequest;
import io.fourfinanceit.app.repository.LoggedRemoteAddressOfRequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class LoggedRemoteAddressOfRequestServiceTest {

    @InjectMocks
    private LoggedRemoteAddressOfRequestService remoteAddressService;

    @Mock
    private LoggedRemoteAddressOfRequestRepository remoteAddressRepository;

    private Timestamp deleteBefore;
    private List<LoggedRemoteAddressOfRequest> loggedRemoteAddresses;

    public static final String REMOTE_ADDRESS = "0.0.0.0.0";
    public static final String ENDPOINT_NAME = "CREATE_LOAN";

    @Before
    public void setUp() {
        Date currentDate = new Date();
        deleteBefore = new Timestamp(currentDate.getTime());

        LoggedRemoteAddressOfRequest loggedRemoteAddress = new LoggedRemoteAddressOfRequest();
        loggedRemoteAddresses = new ArrayList<>();
        loggedRemoteAddresses.add(loggedRemoteAddress);
        loggedRemoteAddresses.add(loggedRemoteAddress);
        loggedRemoteAddresses.add(loggedRemoteAddress);
        loggedRemoteAddresses.add(loggedRemoteAddress);
        loggedRemoteAddresses.add(loggedRemoteAddress);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeleteLoggedRemoteAddresses() {
        remoteAddressService.deleteLoggedRemoteAddresses(deleteBefore);

        verify(remoteAddressRepository, times(1)).deleteByCreatedAtBefore(any(Date.class));
    }

    @Test
    public void testCheckIfRequestLimitForRemoteAddressExceededWhenLimitExceeded() {
        when(remoteAddressRepository.findByRemoteAddressAndRequestedEndpointIsInAndAndCreatedAtAfter(
                anyString(),
                anyList(),
                any(Date.class))).thenReturn(loggedRemoteAddresses);

        boolean actual = remoteAddressService.checkIfRequestLimitForRemoteAddressExceeded(
                REMOTE_ADDRESS, Arrays.asList(ENDPOINT_NAME), 5, 3);

        assertTrue(actual);

        verify(remoteAddressRepository, times(1))
                .findByRemoteAddressAndRequestedEndpointIsInAndAndCreatedAtAfter(anyString(), anyList(), any(Date.class));
    }

    @Test
    public void testCheckIfRequestLimitForRemoteAddressExceededWhenLimitNotExceeded() {
        when(remoteAddressRepository.findByRemoteAddressAndRequestedEndpointIsInAndAndCreatedAtAfter(
                anyString(),
                anyList(),
                any(Date.class))).thenReturn(loggedRemoteAddresses);

        boolean actual = remoteAddressService.checkIfRequestLimitForRemoteAddressExceeded(
                REMOTE_ADDRESS, Arrays.asList(ENDPOINT_NAME), 5, 6);

        assertFalse(actual);

        verify(remoteAddressRepository, times(1))
                .findByRemoteAddressAndRequestedEndpointIsInAndAndCreatedAtAfter(anyString(), anyList(), any(Date.class));
    }


    @Test
    public void testLogRequestRemoteAddress() {
        remoteAddressService.logRequestRemoteAddress(REMOTE_ADDRESS, ENDPOINT_NAME);

        verify(remoteAddressRepository, times(1)).save(any(LoggedRemoteAddressOfRequest.class));
    }
}
