package aero.icarus2020.managers;

import aero.icarus2020.models.PreaggregatedStatisticsModel;
import aero.icarus2020.repositories.PreaggregatedStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class PreaggregatedStatsManager {

    @Autowired
    private PreaggregatedStatisticsRepository preaggregatedStatisticsRepository;

    public HashMap<String, PreaggregatedStatisticsModel> getAllStats() {
        // Retrieve all pre-aggregated statistics
        Iterable<PreaggregatedStatisticsModel> results_list_stats = preaggregatedStatisticsRepository.findAll();
        HashMap<String, PreaggregatedStatisticsModel> retrieved_stats = new HashMap<String, PreaggregatedStatisticsModel>();

        for (PreaggregatedStatisticsModel s : results_list_stats) {
            retrieved_stats.put(s.getMetricName(), s);
        }

        // List with all predefined names of the preaggregated metrics
        List<String> metrics_list = Arrays.asList(
                "total_data_assets_requested", "total_rejected_data_assets_requests",
                "total_rejected_contracts", "total_paid_contracts", "total_updated_data_assets",
                "total_completed_successfully_analytic_jobs", "total_failed_analytic_jobs",
                "now_pending_org_registrations", "now_pending_user_registrations",
                "now_pending_data_checkin_jobs",
                "now_active_analytic_jobs", "now_pending_contract_payment",
                "total_virtual_datasets"
        );

        // Check if a predefined metric does not exist in the database
        for (String metric_s : metrics_list) {
            if (!retrieved_stats.containsKey(metric_s)) {
                PreaggregatedStatisticsModel stats_model = new PreaggregatedStatisticsModel();
                stats_model.setMetricName(metric_s);
                stats_model.setMetricValue(0);
                retrieved_stats.put(metric_s, stats_model);
                this.updateStat(retrieved_stats.get(metric_s), 0);
            }
        }

        return retrieved_stats;
    }

    public void updateStat(PreaggregatedStatisticsModel preaggregatedStatisticsModel, int shift_value) {
        if (preaggregatedStatisticsModel != null) {
            preaggregatedStatisticsModel.updateMetricValue(shift_value);
            preaggregatedStatisticsRepository.save(preaggregatedStatisticsModel);
        }
    }

    public PreaggregatedStatisticsRepository getPreaggregatedStatisticsRepository() {
        return preaggregatedStatisticsRepository;
    }
}
