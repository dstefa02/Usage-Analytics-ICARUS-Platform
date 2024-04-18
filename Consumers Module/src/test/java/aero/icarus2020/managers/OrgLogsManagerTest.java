package aero.icarus2020.managers;

import aero.icarus2020.models.OrganizationLogsModel;
import aero.icarus2020.repositories.OrganizationLogsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrgLogsManagerTest {

    @MockBean
    private OrganizationLogsRepository organizationLogsRepository;

    @Autowired
    private OrgLogsManager orgLogsManager;

    /**
     * This test method checks that the storeLogs method will create and store correctly the log.
     */
    @Test
    public void storeLogsTest() {
        // mock organizationLogsRepository
        when(organizationLogsRepository.save(any(OrganizationLogsModel.class))).thenReturn(null);

        long orgId = 0;
        long eventId = 1;
        orgLogsManager.storeLogs(orgId, eventId);
        OrganizationLogsModel producedModel = orgLogsManager.getOrganizationLogsModel();
        OrganizationLogsModel organizationLogsModel = new OrganizationLogsModel();
        organizationLogsModel.setOrganizationId(orgId);
        organizationLogsModel.setEventId(eventId);
        assertEquals("The generated organization log (organizations id)", organizationLogsModel.getOrganizationId(), producedModel.getOrganizationId());
        assertEquals("The generated organization log (event id)", organizationLogsModel.getEventId(), producedModel.getEventId());
    }
}
