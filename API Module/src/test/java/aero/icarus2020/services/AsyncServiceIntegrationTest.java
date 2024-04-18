package aero.icarus2020.services;

import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class tests that the apis are available to the application.
 * Each method runs only if the application is compiled with the command "-Dtest-groups=integration-tests"
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@IfProfileValue(name = "test-groups", values = {"integration-tests"})
public class AsyncServiceIntegrationTest {

    @Autowired
    private AsyncService asyncService;

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

    private TestRestTemplate testRestTemplate;

    @Before
    public void setUp() {
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void getICARUSCountsTest() {
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(countAPI, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getICARUSAllDataAssetTest() {
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(allDataAssetsAPI, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getICARUSUserDataAssetTest() {
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(userDatassetAPI, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getICARUSAllOrganizationTest() {
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(allOrganizationAPI, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getSocketConnectionsTest() {
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(socketsAPI, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Ignore
    public void checkAdminAuthTest() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjY2hhbmlvdGFraUB1Yml0ZWNoLmV1Iiwicm9sZSI6IlNVUEVSQURNSU4iLCJ1c2VyX2lkIjoxLCJpc3MiOiJpY2FydXMtYmFja2VuZC1hcHAiLCJleHAiOjE1OTkzOTc0OTgsImlhdCI6MTU5OTIyNDY5OCwianRpIjoiMWZkMWMyOWMtNjE5OC00ODA3LThhZWEtMWE4NzJmNjY1OWQ4In0.C4QqVNOC-gwV7Q2HoLKr6K7qVkcQ7dMgzmwAwJej9Vs";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "auth_token=" + token);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(adminAuthAPI, HttpMethod.GET, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Ignore
    public void checkTokenTest() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW9yZ2lvdS5qb2FubmFAY3MudWN5LmFjLmN5Iiwicm9sZSI6Ik9SR0FOSVNBVElPTkFETUlOIiwidXNlcl9pZCI6NDAsIm9yZ2FuaXphdGlvbl9pZCI6MTMsImlzcyI6ImljYXJ1cy1iYWNrZW5kLWFwcCIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiVW5pdmVyc2l0eSBvZiBDeXBydXMiLCJleHAiOjE1OTkzOTc0NDUsImlhdCI6MTU5OTIyNDY0NSwianRpIjoiNDFhODE4YjItMWM3ZC00NWVlLTk4NjQtMDE3MjMxZjNlZTk3In0.c5RRX8Itgbn52kuS0zNhedZ86-XYPRhWhH9rVI9FC7o";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "auth_token=" + token);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(authAPI, HttpMethod.GET, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}