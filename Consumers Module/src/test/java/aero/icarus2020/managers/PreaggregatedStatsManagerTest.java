package aero.icarus2020.managers;

import aero.icarus2020.models.AssetLogsModel;
import aero.icarus2020.models.PreaggregatedStatisticsModel;
import aero.icarus2020.repositories.PreaggregatedStatisticsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreaggregatedStatsManagerTest {

    @MockBean
    private PreaggregatedStatisticsRepository preaggregatedStatisticsRepository;

    @Autowired
    private PreaggregatedStatsManager preaggregatedStatsManager;

    /**
     * This test method checks that the getAllStats method will retrieve and return all of the preaggregated statistics.
     */
    @Test
    public void getAllStatsTest() {

        List<String> metrics_list = Arrays.asList(
                "total_data_assets_requested", "total_rejected_data_assets_requests",
                "total_rejected_contracts", "total_paid_contracts", "total_updated_data_assets",
                "total_completed_successfully_analytic_jobs", "total_failed_analytic_jobs",
                "now_pending_org_registrations", "now_pending_user_registrations",
                "now_pending_data_checkin_jobs",
                "now_active_analytic_jobs", "now_pending_contract_payment",
                "total_virtual_datasets"
        );

        // mock assetLogsRepository
        List<PreaggregatedStatisticsModel> preaggregatedStatisticsModelList = new ArrayList<>();
        PreaggregatedStatisticsModel preaggregatedStatisticsModel;
        for (int i = 0; i < metrics_list.size(); i++) {
            preaggregatedStatisticsModel = new PreaggregatedStatisticsModel();
            preaggregatedStatisticsModel.setMetricId(i);
            preaggregatedStatisticsModel.setMetricName(metrics_list.get(i));
            preaggregatedStatisticsModel.setMetricValue(i + 1);
        }
        when(preaggregatedStatisticsRepository.findAll()).thenReturn(preaggregatedStatisticsModelList);
        when(preaggregatedStatisticsRepository.save(any(PreaggregatedStatisticsModel.class))).thenReturn(null);

        HashMap<String, PreaggregatedStatisticsModel> result = preaggregatedStatsManager.getAllStats();
        System.out.println(result.toString());
        assertEquals("Result size", 13, result.size());
        preaggregatedStatisticsModelList.forEach(preaggregatedStatisticsModel1 -> {
            String metricName = preaggregatedStatisticsModel1.getMetricName();
            assertEquals("entry metric value", preaggregatedStatisticsModel1.getMetricValue(), result.get(metricName).getMetricValue());
            assertEquals("entry metric name", metricName, result.get(metricName).getMetricName());
            assertEquals("entry metric id", preaggregatedStatisticsModel1.getMetricId(), result.get(metricName).getMetricId());
        });

    }
}
