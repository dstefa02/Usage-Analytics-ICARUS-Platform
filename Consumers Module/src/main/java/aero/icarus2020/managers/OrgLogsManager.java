package aero.icarus2020.managers;

import aero.icarus2020.models.OrganizationLogsModel;
import aero.icarus2020.repositories.OrganizationLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrgLogsManager {

    @Autowired
    private OrganizationLogsRepository organizationLogsRepository;

    private OrganizationLogsModel organizationLogsModel;

    public void storeLogs(long org_id, long event_id) {
        // Store organization logs
        organizationLogsModel = new OrganizationLogsModel();
        organizationLogsModel.setOrganizationId(org_id);
        organizationLogsModel.setEventId(event_id);
        this.organizationLogsRepository.save(organizationLogsModel);
    }

    public OrganizationLogsRepository getOrganizationLogsRepository() {
        return organizationLogsRepository;
    }

    public OrganizationLogsModel getOrganizationLogsModel() {
        return organizationLogsModel;
    }
}
