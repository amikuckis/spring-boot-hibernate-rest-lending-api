package io.fourfinanceit.app.scheduled;

import io.fourfinanceit.app.service.LoggedRemoteAddressOfRequestService;
import io.fourfinanceit.app.utils.MyAppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(LoggedRemoteAddressOfRequestService.class);

    private static final int REMOTE_ADDRESS_DELETE_PERIOD = 2 * 24 * 60 * 1000;

    @Autowired
    LoggedRemoteAddressOfRequestService loggedRemoteAddressOfRequestService;

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledDeleteLoggedRemoteAddresses() {
        Timestamp deleteBefore = new Timestamp(System.currentTimeMillis() - REMOTE_ADDRESS_DELETE_PERIOD);
        loggedRemoteAddressOfRequestService.deleteLoggedRemoteAddresses(deleteBefore);
        logger.info("Scheduled logged remote address cleaning performed for logs before: '{}'", deleteBefore);
    }

}
