package aero.icarus2020.controllers;

import aero.icarus2020.exception.UnauthorizedException;
import aero.icarus2020.models.PreaggregatedStatisticsModel;
import aero.icarus2020.models.TimelineDataEvents;
import aero.icarus2020.repository.*;
import aero.icarus2020.services.AsyncService;
import aero.icarus2020.util.ReturnEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@CrossOrigin()
@RestController
@RequestMapping("/api/v1")
public class UsageAnalyticsController {

    @Autowired
    UsageAnalyticsLogsRepository usage_analytics_repo;
    @Autowired
    AssetLogsRepository asset_logs_repo;
    @Autowired
    OrganizationLogsRepository org_logs_repo;
    @Autowired
    PreaggregatedStatisticsRepository preaggregated_stats_repo;
    @Autowired
    private AsyncService service;

    private static final Logger log = LoggerFactory.getLogger(UsageAnalyticsController.class);
    private String category_str;

    @GetMapping("/usage-analytics/admin/count-stats")
    public JsonNode get_admin_count_stats(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, IOException, UnauthorizedException {

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);

        // Get all active socket connections e.g. [{"room":"user.4","sockets":2},{"room":"user.0","sockets":1}]
        CompletableFuture<String> async_socket_results = service.getSocketConnections();

        // Get platform general counts e.g. {"datasets":34,"organizations":8,"users":23}
        CompletableFuture<String> async_count_results = service.getICARUSCounts();

        // Get for all data assets their metadata like categories and visibility
        // e.g. [{"id":1, "status":"Public", "categories": [{"id":2,"name":"Airport"},{"id":5,"name":"Flight"}]}, {}]
        CompletableFuture<String> async_all_data_results = service.getICARUSAllDataAsset();

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken, async_count_results, async_all_data_results, async_socket_results).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Final JSON object to return
        JSONObject return_values_json = new JSONObject();

        // Stats batch for preaggregated statistics from Database
        JSONObject stats_batch_1 = new JSONObject();
        // Stats batch for socket connections
        JSONObject stats_batch_2 = new JSONObject();
        // Stats batch for platform general counts e.g. total datasets
        JSONObject stats_batch_3 = new JSONObject();
        // Stats batch for metadata of data assets e.g. categories
        JSONObject stats_batch_4 = new JSONObject();

        // Retrieve preaggregated statistics from Database - Stats batch 1
        try {
            Iterable<PreaggregatedStatisticsModel> results_list_stats = preaggregated_stats_repo.findAll();
            for (PreaggregatedStatisticsModel s : results_list_stats) {
                stats_batch_1.put(s.getName(), s.getValue());
            }
        } catch (Exception e) {
            log.error("Stats batch for preaggregated statistics from Database: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        } finally {
            return_values_json.put("stats_batch_1", stats_batch_1);
        }

        // Calculate current total active connections and total active users  - Stats batch 2
        try {
            JSONArray sockets_array = new JSONArray(async_socket_results.get());
            int now_active_connections = 0;
            int now_active_users = sockets_array.length();

            for (int i = 0; i < sockets_array.length(); i++) {
                JSONObject socket_obj = sockets_array.getJSONObject(i);
                now_active_connections += socket_obj.getInt("sockets");
            }

            stats_batch_2.put("now_active_connections", now_active_connections);
            stats_batch_2.put("now_active_users", now_active_users);
        } catch (Exception e) {
            log.error("Stats batch for socket connections: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        } finally {
            return_values_json.put("stats_batch_2", stats_batch_2);
        }

        // Platform general counts  - Stats batch 3
        try {
            JSONObject count_results_json = new JSONObject(async_count_results.get());
            stats_batch_3.put("total_data_assets", count_results_json.getLong("datasets"));
            stats_batch_3.put("total_organizations", count_results_json.getLong("organizations"));
            stats_batch_3.put("total_users", count_results_json.getLong("users"));
        } catch (Exception e) {
            log.error("Stats batch for platform general counts: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        } finally {
            return_values_json.put("stats_batch_3", stats_batch_3);
        }

        // Stats batch for metadata of data assets  - Stats batch 4
        try {
            JSONArray datasets_meta = new JSONArray(async_all_data_results.get());
            JSONObject data_asset_visibility = new JSONObject();
            data_asset_visibility.put("Confidential", (long) 0);
            data_asset_visibility.put("Private", (long) 0);
            data_asset_visibility.put("Public", (long) 0);

            for (int i = 0; i < datasets_meta.length(); i++) {
                JSONObject data_asset_obj = datasets_meta.getJSONObject(i);

                if (data_asset_obj.has("status")) {
                    String visibility = data_asset_obj.optString("status", "");
                    switch (visibility) {
                        case "Confidential":
                            data_asset_visibility.put("Confidential", data_asset_visibility.getLong("Confidential") + 1);
                            break;
                        case "Private":
                            data_asset_visibility.put("Private", data_asset_visibility.getLong("Private") + 1);
                            break;
                        case "Public":
                            data_asset_visibility.put("Public", data_asset_visibility.getLong("Public") + 1);
                            break;
                    }
                }
            }

            stats_batch_4.put("total_data_assets_per_visibility", data_asset_visibility);
        } catch (Exception e) {
            log.error("Stats batch for metadata of data assets: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        } finally {
            return_values_json.put("stats_batch_4", stats_batch_4);
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(return_values_json.toString());
    }

    @GetMapping("/usage-analytics/admin/data-assets-per-category")
    @Transactional(readOnly = true)
    public HashMap<String, Long> get_total_data_assets_per_category(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<String, Long> total_data_assets_per_category = new HashMap<>();

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        asyncCheckToken = service.checkAdminAuth(token);

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();

        boolean admin = true;
        // If not authorized as admin
        if (!asyncCheckToken.get())
            admin = false;

        try {
            CompletableFuture<String> async_all_data_results = service.getICARUSAllDataAsset();
            JSONArray datasets_meta = new JSONArray(async_all_data_results.get());

            for (int i = 0; i < datasets_meta.length(); i++) {
                JSONObject data_asset_obj = datasets_meta.getJSONObject(i);
                if (admin || (data_asset_obj.has("status") && data_asset_obj.getString("status").compareToIgnoreCase("Confidential") != 0))
                    if (data_asset_obj.has("categories")) {
                        JSONArray categories = data_asset_obj.getJSONArray("categories");

                        for (int j = 0; j < categories.length(); j++) {
                            JSONObject category_obj = categories.getJSONObject(j);
                            String category_str = category_obj.getString("name");

                            if (!total_data_assets_per_category.containsKey(category_str)) {
                                total_data_assets_per_category.put(category_str, (long) 0);
                            }
                            total_data_assets_per_category.put(category_str, total_data_assets_per_category.get(category_str) + 1);
                        }

                    }
            }

        } catch (Exception e) {
            log.error("total_data_assets_per_category: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        }

        return total_data_assets_per_category;
    }

    @GetMapping("/usage-analytics/admin/timeline")
    @Transactional(readOnly = true)
    public ArrayList<ReturnEntity> get_admin_timeline_data(
            @CookieValue(value = "auth_token", defaultValue = "") String token,
            @RequestParam String event_type)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        ArrayList<ReturnEntity> results_timeline = new ArrayList<>();

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Retrieve records from database
        try {
            List<Object[]> query_results = usage_analytics_repo.getCountsByEventType(event_type);

            query_results.forEach(obj_arr -> {
                results_timeline.add(new ReturnEntity((Date) obj_arr[0], (BigInteger) obj_arr[1]));
            });

        } catch (Exception e) {
            log.error("get_admin_timeline_data: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        }
        // Sort dates
        results_timeline.sort((o1, o2) -> o1.date.compareTo(o2.date));

        return results_timeline;
    }


    @GetMapping("/usage-analytics/admin/timeline-active-users")
    @Transactional(readOnly = true)
    public ArrayList<ReturnEntity> get_admin_timeline_active_users(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        ArrayList<ReturnEntity> results_timeline = new ArrayList<>();

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }


        // Retrieve records from database
        try {
            HashMap<Date, HashSet<String>> users_per_date = new HashMap<>();

            try (Stream<Object[]> stream = usage_analytics_repo.getActiveUsers("USER_CONNECT")) {
                stream.forEach(obj_arr -> {
                    try {
                        Date date = (Date) obj_arr[0];
                        String user_id = obj_arr[1].toString();

                        HashSet<String> users_set;

                        if (!users_per_date.containsKey(date)) {
                            users_set = new HashSet<>();
                        } else {
                            users_set = users_per_date.get(date);
                        }

                        users_set.add(user_id);
                        users_per_date.put(date, users_set);

                    } catch (Exception e) {
                        // log.error("A field is missing (e.g. user_id): EXCEPTION..." + e.toString());
                    }

                });
            }

            for (Map.Entry<Date, HashSet<String>> entry : users_per_date.entrySet()) {
                Date date = entry.getKey();
                HashSet<String> users_set = entry.getValue();
                results_timeline.add(new ReturnEntity(date, BigInteger.valueOf(users_set.size())));
            }
            // Sort dates
            results_timeline.sort((o1, o2) -> o1.date.compareTo(o2.date));

        } catch (Exception e) {
            log.error("get_admin_timeline_data: EXCEPTION..." + e.toString());
        }

        return results_timeline;
    }

    @GetMapping("/usage-analytics/admin/data-asset-per-velocity")
    @Transactional(readOnly = true)
    public HashMap<String, Integer> get_admin_data_assets_per_velocity(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<String, Integer> results = new HashMap<>();

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSAllDataAsset();

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            dataAssetArray.forEach(assetObj -> {
                JSONObject dataAsset = (JSONObject) assetObj;
                if (dataAsset.has("metadata") && dataAsset.getJSONObject("metadata").has("velocity")) {
                    if (dataAsset.getJSONObject("metadata").get("velocity") instanceof String) {
                        String category = dataAsset.getJSONObject("metadata").getString("velocity");
                        if (results.containsKey(category)) {
                            results.put(category, (results.get(category) + 1));
                        } else {
                            results.put(category, 1);
                        }
                    }
                }

            });
        } catch (JSONException err) {
//            Log.d("Error", err.toString());
        }


        return results;
    }

    @GetMapping("/usage-analytics/admin/data-asset-per-frequency")
    @Transactional(readOnly = true)
    public HashMap<String, Integer> get_admin_data_assets_per_frequency(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<String, Integer> results = new HashMap<>();

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSAllDataAsset();

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            dataAssetArray.forEach(assetObj -> {
                JSONObject dataAsset = (JSONObject) assetObj;

                if (dataAsset.has("metadata") && dataAsset.getJSONObject("metadata").has("frequency")) {
                    if (dataAsset.getJSONObject("metadata").get("frequency") instanceof String) {
                        String category = dataAsset.getJSONObject("metadata").getString("frequency");
                        if (results.containsKey(category)) {
                            results.put(category, (results.get(category) + 1));
                        } else {
                            results.put(category, 1);
                        }
                    }
                }
            });
        } catch (JSONException err) {
//            Log.d("Error", err.toString());
        }


        return results;
    }

    @GetMapping("/usage-analytics/admin/data-asset-per-calculation-scheme")
    @Transactional(readOnly = true)
    public HashMap<String, Integer> data_asset_per_calculation_scheme(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<String, Integer> results = new HashMap<>();

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSAllDataAsset();

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            dataAssetArray.forEach(assetObj -> {
                JSONObject dataAsset = (JSONObject) assetObj;
                if (dataAsset.has("metadata") && dataAsset.getJSONObject("metadata").has("calculationScheme")) {
                    if (dataAsset.getJSONObject("metadata").get("calculationScheme") instanceof String) {
                        String category = dataAsset.getJSONObject("metadata").getString("calculationScheme");
                        if (results.containsKey(category)) {
                            results.put(category, (results.get(category) + 1));
                        } else {
                            results.put(category, 1);
                        }
                    }

                }

            });
        } catch (JSONException err) {
            System.out.println("Error: " + err.toString());
        }
        return results;
    }

    @GetMapping("/usage-analytics/admin/data-asset-events")
    @Transactional(readOnly = true)
    public HashMap<Integer, HashMap<String, Integer>> data_asset_events(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<Integer, HashMap<String, Integer>> results = new HashMap<>();

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSUserDataAsset(token);


        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            dataAssetArray.forEach(assetObj -> {
                JSONObject dataAsset = (JSONObject) assetObj;
                if (dataAsset.has("id")) {
                    Object asset_id = dataAsset.get("id");
                    try {
                        List<Object[]> query_results = usage_analytics_repo.getUserAssetEvents((Integer) asset_id);
                        HashMap<String, Integer> perDatasset = new HashMap<>();
                        query_results.forEach(obj_arr -> {
                            if (perDatasset.containsKey((String) obj_arr[0])) {
                                perDatasset.put((String) obj_arr[0], perDatasset.get((String) obj_arr[0]) + 1);
                            } else {
                                perDatasset.put((String) obj_arr[0], 1);
                            }
                        });
                        results.put((Integer) asset_id, perDatasset);
                    } catch (Exception e) {
                        log.error("get_admin_timeline_data: EXCEPTION..." + e.toString());
                        //e.printStackTrace();
                    }

                }

            });
        } catch (JSONException err) {
            System.out.println("Error: " + err.toString());
        }

        System.out.println(results);
        return results;
    }

    @GetMapping("/usage-analytics/admin/timeline-data-asset-events")
    @Transactional(readOnly = true)
    public HashMap<String, List<TimelineDataEvents>> timeline_data_asset_events(
            @CookieValue(value = "auth_token", defaultValue = "") String token,
            @RequestParam Integer req_asset_id)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSUserDataAsset(token);

        HashMap<String, List<TimelineDataEvents>> results_timeline = new HashMap<>();

        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Retrieve records from database
        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            ArrayList<Integer> listdata = new ArrayList<>();
            if (dataAssetArray != null) {
                for (int i = 0; i < dataAssetArray.length(); i++) {
                    listdata.add((Integer) dataAssetArray.getJSONObject(i).get("id"));
                }
            }
            if (listdata.contains(req_asset_id)) {
                try {
                    List<Object[]> query_results = usage_analytics_repo.getUserAssetEventsByDate(req_asset_id);
                    query_results.forEach(rec_arr -> {
                        if (results_timeline.containsKey((String) rec_arr[0])) {
                            results_timeline.get((String) rec_arr[0]).add(new TimelineDataEvents((Date) rec_arr[1], (BigInteger) rec_arr[2]));
                        } else {
                            List<TimelineDataEvents> tmp = new ArrayList<>();
                            tmp.add(new TimelineDataEvents((Date) rec_arr[1], (BigInteger) rec_arr[2]));
                            results_timeline.put((String) rec_arr[0], tmp);
                        }
                    });
                } catch (Exception e) {
                    log.error("Timeline asset event EXCEPTION: " + e.toString());
                    //e.printStackTrace();
                }
            } else {
                throw new UnauthorizedException("Unauthorized");
            }
        } catch (JSONException err) {
            System.out.println("Error: " + err.toString());
        }

        return results_timeline;
    }


    @GetMapping("/usage-analytics/admin/timeline-all-data-asset-events")
    @Transactional(readOnly = true)
    public HashMap<String, TreeMap<String, BigInteger>> timeline_all_data_asset_events(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSUserDataAsset(token);
        HashMap<String, TreeMap<String, BigInteger>> results_timeline = new HashMap<>();

        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Retrieve records from database
        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            dataAssetArray.forEach(assetObj -> {
                JSONObject dataAsset = (JSONObject) assetObj;
                if (dataAsset.has("id")) {
                    Object asset_id = dataAsset.get("id");
                    try {
                        List<Object[]> query_results = usage_analytics_repo.getUserAssetEventsByDate((Integer) asset_id);
                        query_results.forEach(rec_arr -> {
                            if (results_timeline.containsKey((String) rec_arr[0])) {
                                if (results_timeline.get((String) rec_arr[0]).containsKey(rec_arr[1].toString())) {
                                    results_timeline.get((String) rec_arr[0]).put(rec_arr[1].toString(), results_timeline.get((String) rec_arr[0]).get(rec_arr[1].toString()).add((BigInteger) rec_arr[2]));
                                } else {
                                    results_timeline.get((String) rec_arr[0]).put(rec_arr[1].toString(), (BigInteger) rec_arr[2]);
                                }
                            } else {
                                TreeMap<String, BigInteger> tmp = new TreeMap<>();
                                tmp.put(rec_arr[1].toString(), (BigInteger) rec_arr[2]);
                                results_timeline.put((String) rec_arr[0], tmp);
                            }

                        });

                    } catch (Exception e) {
                        log.error("Timeline dataasset event EXCEPTION..." + e.toString());
                    }

                }

            });
        } catch (JSONException err) {
            System.out.println("Error: " + err.toString());
        }

        return results_timeline;
    }

    @GetMapping("/usage-analytics/admin/user-data-asset-events")
    @Transactional(readOnly = true)
    public HashMap<String, Integer> all_data_asset_events(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        HashMap<String, Integer> results = new HashMap<>();

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);
        CompletableFuture<String> asyncdataAssets = service.getICARUSUserDataAsset(token);

        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asyncdataAssets).join();

        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray dataAssetArray = new JSONArray(asyncdataAssets.get());
            dataAssetArray.forEach(assetObj -> {
                JSONObject dataAsset = (JSONObject) assetObj;
                if (dataAsset.has("id")) {
                    Object asset_id = dataAsset.get("id");
                    try {
                        List<Object[]> query_results = usage_analytics_repo.getUserAssetEvents((Integer) asset_id);
                        query_results.forEach(obj_arr -> {
                            if (results.containsKey((String) obj_arr[0])) {
                                results.put((String) obj_arr[0], results.get((String) obj_arr[0]) + 1);
                            } else {
                                results.put((String) obj_arr[0], 1);
                            }
                        });
                    } catch (Exception e) {
                        log.error("User dataassets events: EXCEPTION..." + e.toString());
                        //e.printStackTrace();
                    }

                }

            });
        } catch (JSONException err) {
            System.out.println("Error: " + err.toString());
        }
        return results;
    }

    @GetMapping("/usage-analytics/admin/category-preferences")
    @Transactional(readOnly = true)
    public HashMap<String, Integer> get_category_preferences(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<String, Integer> results = new HashMap<>();

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);
        CompletableFuture<String> asynCategories = service.getICARUSAllOrganization();

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asynCategories).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray orgArray = new JSONArray(asynCategories.get());
            orgArray.forEach(orgObj -> {
                JSONObject org = (JSONObject) orgObj;
                if (org.has("categories")) {
                    JSONArray categories = org.getJSONArray("categories");
                    categories.forEach(categoryObj -> {
                        JSONObject c = (JSONObject) categoryObj;
                        if (c.get("name") instanceof String) {
                            if (results.containsKey(c.getString("name"))) {
                                results.put(c.getString("name"), (results.get(c.getString("name")) + 1));
                            } else {
                                results.put(c.getString("name"), 1);
                            }
                        }
                    });

                }
            });
        } catch (JSONException ignored) {
        }

        return results;
    }

    @GetMapping("/usage-analytics/admin/organization-per-country")
    @Transactional(readOnly = true)
    public HashMap<String, Integer> organization_per_country(
            @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, UnauthorizedException {

        // Final object to return
        HashMap<String, Integer> results = new HashMap<>();

        // Validate Admin User e.g. {"id":1,"organizationid":null,"username":"christina","userRoles":["SUPERADMIN"]}
        CompletableFuture<Boolean> asyncCheckToken = service.checkAdminAuth(token);
        CompletableFuture<String> asynCategories = service.getICARUSAllOrganization();

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();
        CompletableFuture.allOf(asynCategories).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        try {
            JSONArray orgArray = new JSONArray(asynCategories.get());
            orgArray.forEach(orgObj -> {
                JSONObject org = (JSONObject) orgObj;
                if (org.has("country")) {
                    if (org.get("country") instanceof String) {
                        if (results.containsKey(org.getString("country"))) {
                            results.put(org.getString("country"), (results.get(org.getString("country")) + 1));
                        } else {
                            results.put(org.getString("country"), 1);
                        }
                    }
                }

            });
        } catch (JSONException ignored) {
        }

        return results;
    }

    @GetMapping("/usage-analytics/admin/popular-category-datasets")
    @Transactional(readOnly = true)
    public JsonNode get_popular_category_dataset(@RequestParam String event_type,
                                                 @CookieValue(value = "auth_token", defaultValue = "") String token)
            throws ExecutionException, InterruptedException, IOException, UnauthorizedException {

        CompletableFuture<Boolean> asyncCheckToken = service.checkToken(token);

        // Wait until they are all done
        CompletableFuture.allOf(asyncCheckToken).join();

        // If not authorized user or not admin user
        if (asyncCheckToken == null || !asyncCheckToken.get()) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Final object to return
        JSONObject return_values_json = new JSONObject();

        // Stats batch for preaggregated statistics from Database
        JSONArray popular_categories = new JSONArray();
        // Stats batch for socket connections
        JSONArray popular_datasets = new JSONArray();

        HashMap<String, Long> total_per_category = new HashMap<>();
        HashMap<String, Long> total_per_dataset = new HashMap<>();
        HashMap<String, String> datasetid_per_name = new HashMap<>();

        List<String> query_result = usage_analytics_repo.getSpecificEvents(event_type);


        try {
            HashMap<String, Long> dataset_per_count = new HashMap<>();

            // count views / contract for each asset
            query_result.forEach(asset -> {
                if (dataset_per_count.containsKey(asset)) {
                    dataset_per_count.put(asset, dataset_per_count.get(asset) + 1);
                } else {
                    dataset_per_count.put(asset, (long) 1);
                }

            });

            // merge counts for each category

            CompletableFuture<String> async_all_data_results = service.getICARUSAllDataAsset();
            JSONArray datasets_meta = new JSONArray(async_all_data_results.get());
            //JSONArray datasets_meta = new JSONArray("[{\"name\": \"Test\", \"id\":\"1\", \"status\": \"Public\", \"categories\":[{\"id\":2,\"name\":\"Airport\"}]}, {\"name\": \"Test1\", \"id\":\"2\", \"status\": \"Private\", \"categories\":[{\"id\":1,\"name\":\"Aircraft\"}]}, {\"name\": \"Dataset90\", \"id\":\"90\", \"status\": \"Public\", \"categories\":[{\"id\":2,\"name\":\"Airport\"},{\"id\":3,\"name\":\"Flight\"}]}, {\"name\": \"Dataset110\", \"id\":\"110\", \"status\": \"Public\", \"categories\":[{\"id\":3,\"name\":\"Booking\"},{\"id\":2,\"name\":\"Airport\"}]}, {\"name\": \"Dataset86\", \"id\":\"86\", \"status\": \"Public\", \"categories\":[{\"id\":2,\"name\":\"Airport\"},{\"id\":1,\"name\":\"Aircraft\"}]}]");

            for (int i = 0; i < datasets_meta.length(); i++) {

                JSONObject data_asset_obj = datasets_meta.getJSONObject(i);
                if (data_asset_obj.has("id")) {
                    String dataset_id = String.valueOf(data_asset_obj.get("id"));
                    if (dataset_per_count.containsKey(dataset_id)) {

                        if (data_asset_obj.has("status") && data_asset_obj.getString("status").compareToIgnoreCase("Confidential") != 0) {

                            // allocate the count for each dataset's categories
                            if (data_asset_obj.has("categories")) {
                                JSONArray categories = data_asset_obj.getJSONArray("categories");

                                for (int j = 0; j < categories.length(); j++) {
                                    JSONObject category_obj = categories.getJSONObject(j);
                                    String category_str = category_obj.getString("name");

                                    if (total_per_category.containsKey(category_str)) {
                                        total_per_category.put(category_str, total_per_category.get(category_str) + dataset_per_count.get(dataset_id));
                                    } else {
                                        total_per_category.put(category_str, dataset_per_count.get(dataset_id));
                                    }
                                }
                            }

                            if (data_asset_obj.getString("status").compareToIgnoreCase("Public") == 0) {
                                total_per_dataset.put(dataset_id, dataset_per_count.get(dataset_id));

                                if (data_asset_obj.has("name")) {
                                    datasetid_per_name.put(dataset_id, data_asset_obj.get("name").toString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("popular_category-dataset: EXCEPTION..." + e.toString());
        }
        LinkedHashMap<String, Long> result_category = new LinkedHashMap<>();
        LinkedHashMap<String, Long> result_dataset = new LinkedHashMap<>();

        total_per_category.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5)
                .forEach(entry -> result_category.put(entry.getKey(), entry.getValue()));

        total_per_dataset.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5)
                .forEach(entry -> result_dataset.put(entry.getKey(), entry.getValue()));

        result_category.entrySet().forEach(asset -> {
            JSONObject json = new JSONObject();
            json.put("category", asset.getKey());
            json.put("count", asset.getValue());
            popular_categories.put(json);
        });

        result_dataset.entrySet().forEach(asset -> {
            JSONObject json = new JSONObject();
            json.put("assetId", asset.getKey());
            json.put("count", asset.getValue());
            json.put("name", datasetid_per_name.get(asset.getKey()));
            popular_datasets.put(json);
        });

        return_values_json.put("popular_categories", popular_categories);
        return_values_json.put("popular_datasets", popular_datasets);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(return_values_json.toString());
    }
}