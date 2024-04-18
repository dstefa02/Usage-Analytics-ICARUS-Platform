package aero.icarus2020.controllers;

import aero.icarus2020.exception.ResourceNotFoundException;
import aero.icarus2020.exception.UnauthorizedException;
import aero.icarus2020.models.PreaggregatedStatisticsModel;
import aero.icarus2020.models.TimelineDataEvents;
import aero.icarus2020.repository.AssetLogsRepository;
import aero.icarus2020.repository.OrganizationLogsRepository;
import aero.icarus2020.repository.PreaggregatedStatisticsRepository;
import aero.icarus2020.repository.UsageAnalyticsLogsRepository;
import aero.icarus2020.services.AsyncService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.math.BigInteger;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UsageAnalyticsController.class)
public class UsageAnalyticsControllerTest {

    @MockBean
    private UsageAnalyticsLogsRepository usage_analytics_repo;
    @MockBean
    AssetLogsRepository asset_logs_repo;
    @MockBean
    OrganizationLogsRepository org_logs_repo;
    @MockBean
    PreaggregatedStatisticsRepository preaggregated_stats_repo;

    @MockBean
    private AsyncService service;

    @Autowired
    private MockMvc mvc;
    private static final Logger log = LoggerFactory.getLogger(UsageAnalyticsController.class);
    private String category_str;

    // testing token
    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW9yZ2lvdS5qb2FubmFAY3MudWN5LmFjLmN5Iiwicm9sZSI6Ik9SR0FOSVNBVElPTkFETUlOIiwidXNlcl9pZCI6NDAsIm9yZ2FuaXphdGlvbl9pZCI6MTMsImlzcyI6ImljYXJ1cy1iYWNrZW5kLWFwcCIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiVW5pdmVyc2l0eSBvZiBDeXBydXMiLCJleHAiOjE1OTgwODExNjIsImlhdCI6MTU5NzkwODM2MiwianRpIjoiZmM2Njg3MzktZWQyZi00ZjgwLTljOWYtYjlkZjdjYTkzMmE4In0.F-IGLwu_wzXWPx2AaNbontrTjQGqWs5UI2YIbWhOseg";
    private final Cookie cookie = new Cookie("auth_token", token);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Set up mock services
     */
    @Before
    public void setUp() {

        // mock AyncService methods that are used from UsageAnalyticsController.class
        when(service.checkToken(any(String.class))).thenReturn(CompletableFuture.completedFuture(true));
        when(service.checkAdminAuth(any(String.class))).thenReturn(CompletableFuture.completedFuture(true));
        when(service.getSocketConnections()).thenReturn(CompletableFuture.completedFuture(""));
        when(service.getICARUSCounts()).thenReturn(CompletableFuture.completedFuture("4"));
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(""));
        when(service.getICARUSUserDataAsset(any(String.class))).thenReturn(CompletableFuture.completedFuture(""));
        when(service.getICARUSAllOrganization()).thenReturn(CompletableFuture.completedFuture(""));
    }

    /**
     * This method tests that the method get_admin_count_stats will gain the needed information
     * from the apis and return the relevant statistic batches for the admin.
     *
     * @throws Exception, when the connection is interrupted
     */
    @Test
    public void get_admin_count_statsTest() throws Exception {

        List<PreaggregatedStatisticsModel> results_list_stats = new ArrayList<>();
        PreaggregatedStatisticsModel p = new PreaggregatedStatisticsModel();
        JSONObject stats_batch_1 = new JSONObject();
        p.setName("total_data_assets_requested");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_rejected_data_assets_requests");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_rejected_contracts");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_paid_contracts");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_updated_data_assets");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_completed_successfully_analytic_jobs");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_failed_analytic_jobs");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("now_pending_user_registrations");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("now_active_analytic_jobs");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("now_pending_contract_payment");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("now_pending_data_checkin_jobs");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("now_pending_org_registrations");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        p = new PreaggregatedStatisticsModel();
        p.setName("total_virtual_datasets");
        p.setValue(0);
        stats_batch_1.put(p.getName(), p.getValue());
        results_list_stats.add(p);
        when(preaggregated_stats_repo.findAll()).thenReturn(results_list_stats);

        JSONObject stats_batch_2 = new JSONObject();
        String socket_results = "[{room: smth, sockets: 1}]";
        stats_batch_2.put("now_active_connections", 1);
        stats_batch_2.put("now_active_users", 1);
        when(service.getSocketConnections()).thenReturn(CompletableFuture.completedFuture(socket_results));
        JSONObject stats_batch_3 = new JSONObject();
        String icarus_counts = "{datasets: 1, organizations: 2, users: 3}";
        stats_batch_3.put("total_data_assets", 1);
        stats_batch_3.put("total_organizations", 2);
        stats_batch_3.put("total_users", 3);
        when(service.getICARUSCounts()).thenReturn(CompletableFuture.completedFuture(icarus_counts));
        JSONObject stats_batch_4 = new JSONObject();
        JSONObject stats_batch_4_data = new JSONObject();
        String icarus_datasets = "[{status: Confidential}, {status: Private}, {status: Private}]";
        stats_batch_4_data.put("Confidential", 1);
        stats_batch_4_data.put("Private", 2);
        stats_batch_4_data.put("Public", 0);
        stats_batch_4.put("total_data_assets_per_visibility", stats_batch_4_data);
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(icarus_datasets));

        JSONObject return_values_json = new JSONObject();
        return_values_json.put("stats_batch_1", stats_batch_1);
        return_values_json.put("stats_batch_2", stats_batch_2);
        return_values_json.put("stats_batch_3", stats_batch_3);
        return_values_json.put("stats_batch_4", stats_batch_4);
        JsonNode res_node = mapper.readTree(return_values_json.toString());

        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());

        mvc.perform(get("/api/v1/usage-analytics/admin/count-stats")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_total_data_assets_per_category returns correctly the data that needs to return
     * (a table with the categories and the number of datasets that have that category)
     *
     * @throws Exception
     */
    @Test
    public void get_total_data_assets_per_categoryTest() throws Exception {
        JSONArray icarusDatasets = new JSONArray();
        JSONObject dataset = new JSONObject();
        JSONArray categories = new JSONArray();
        JSONObject category = new JSONObject();
        category.put("name", "test");
        categories.put(category);
        dataset.put("status", "Public");
        dataset.put("categories", categories);
        icarusDatasets.put(dataset);
        dataset = new JSONObject();
        categories = new JSONArray();
        category = new JSONObject();
        category.put("name", "test");
        categories.put(category);
        dataset.put("status", "Public");
        dataset.put("categories", categories);
        icarusDatasets.put(dataset);
        dataset = new JSONObject();
        categories = new JSONArray();
        category = new JSONObject();
        category.put("name", "test2");
        categories.put(category);
        dataset.put("status", "Public");
        dataset.put("categories", categories);
        icarusDatasets.put(dataset);
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(icarusDatasets.toString()));
        JSONObject return_values_json = new JSONObject();
        return_values_json.put("test", 2);
        return_values_json.put("test2", 1);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/data-assets-per-category")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_admin_timeline_data returns correctly the data that needs to return
     * (a timeline with the dates and counts that of the requested event)
     *
     * @throws Exception
     */
    @Test
    public void get_admin_timeline_dataTest() throws Exception {
        List<Object[]> counts_list = new ArrayList<>();
        when(usage_analytics_repo.getCountsByEventType(any(String.class))).thenReturn(counts_list);
        JSONArray return_values_json = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        return_values_json.put(jsonObject);
        Object[] counts = new Object[2];
        counts[0] = new Date(2020, 9, 23);
        counts[1] = new BigInteger("3");
        jsonObject.put("date", counts[0]);
        jsonObject.put("count", counts[1]);
        counts_list.add(counts);
        jsonObject = new JSONObject();
        return_values_json.put(jsonObject);
        counts = new Object[2];
        counts[0] = new Date(2020, 9, 24);
        counts[1] = new BigInteger("4");
        jsonObject.put("date", counts[0]);
        jsonObject.put("count", counts[1]);
        counts_list.add(counts);
        jsonObject = new JSONObject();
        return_values_json.put(jsonObject);
        counts = new Object[2];
        counts[0] = new Date(2020, 9, 25);
        counts[1] = new BigInteger("5");
        jsonObject.put("date", counts[0]);
        jsonObject.put("count", counts[1]);
        counts_list.add(counts);
        jsonObject = new JSONObject();
        return_values_json.put(jsonObject);
        counts = new Object[2];
        counts[0] = new Date(2020, 9, 26);
        counts[1] = new BigInteger("6");
        jsonObject.put("date", counts[0]);
        jsonObject.put("count", counts[1]);
        counts_list.add(counts);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/timeline?event_type=test")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_admin_timeline_active_users returns correctly the data that needs to return
     * (a table with the dates and the number of users that logged in on that date)
     *
     * @throws Exception
     */
    @Test
    public void get_admin_timeline_active_usersTest() throws Exception {
        List<Object[]> activeUsers = new ArrayList<>();
        Object[] entry = new Object[2];
        entry[0] = new Date(2020, 9, 26);
        entry[1] = 1;
        activeUsers.add(entry);
        entry = new Object[2];
        entry[0] = new Date(2020, 9, 26);
        entry[1] = 1;
        activeUsers.add(entry);
        entry = new Object[2];
        entry[0] = new Date(2020, 9, 26);
        entry[1] = 2;
        activeUsers.add(entry);
        entry = new Object[2];
        entry[0] = new Date(2020, 9, 27);
        entry[1] = 1;
        activeUsers.add(entry);
        when(usage_analytics_repo.getActiveUsers(any(String.class))).thenReturn(activeUsers.stream());
        JSONArray return_values_json = new JSONArray();
        JSONObject value = new JSONObject();
        value.put("date", String.valueOf(new Date(2020, 9, 26)));
        value.put("count", 2);
        return_values_json.put(value);
        value = new JSONObject();
        value.put("date", String.valueOf(new Date(2020, 9, 27)));
        value.put("count", 1);
        return_values_json.put(value);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/timeline-active-users")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_admin_data_assets_per_velocity returns correctly the data that needs to return
     * (a table with the datasets counts for each velocity)
     *
     * @throws Exception
     */
    @Test
    public void get_admin_data_assets_per_velocityTest() throws Exception {
        JSONArray icarusDatasets = new JSONArray();
        JSONObject metadata = new JSONObject();
        JSONObject velocity = new JSONObject();
        velocity.put("velocity", "Batch");
        metadata.put("metadata", velocity);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        velocity = new JSONObject();
        velocity.put("velocity", "Test");
        metadata = new JSONObject();
        metadata.put("metadata", velocity);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(icarusDatasets.toString()));
        JSONObject return_values_json = new JSONObject();
        return_values_json.put("Batch", 3);
        return_values_json.put("Test", 2);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-per-velocity")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_admin_data_assets_per_frequency returns correctly the data that needs to return
     * (a table with the datasets counts for each frequency)
     *
     * @throws Exception
     */
    @Test
    public void get_admin_data_assets_per_frequencyTest() throws Exception {
        JSONArray icarusDatasets = new JSONArray();
        JSONObject metadata = new JSONObject();
        JSONObject frequency = new JSONObject();
        frequency.put("frequency", "Hourly");
        metadata.put("metadata", frequency);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        frequency = new JSONObject();
        frequency.put("frequency", "Daily");
        metadata = new JSONObject();
        metadata.put("metadata", frequency);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(icarusDatasets.toString()));
        JSONObject return_values_json = new JSONObject();
        return_values_json.put("Hourly", 3);
        return_values_json.put("Daily", 2);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-per-frequency")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method data_asset_per_calculation_scheme returns correctly the data that needs to return
     * (a table with the datasets counts for each calculation scheme)
     *
     * @throws Exception
     */
    @Test
    public void data_asset_per_calculation_schemeTest() throws Exception {
        JSONArray icarusDatasets = new JSONArray();
        JSONObject metadata = new JSONObject();
        JSONObject calculationScheme = new JSONObject();
        calculationScheme.put("calculationScheme", "Request Dependent");
        metadata.put("metadata", calculationScheme);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        calculationScheme = new JSONObject();
        calculationScheme.put("calculationScheme", "Fixed per Row");
        metadata = new JSONObject();
        metadata.put("metadata", calculationScheme);
        icarusDatasets.put(metadata);
        icarusDatasets.put(metadata);
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(icarusDatasets.toString()));
        JSONObject return_values_json = new JSONObject();
        return_values_json.put("Request Dependent", 3);
        return_values_json.put("Fixed per Row", 2);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-per-calculation-scheme")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method data_asset_events returns correctly the data that needs to return
     * (a table with the event counts per dataset)
     *
     * @throws Exception
     */
    @Test
    public void data_asset_eventsTest() throws Exception {
        JSONArray datasets = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        datasets.put(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", 2);
        datasets.put(jsonObject);
        when(service.getICARUSUserDataAsset(any(String.class))).thenReturn(CompletableFuture.completedFuture(datasets.toString()));
        JSONObject return_values_json = new JSONObject();
        List<Object[]> userAssetEvents = new ArrayList<>();
        jsonObject = new JSONObject();
        jsonObject.put("ASSET_STARRED", 2);
        Object[] objects = new Object[2];
        objects[0] = "ASSET_STARRED";
        objects[1] = "2020/09/24";
        userAssetEvents.add(objects);
        objects = new Object[2];
        objects[0] = "ASSET_STARRED";
        objects[1] = "2020/09/25";
        userAssetEvents.add(objects);
        jsonObject.put("ASSET_UNSTARRED", 1);
        objects = new Object[2];
        objects[0] = "ASSET_UNSTARRED";
        objects[1] = "2020/09/25";
        userAssetEvents.add(objects);
        return_values_json.put("1", jsonObject);
        when(usage_analytics_repo.getUserAssetEvents(1)).thenReturn(userAssetEvents);
        userAssetEvents = new ArrayList<>();
        jsonObject = new JSONObject();
        jsonObject.put("REQUEST", 1);
        objects = new Object[2];
        objects[0] = "REQUEST";
        objects[1] = "2020/09/26";
        userAssetEvents.add(objects);
        return_values_json.put("2", jsonObject);
        when(usage_analytics_repo.getUserAssetEvents(2)).thenReturn(userAssetEvents);
        JsonNode res_node = mapper.readTree(return_values_json.toString());
        ResultMatcher expected = MockMvcResultMatchers.content().string(res_node.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-events")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method timeline_data_asset_events returns correctly the data that needs to return
     * (a table that contains the counts for each event type, for the requested dataset)
     *
     * @throws Exception
     */
    @Test
    public void timeline_data_asset_eventsTest() throws Exception {

        JSONArray assets = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("id", 1);
        assets.put(object);
        when(service.getICARUSUserDataAsset(any(String.class))).thenReturn(CompletableFuture.completedFuture(assets.toString()));

        JSONObject return_values = new JSONObject();
        JSONArray events = new JSONArray();
        List<Object[]> userAssetEvents = new ArrayList<>();
        Object[] objects = new Object[3];
        objects[0] = "ASSET_STARRED";
        objects[1] = new Date(2020, 9, 24);
        objects[2] = new BigInteger("3");
        JSONObject jsonTemp = new JSONObject();
        jsonTemp.put("date", (Date) objects[1]);
        jsonTemp.put("count", (BigInteger) objects[2]);
        events.put(jsonTemp);
        userAssetEvents.add(objects);
        objects = new Object[3];
        objects[0] = "ASSET_STARRED";
        objects[1] = new Date(2020, 9, 25);
        objects[2] = new BigInteger("3");
        jsonTemp = new JSONObject();
        jsonTemp.put("date", (Date) objects[1]);
        jsonTemp.put("count", (BigInteger) objects[2]);
        events.put(jsonTemp);
        return_values.put((String) objects[0], events);
        userAssetEvents.add(objects);
        objects = new Object[3];
        objects[0] = "ASSET_UNSTARRED";
        objects[1] = new Date(2020, 9, 25);
        objects[2] = new BigInteger("1");
        List<TimelineDataEvents> tmp = new ArrayList<>();
        tmp.add(new TimelineDataEvents((Date) objects[1], (BigInteger) objects[2]));
        return_values.put((String) objects[0], tmp);
        userAssetEvents.add(objects);
        when(usage_analytics_repo.getUserAssetEventsByDate(1)).thenReturn(userAssetEvents);

        ResultMatcher expected = MockMvcResultMatchers.content().string(return_values.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/timeline-data-asset-events?req_asset_id=1")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method timeline_all_data_asset_events returns correctly the data that needs to return
     * (a table that contains the counts for each event type for each date,
     * for all datasets that the user has access to)
     *
     * @throws Exception
     */
    @Test
    public void timeline_all_data_asset_eventsTest() throws Exception {

        JSONArray assets = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("id", 1);
        assets.put(object);
        object = new JSONObject();
        object.put("id", 2);
        assets.put(object);
        when(service.getICARUSUserDataAsset(any(String.class))).thenReturn(CompletableFuture.completedFuture(assets.toString()));

        JSONObject return_values = new JSONObject();
        List<Object[]> userAssetEvents = new ArrayList<>();
        Object[] objects = new Object[3];
        objects[0] = "ASSET_STARRED";
        objects[1] = new Date(2020, 9, 24);
        objects[2] = new BigInteger("3");
        JSONObject jsonTemp = new JSONObject();
        jsonTemp.put(objects[1].toString(), (BigInteger) objects[2]);
        userAssetEvents.add(objects);
        objects = new Object[3];
        objects[0] = "ASSET_STARRED";
        objects[1] = new Date(2020, 9, 25);
        objects[2] = new BigInteger("2");
        jsonTemp.put(objects[1].toString(), (BigInteger) objects[2]);
        return_values.put((String) objects[0], jsonTemp);
        userAssetEvents.add(objects);
        objects = new Object[3];
        objects[0] = "ASSET_UNSTARRED";
        objects[1] = new Date(2020, 9, 25);
        objects[2] = new BigInteger("1");
        return_values.put((String) objects[0], jsonTemp);
        userAssetEvents.add(objects);
        when(usage_analytics_repo.getUserAssetEventsByDate(1)).thenReturn(userAssetEvents);

        userAssetEvents = new ArrayList<>();
        objects = new Object[3];
        objects[0] = "ASSET_UNSTARRED";
        objects[1] = new Date(2020, 9, 25);
        objects[2] = new BigInteger("1");
        jsonTemp = new JSONObject();
        jsonTemp.put(objects[1].toString(), 2);
        return_values.put((String) objects[0], jsonTemp);
        userAssetEvents.add(objects);
        when(usage_analytics_repo.getUserAssetEventsByDate(2)).thenReturn(userAssetEvents);
        ResultMatcher expected = MockMvcResultMatchers.content().string(return_values.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/timeline-all-data-asset-events")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);

    }

    /**
     * This test checks that the method all_data_asset_events returns correctly the data that needs to return
     * (a table that contains the counts for each event type, for all datasets that the user has access to)
     *
     * @throws Exception
     */
    @Test
    public void all_data_asset_eventsTest() throws Exception {
        JSONArray assets = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("id", 1);
        assets.put(object);
        object = new JSONObject();
        object.put("id", 2);
        assets.put(object);
        when(service.getICARUSUserDataAsset(any(String.class))).thenReturn(CompletableFuture.completedFuture(assets.toString()));

        JSONObject return_values = new JSONObject();
        List<Object[]> userAssetEvents = new ArrayList<>();
        Object[] objects = new Object[2];
        objects[0] = "ASSET_STARRED";
        objects[1] = new Date(2020, 9, 24);
        userAssetEvents.add(objects);
        objects = new Object[2];
        objects[0] = "ASSET_STARRED";
        objects[1] = new Date(2020, 9, 25);
        return_values.put(objects[0].toString(), 2);
        userAssetEvents.add(objects);
        objects = new Object[2];
        objects[0] = "ASSET_UNSTARRED";
        objects[1] = new Date(2020, 9, 25);
        userAssetEvents.add(objects);
        when(usage_analytics_repo.getUserAssetEvents(1)).thenReturn(userAssetEvents);

        userAssetEvents = new ArrayList<>();
        objects = new Object[2];
        objects[0] = "ASSET_UNSTARRED";
        objects[1] = new Date(2020, 9, 25);
        return_values.put(objects[0].toString(), 2);
        userAssetEvents.add(objects);
        when(usage_analytics_repo.getUserAssetEvents(2)).thenReturn(userAssetEvents);
        ResultMatcher expected = MockMvcResultMatchers.content().string(return_values.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/user-data-asset-events")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_category_preferences returns correctly the data that needs to return
     * (a table that contains the counts of the organizations that have as a preference a category).
     *
     * @throws Exception
     */
    @Test
    public void get_category_preferencesTest() throws Exception {
        JSONArray organizations = new JSONArray();
        JSONObject organization = new JSONObject();
        JSONArray categories = new JSONArray();
        JSONObject category = new JSONObject();
        category.put("name", "test");
        categories.put(category);
        category = new JSONObject();
        category.put("name", "test2");
        categories.put(category);
        organization.put("categories", categories);
        organizations.put(organization);

        organization = new JSONObject();
        categories = new JSONArray();
        category = new JSONObject();
        category.put("name", "test3");
        categories.put(category);
        category = new JSONObject();
        category.put("name", "test2");
        categories.put(category);
        organization.put("categories", categories);
        organizations.put(organization);
        when(service.getICARUSAllOrganization()).thenReturn(CompletableFuture.completedFuture(organizations.toString()));
        JSONObject return_values = new JSONObject();
        return_values.put("test", 1);
        return_values.put("test2", 2);
        return_values.put("test3", 1);
        ResultMatcher expected = MockMvcResultMatchers.content().string(return_values.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/category-preferences")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method organization_per_country returns correctly the data that needs to return
     * (a table that contains the counts of the organizations in each country).
     *
     * @throws Exception
     */
    @Test
    public void organization_per_countryTest() throws Exception {
        JSONArray organizations = new JSONArray();
        JSONObject organization = new JSONObject();
        organization.put("country", "Cyprus");
        organizations.put(organization);

        organization = new JSONObject();
        organization.put("country", "Cyprus");
        organizations.put(organization);

        organization = new JSONObject();
        organization.put("country", "Greece");
        organizations.put(organization);
        when(service.getICARUSAllOrganization()).thenReturn(CompletableFuture.completedFuture(organizations.toString()));
        JSONObject return_values = new JSONObject();
        return_values.put("Cyprus", 2);
        return_values.put("Greece", 1);
        ResultMatcher expected = MockMvcResultMatchers.content().string(return_values.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/organization-per-country")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This test checks that the method get_popular_category_dataset returns correctly the data that needs to return
     * (a table that contains the counts of categories for each public dataset).
     * the event type can be either for views or contracts for each dataset.
     *
     * @throws Exception
     */
    @Test
    public void get_popular_category_datasetTest() throws Exception {
        List<String> assets = new ArrayList<>();
        assets.add("1");
        assets.add("1");
        assets.add("2");
        when(usage_analytics_repo.getSpecificEvents(any(String.class))).thenReturn(assets);
        JSONArray datasets = new JSONArray();
        JSONObject dataset = new JSONObject();
        JSONArray categories = new JSONArray();
        JSONObject category = new JSONObject();
        category.put("name", "test");
        categories.put(category);
        category = new JSONObject();
        category.put("name", "test2");
        categories.put(category);
        dataset.put("categories", categories);
        dataset.put("id", 1);
        dataset.put("status", "Public");
        datasets.put(dataset);

        dataset = new JSONObject();
        categories = new JSONArray();
        category = new JSONObject();
        category.put("name", "test2");
        categories.put(category);
        dataset.put("categories", categories);
        dataset.put("id", 2);
        dataset.put("status", "Public");
        datasets.put(dataset);
        when(service.getICARUSAllDataAsset()).thenReturn(CompletableFuture.completedFuture(datasets.toString()));

        JSONObject return_values = new JSONObject();
        JSONArray popular_datasets = new JSONArray();
        JSONObject popular_dataset = new JSONObject();
        popular_dataset.put("assetId", "1");
        popular_dataset.put("count", 2);
        popular_datasets.put(popular_dataset);
        popular_dataset = new JSONObject();
        popular_dataset.put("assetId", "2");
        popular_dataset.put("count", 1);
        popular_datasets.put(popular_dataset);
        return_values.put("popular_datasets", popular_datasets);

        JSONArray popular_categories = new JSONArray();
        JSONObject popular_category = new JSONObject();
        popular_category.put("category", "test2");
        popular_category.put("count", 3);
        popular_categories.put(popular_category);
        popular_category = new JSONObject();
        popular_category.put("category", "test");
        popular_category.put("count", 2);
        popular_categories.put(popular_category);
        return_values.put("popular_categories", popular_categories);
        ResultMatcher expected = MockMvcResultMatchers.content().string(return_values.toString());
        mvc.perform(get("/api/v1/usage-analytics/admin/popular-category-datasets?event_type=ASSET_VIEWED")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(expected);
    }

    /**
     * This method checks that all usageAnalyticsController methods are checking the token and raise an exception when
     * the token is not valid (does not exist in this case).
     **/
    @Test
    public void UnauthorizedExceptionTest() {

        when(service.checkToken(any(String.class))).thenReturn(CompletableFuture.completedFuture(false));
        when(service.checkAdminAuth(any(String.class))).thenReturn(CompletableFuture.completedFuture(false));

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/count-stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/data-assets-per-category")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/timeline")
                    .param("event_type", "test")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/timeline-active-users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-per-velocity")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-per-frequency")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-per-calculation-scheme")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/data-asset-events")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/timeline-data-asset-events")
                    .param("req_asset_id", "2")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/timeline-all-data-asset-events")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/user-data-asset-events")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/category-preferences")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/organization-per-country")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/popular-category-datasets")
                    .param("event_type", "test")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Unauthorized"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks that all usageAnalyticsController methods that require a param will raise an error when
     * they do not get one.
     **/
    @Test
    public void missingParamException() {
        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/timeline")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/timeline-data-asset-events")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Missing cookie 'auth_token' for method parameter of type String"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mvc.perform(get("/api/v1/usage-analytics/admin/popular-category-datasets")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Unauthorized"));
        } catch (ResourceNotFoundException | InterruptedException | ExecutionException | UnauthorizedException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

