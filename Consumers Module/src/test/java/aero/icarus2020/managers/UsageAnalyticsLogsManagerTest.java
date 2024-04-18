package aero.icarus2020.managers;

import aero.icarus2020.models.UsageAnalyticsLogsModel;
import aero.icarus2020.repositories.UsageAnalyticsLogsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsageAnalyticsLogsManagerTest {

    @MockBean
    private UsageAnalyticsLogsRepository usageAnalyticsLogsRepository;

    @Autowired
    private UsageAnalyticsLogsManager usageAnalyticsLogsManager;

    /**
     * This test method checks that the storeLogs method will create and store correctly the log.
     */
    @Test
    public void storeLogsTest() {
        // mock usageAnalyticsLogsRepository
        when(usageAnalyticsLogsRepository.save(any(UsageAnalyticsLogsModel.class))).thenReturn(null);

        String eventType = "test";
        String meta = "";
        long eventId = usageAnalyticsLogsManager.storeLogs(eventType, meta);
        UsageAnalyticsLogsModel producedModel = usageAnalyticsLogsManager.getUsageAnalyticsLogsModel();
        UsageAnalyticsLogsModel usageAnalyticsLogsModel = new UsageAnalyticsLogsModel();
        usageAnalyticsLogsModel.setId(0);
        usageAnalyticsLogsModel.setCreatedOn(new Date());
        usageAnalyticsLogsModel.setEventType(eventType);
        usageAnalyticsLogsModel.setMeta(meta);

        assertEquals("The generated usage analytic log (event id)", usageAnalyticsLogsModel.getId(), eventId);
        assertEquals("The generated usage analytic log (event type)", usageAnalyticsLogsModel.getEventType(), producedModel.getEventType());
        assertEquals("The generated usage analytic log (meta)", usageAnalyticsLogsModel.getMeta(), producedModel.getMeta());
    }
}
