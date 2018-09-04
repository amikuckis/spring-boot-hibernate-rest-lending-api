package io.fourfinanceit.app.service;

import io.fourfinanceit.app.model.domain.LoggedRemoteAddressOfRequest;
import io.fourfinanceit.app.repository.LoggedRemoteAddressOfRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class LoggedRemoteAddressOfRequestService {

    @Autowired
    private LoggedRemoteAddressOfRequestRepository loggedRemoteAddressOfRequestRepository;

    public void deleteLoggedRemoteAddresses(Timestamp deleteBefore) {
        loggedRemoteAddressOfRequestRepository.deleteByCreatedAtBefore(deleteBefore);
    }

    public boolean checkIfRequestLimitForRemoteAddressExceeded(String remoteAddress,
                                                               List<String> requestedEndpoints,
                                                               int hourLimit,
                                                               int requestLimit) {

        Timestamp checkStartTimestamp = new Timestamp(System.currentTimeMillis() - (hourLimit));

        int resultSize = loggedRemoteAddressOfRequestRepository
                .findByRemoteAddressAndRequestedEndpointIsInAndAndCreatedAtAfter(
                        remoteAddress,
                        requestedEndpoints,
                        checkStartTimestamp
                ).size();

        return resultSize >= requestLimit;
    }

    public void logRequestRemoteAddress(String remoteAddress, String requestedEndpoint) {

        LoggedRemoteAddressOfRequest address = new LoggedRemoteAddressOfRequest();
        address.setRemoteAddress(remoteAddress);
        address.setRequestedEndpoint(requestedEndpoint);
        loggedRemoteAddressOfRequestRepository.save(address);
    }
}
