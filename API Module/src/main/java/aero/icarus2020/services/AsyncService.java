package aero.icarus2020.services;

import aero.icarus2020.exception.EmptyResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);

    @Value("${icarus.api.authAPI}")
    private String authAPI;
    @Value("${icarus.api.adminAuthAPI}")
    private String adminAuthAPI;
    @Value("${icarus.api.countAPI}")
    private String countAPI;
    @Value("${icarus.api.allDataAssetsAPI}")
    private String allDataAssetsAPI;
    @Value("${icarus.api.userDataAssetsAPI}")
    private String userDatassetAPI;
    @Value("${icarus.api.allOrganizationAPI}")
    private String allOrganizationAPI;
    @Value("${icarus.api.socketsAPI}")
    private String socketsAPI;

    @Autowired
    private RestTemplate restTemplate;

    @Async("asyncTaskExecutor")
    public CompletableFuture<Boolean> checkAdminAuth(String token) {
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", "auth_token=" + token);
            HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
            ResponseEntity<String> response = restTemplate.exchange(adminAuthAPI, HttpMethod.GET, requestEntity, String.class);

            // If status_code != 200 then user does not exist
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject auth_results = new JSONObject(response.getBody());
                JSONArray user_roles = new JSONArray(auth_results.get("userRoles").toString());

                // Check if user_role == "SUPERADMIN"
                for (int i = 0; i < user_roles.length(); i++) {
                    String role = user_roles.getString(i);
                    if (role.equals("SUPERADMIN")) {
                        return CompletableFuture.completedFuture(true);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Token: " + token);
            log.error("checkAdminAuth: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        }
        return CompletableFuture.completedFuture(false);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<Boolean> checkToken(String token) {
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", "auth_token=" + token);
            HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
            ResponseEntity<String> response = restTemplate.exchange(authAPI, HttpMethod.GET, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return CompletableFuture.completedFuture(true);
            }
        } catch (Exception eek) {
        }
        return CompletableFuture.completedFuture(false);
    }


    @Async("asyncTaskExecutor")
    public CompletableFuture<String> getICARUSCounts() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(this.countAPI, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.hasBody())
                    return CompletableFuture.completedFuture(response.getBody());
                else throw new EmptyResponseException();
            } else {
                log.warn("getICARUSCounts: STATUS_CODE: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("getICARUSCounts: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        }
        return CompletableFuture.completedFuture("");
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<String> getICARUSAllDataAsset() {
        try {

            ResponseEntity<String> response = restTemplate.getForEntity(this.allDataAssetsAPI, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.hasBody())
                    return CompletableFuture.completedFuture(response.getBody());
                else throw new EmptyResponseException();
            } else {
                log.warn("getICARUSAllDataAsset: STATUS_CODE: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("getICARUSAllDataAsset: EXCEPTION..." + e.toString());
        }
        return CompletableFuture.completedFuture("");
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<String> getICARUSUserDataAsset(String token) {
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", "auth_token=" + token);
            HttpEntity requestEntity = new HttpEntity(null, requestHeaders);

            ResponseEntity<String> response = restTemplate.exchange(this.userDatassetAPI, HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.hasBody())
                    return CompletableFuture.completedFuture(response.getBody());
                else throw new EmptyResponseException();
            } else {
                log.warn("getICARUSAllDataAsset: STATUS_CODE: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("getICARUSuserDataAssetEvents: EXCEPTION..." + e.toString());
        }
        return CompletableFuture.completedFuture("");
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<String> getICARUSAllOrganization() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(this.allOrganizationAPI, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.hasBody())
                    return CompletableFuture.completedFuture(response.getBody());
                else throw new EmptyResponseException();
            } else {
                log.warn("getICARUSAllOrganization: STATUS_CODE: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("getICARUSAllOrganization: EXCEPTION..." + e.toString());
            //e.printStackTrace();
        }
        return CompletableFuture.completedFuture("");
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<String> getSocketConnections() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(this.socketsAPI, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.hasBody())
                    return CompletableFuture.completedFuture(response.getBody());
                else throw new EmptyResponseException();
            } else {
                log.warn("getSocketConnections: STATUS_CODE: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("getSocketConnections: EXCEPTION..." + e.toString());
        }
        return CompletableFuture.completedFuture("");
    }

    public static Logger getLog() {
        return log;
    }

    public String getAuthAPI() {
        return authAPI;
    }

    public String getAdminAuthAPI() {
        return adminAuthAPI;
    }

    public String getCountAPI() {
        return countAPI;
    }

    public String getAllDataAssetsAPI() {
        return allDataAssetsAPI;
    }

    public String getUserDatassetAPI() {
        return userDatassetAPI;
    }

    public String getAllOrganizationAPI() {
        return allOrganizationAPI;
    }

    public String getSocketsAPI() {
        return socketsAPI;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
