package io.fourfinanceit.app.repository;

import io.fourfinanceit.app.model.domain.LoggedRemoteAddressOfRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface LoggedRemoteAddressOfRequestRepository extends JpaRepository<LoggedRemoteAddressOfRequest, Long> {

    public List<LoggedRemoteAddressOfRequest> findByRemoteAddressAndRequestedEndpointIsInAndAndCreatedAtAfter(
            String remoteAddress,
            List<String> requestedEndpoints,
            Date createdAt
    );

    @Transactional
    public List<LoggedRemoteAddressOfRequest> deleteByCreatedAtBefore(Date createdAt);
}
