package aero.icarus2020.managers;

import aero.icarus2020.models.UsageAnalyticsLogsModel;
import aero.icarus2020.repositories.UsageAnalyticsLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsageAnalyticsLogsManager {

    @Autowired
    private UsageAnalyticsLogsRepository usageAnalyticsLogsRepository;

    private UsageAnalyticsLogsModel usageAnalyticsLogsModel;

    public long storeLogs(String event_type, String meta_json) {
        // Create new logs and store them in the "usageanalyticslogs" table
        usageAnalyticsLogsModel = new UsageAnalyticsLogsModel();
        usageAnalyticsLogsModel.setEventType(event_type);
        usageAnalyticsLogsModel.setMeta(meta_json);
        this.usageAnalyticsLogsRepository.save(usageAnalyticsLogsModel);

        return usageAnalyticsLogsModel.getId();
    }

    public UsageAnalyticsLogsRepository getUsageAnalyticsLogsRepository() {
        return usageAnalyticsLogsRepository;
    }

    public UsageAnalyticsLogsModel getUsageAnalyticsLogsModel() {
        return usageAnalyticsLogsModel;
    }
}
