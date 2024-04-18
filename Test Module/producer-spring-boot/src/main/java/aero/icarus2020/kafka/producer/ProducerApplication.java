package aero.icarus2020.kafka.producer;

import aero.icarus2020.kafka.producer.producers.UsageAnalyticsSender;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProducerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Autowired
    private UsageAnalyticsSender sender;

    // 1) ORG_REGISTER_REQUESTED - Policy manager (UBITECH) (release v1.0)
    public void event_org_register_requested() {
        // json example of ORG_REGISTER_REQUESTED
        /*{
          "event_type": "ORG_REGISTER_REQUESTED",
          "properties": {
             "org_registration_id": "1",
             "org_name": "UCY"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ORG_REGISTER_REQUESTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("org_registration_id", "1");
        properties.put("org_name", "UCY");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ORG_REGISTER_REQUESTED> message!");
    }

    // 2) ORG_REGISTER_FINISHED - Policy manager (UBITECH) (release v1.0)
    public void event_org_register_finished() {
        // json example of ORG_REGISTER_FINISHED
        /*{
          "event_type": "ORG_REGISTER_FINISHED",
          "properties": {
             "org_registration_id": "1",
             "org_name": "UCY",
             "status": "accepted/rejected"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ORG_REGISTER_FINISHED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("org_registration_id", "1");
        properties.put("org_name", "UCY");
        properties.put("status", "accepted"); // accepted/rejected registration
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ORG_REGISTER_FINISHED> message!");
    }

    // 3) USER_REGISTER_REQUESTED - Policy manager (UBITECH) (release v1.0)
    public void event_user_register_requested() {
        // json example of USER_REGISTER_REQUESTED
        /*{
          "event_type": "USER_REGISTER_REQUESTED",
          "properties": {
             "user_invitation_id": "1",
             "org_id": "1"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "USER_REGISTER_REQUESTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("user_invitation_id", "1");
        properties.put("org_id", "1");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <USER_REGISTER_REQUESTED> message!");
    }

    // 4) USER_REGISTER_FINISHED - Policy manager (UBITECH) (release v1.0)
    public void event_user_register_finished() {
        // json example of USER_REGISTER_FINISHED
        /*{
          "event_type": "USER_REGISTER_FINISHED",
          "properties": {
             "user_registration_id": "1",
             "org_id": "1"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "USER_REGISTER_FINISHED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("user_registration_id", "1");
        properties.put("org_id", "1");
        //properties.put("status", "accepted"); // accepted/rejected registration
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <USER_REGISTER_FINISHED> message!");
    }

    // 5) USER_CONNECT - Notification Manager (UCY) (release v1.0)
    public void event_user_connect() {
        // json example of USER_CONNECT
        /*{
          "event_type": "USER_CONNECT",
          "properties": {
             "socket_id": "ysGntZGQoAQsWI3lAAAC",
             "user_id": "1",
             "ip": "127.0.0.1",
             "user_agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "USER_CONNECT"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("socket_id", "ysGntZGQoAQsWI3lAAAC");
        properties.put("user_id", (int)(Math.random() * 20));
        properties.put("ip", "127.0.0.1");
        properties.put("user_agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <USER_CONNECT> message!");
    }

    // 6) USER_DISCONNECT - Notification Manager (UCY) (release v1.0)
    public void event_user_disconnect() {
        // json example of USER_DISCONNECT
        /*{
          "event_type": "USER_DISCONNECT",
          "properties": {
             "socket_id": "ysGntZGQoAQsWI3lAAAC",
             "user_id": "1",
             "ip": "127.0.0.1",
             "user_agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "USER_DISCONNECT"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("socket_id", "ysGntZGQoAQsWI3lAAAC");
        properties.put("user_id", (int)(Math.random() * 20));
        properties.put("ip", "127.0.0.1");
        properties.put("user_agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <USER_DISCONNECT> message!");
    }

    // 7) ASSET_VIEWED - Query explorer (SUITE5) (release v1.5)
    public void event_asset_viewed() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ASSET_VIEWED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_id", "110");
        properties.put("viewer_id", "1");
        properties.put("org_viewer_id", "1");
        properties.put("asset_type", "data_asset");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ASSET_VIEWED> message!");
    }

    // 8) ASSET_STARRED - Query explorer (SUITE5) (release v1.5)
    public void event_asset_starred() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ASSET_STARRED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_id", "110");
        properties.put("user_id", "1");
        properties.put("org_id", "1");
        properties.put("asset_type", "data_asset");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ASSET_STARRED> message!");
    }

    // 9) ASSET_UNSTARRED - Query explorer (SUITE5) (release v1.5)
    public void event_asset_unstarred() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ASSET_UNSTARRED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_id", "110");
        properties.put("user_id", "1");
        properties.put("org_id", "1");
        properties.put("asset_type", "data_asset");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ASSET_UNSTARRED> message!");
    }

    // 10) ASSET_REQUESTED - Data License and Agreement Manager (SUITE5) (release v1.0)
    public void event_asset_requested() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ASSET_REQUESTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "110");
        properties.put("asset_type", "data_asset");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ASSET_REQUESTED> message!");
    }

    // 11) ASSET_REQUEST_REJECTED - Data License and Agreement Manager (SUITE5) (release v1.0)
    public void event_asset_request_rejected() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ASSET_REQUEST_REJECTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "86");
        properties.put("asset_id", "5");
        properties.put("asset_type", "data_asset");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ASSET_REQUEST_REJECTED> message!");
    }

    // 12) CONTRACT_OFFERED - Data License and Agreement Manager (SUITE5) (release v1.0)
    public void event_contract_offered() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "CONTRACT_OFFERED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "110");
        properties.put("asset_type", "data_asset");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        properties.put("duration_days", "60");
        properties.put("price_amount", "1000");
        properties.put("currency", "USD");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <CONTRACT_OFFERED> message!");
    }

    // 13) CONTRACT_DRAFT - Data License and Agreement Manager (SUITE5) (release v1.5)
    public void event_contract_draft() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "CONTRACT_DRAFT"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "110");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <CONTRACT_DRAFT> message!");
    }

    // 14) CONTRACT_NEGOTIATION - Data License and Agreement Manager (SUITE5) (release v1.5)
    public void event_contract_negotiation() {
        // json example of CONTRACT_NEGOTIATION
        /*{
          "event_type": "CONTRACT_NEGOTIATION",
          "properties": {
             "asset_request_id": "1",
             "asset_id": "5",
             "org_applicant_id": "1",
             "org_owner_id": "2"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "CONTRACT_NEGOTIATION"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "5");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <CONTRACT_NEGOTIATION> message!");
    }

    // 15) CONTRACT_ACCEPTED - Data License and Agreement Manager (SUITE5) (release v1.0)
    public void event_contract_accepted() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "CONTRACT_ACCEPTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "110");
        properties.put("asset_type", "data_asset");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <CONTRACT_ACCEPTED> message!");
    }

    // 16) CONTRACT_REJECTED - Data License and Agreement Manager (SUITE5) (release v1.0)
    public void event_contract_rejected() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "CONTRACT_REJECTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "110");
        properties.put("asset_type", "data_asset");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <CONTRACT_REJECTED> message!");
    }

    // 17) CONTRACT_PAID - Data License and Agreement Manager (SUITE5) (release v1.0)
    public void event_contract_paid() {
        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "CONTRACT_PAID"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_request_id", "1");
        properties.put("asset_id", "110");
        properties.put("asset_type", "data_asset");
        properties.put("org_applicant_id", "1");
        properties.put("org_owner_id", "2");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <CONTRACT_PAID> message!");
    }

    // 18) DATA_CHECKIN_JOB_STARTED - Data Handler (UBITECH) (release v1.0)
    public void event_data_checkin_job_started() {
        // json example of DATA_CHECKIN_JOB_STARTED
        /*
            {
              "eventType": "DATA_CHECKIN_JOB_STARTED",
              "properties": {
                "data_checkin_job_id": 219,
                "org_owner_id": "2"
              },
              "timestamp": 1579008869635
            }
        */

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "DATA_CHECKIN_JOB_STARTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("data_checkin_job_id", "1");
        properties.put("org_owner_id", "1");

        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <DATA_CHECKIN_JOB_STARTED> message!");
    }

    // 19) DATA_CHECKIN_JOB_FINISHED - Data Handler (UBITECH) (release v1.0)
    public void event_data_checkin_job_finished() {
        // json example of DATA_CHECKIN_JOB_FINISHED
        /*{
          "event_type": "DATA_CHECKIN_JOB_FINISHED",
          "properties": {
               "data_checkin_job_id": "1",
               "org_owner_id": "1",
               "duration_min": "120",
               "status": "success/failure"
          },
          "timestamp": 1563454611048
        }

        {
          "event_type": "DATA_CHECKIN_JOB_STARTED",
          "properties": {
             "data_checkin_job_id": "1",
             "org_owner_id": "1",
             "data_size_MB": "15.4",
             "num_of_rows": "1000",
             "num_of_cols": "20",
             "num_of_cleaning_rules": "2",
             "cleaning_rules": [
               {
                 "valid_type": "PRE_DEFINED_VALUE",
                 "cleaning_rule": "REPLACE_WITH_VALUE",
                 "missing_values": "FILL_WITH_VALUE"
               },
               {
                 "valid_type": "OUTLIER",
                 "cleaning_rule": "DROP_ROW",
                 "missing_values": "NONE"
                }
             ],
             "num_of_anonymization_rules": "1",
             "anonymization_rules": [
                {
                 "attribute_type": "Sensitive",
                 "privacy_model": "l-diversity"
                }
             ],
             "encryption": "none/all/custom"
          },
          "timestamp": 1563454611048
        }

        */

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "DATA_CHECKIN_JOB_FINISHED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("data_checkin_job_id", "1");
        properties.put("org_owner_id", "1");
        properties.put("duration_min", "120");
        properties.put("status", "success"); // success/failure of data check-in job
        event.put("properties", properties);

        /* Create JSON Object */
        /*// Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "DATA_CHECKIN_JOB_STARTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("data_checkin_job_id", "1");
        properties.put("org_owner_id", "1");

        JSONArray lang = new JSONArray();
        lang.put("EN");
        lang.put("GR");
        properties.put("lang", lang);

        JSONArray cleaning_rules = new JSONArray();
        JSONObject cleaning_rule_1 = new JSONObject();
        cleaning_rule_1.put("valid_type", "PRE_DEFINED_VALUE");
        cleaning_rule_1.put("cleaning_rule", "REPLACE_WITH_VALUE");
        cleaning_rule_1.put("missing_values", "FILL_WITH_VALUE");
        cleaning_rules.put(cleaning_rule_1);
        properties.put("cleaning_rules", cleaning_rules);*/

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <DATA_CHECKIN_JOB_FINISHED> message!");
    }

    // 20) DATA_ASSET_METADATA_CHANGE - Data Handler (UBITECH) (release v1.0)
    public void event_data_asset_metadata_change() {
        // json example of DATA_ASSET_METADATA_CHANGE
        /*{
          "event_type": "DATA_ASSET_METADATA_CHANGE",
          "properties": {
             "asset_id": "1",
             "visibility": "confidential",
             "categories": ["Flight"],
             "frequency": "Hourly",
             "velocity": "batch",
             "spatial_cov": ["GR"],
             "temporal_coverage": ["2019-11-21", "2019-11-26"],
             "lang": ["en"]
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "DATA_ASSET_METADATA_CHANGE"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("asset_id", "1");
        properties.put("visibility", "confidential");

        JSONArray categories = new JSONArray();
        categories.put("Flight");
        properties.put("categories", categories);

        properties.put("frequency", "Hourly");
        properties.put("velocity", "batch");

        JSONArray spatial_cov = new JSONArray();
        spatial_cov.put("GR");
        properties.put("spatial_cov", spatial_cov);

        JSONArray temporal_coverage = new JSONArray();
        temporal_coverage.put("2019-11-21");
        temporal_coverage.put("2019-11-26");
        properties.put("temporal_coverage", temporal_coverage);

        JSONArray lang = new JSONArray();
        lang.put("en");
        properties.put("lang", lang);

        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <DATA_ASSET_METADATA_CHANGE> message!");
    }

    // 21) ASSET_UPDATED - Data Handler (UBITECH) (release v1.5)
    public void event_asset_updated() {
    }

    // 22) ANALYTIC_JOB_QUEUED - Analytics and Visualization Workbench (ENG) (release v1.5)
    public void event_analytic_job_queued() {
        // json example of ANALYTIC_JOB_QUEUED
        /*{
          "event_type": "ANALYTIC_JOB_QUEUED",
          "properties": {
             "analytic_job_id": "1",
             "org_id": "1",
             "user_id": "1",
             "algorithms": ["k-means"]
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ANALYTIC_JOB_QUEUED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("analytic_job_id", "1");
        properties.put("org_id", "1");
        properties.put("user_id", "1");

        JSONArray algorithms = new JSONArray();
        algorithms.put("k-means");
        properties.put("algorithms", algorithms);

        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ANALYTIC_JOB_QUEUED> message!");
    }

    // 23) ANALYTIC_JOB_STARTED - Analytics and Visualization Workbench (ENG) (release v1.0)
    public void event_analytic_job_started() {
        // json example of ANALYTIC_JOB_STARTED
        /*{
          "event_type": "ANALYTIC_JOB_STARTED",
          "properties": {
             "analytic_job_id": "1",
             "org_id": "1",
             "user_id": "1"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ANALYTIC_JOB_STARTED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("analytic_job_id", "1");
        properties.put("org_id", "1");
        properties.put("user_id", "1");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ANALYTIC_JOB_STARTED> message!");
    }

    // 24) ANALYTIC_JOB_FINISHED - Analytics and Visualization Workbench (ENG) (release v1.0)
    public void event_analytic_job_finished() {
        // json example of ANALYTIC_JOB_FINISHED
        /*{
          "event_type": "ANALYTIC_JOB_FINISHED",
          "properties": {
             "analytic_job_id": "1",
             "org_id": "1",
             "status": "success/failure"
          },
          "timestamp": 1563454611048
        }*/

        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "ANALYTIC_JOB_FINISHED"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("analytic_job_id", "1");
        properties.put("org_id", "1");
        properties.put("status", "failure");  // success/failure of analytic job
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <ANALYTIC_JOB_FINISHED> message!");
    }

    // 25) NEW_VIRTUAL_DATASET - SUITE5 (release v1.5)
    public void event_new_virtual_dataset() {
        // json example of ANALYTIC_JOB_FINISHED
        /*{
          "event_type": "ANALYTIC_JOB_FINISHED",
          "properties": {
             "analytic_job_id": "1",
             "org_id": "1",
             "status": "success/failure"
          },
          "timestamp": 1563454611048
        }*/
        /*{
           "event_type": "NEW_VIRTUAL_DATASET",
           "properties": {
              "org_id": 1
           },
           "timestamp": 1563454611048
        }*/


        /* Create JSON Object */
        // Set main info
        JSONObject event = new JSONObject();
        event.put("event_type", "NEW_VIRTUAL_DATASET"); // set event type
        event.put("timestamp", new java.util.Date().toString()); // set current timestamp

        // Set properties
        JSONObject properties = new JSONObject();
        properties.put("virtual_dataset_id", "1");
        properties.put("org_id", "1");
        event.put("properties", properties);

        // Send message
        String jsonString = event.toString();
        sender.send(jsonString);

        log.info("Successfully sent <NEW_VIRTUAL_DATASET> message!");
    }

    @Override
    public void run(String... strings) throws Exception {

        Random rand = new Random();

        // 1) ORG_REGISTER_REQUESTED - Policy manager (UBITECH) (release v1.0)
        //event_org_register_requested();

        // 2) ORG_REGISTER_FINISHED - Policy manager (UBITECH) (release v1.0)
        //event_org_register_finished();

        // 3) USER_REGISTER_REQUESTED - Policy manager (UBITECH) (release v1.0)
        //event_user_register_requested();

        // 4) USER_REGISTER_FINISHED - Policy manager (UBITECH) (release v1.0)
        //event_user_register_finished();

        // 5) USER_CONNECT - Notification Manager (UCY) (release v1.0)
        //for (int i = 0; i < 500; i++) {
        //    event_user_connect();
        //}

        // 6) USER_DISCONNECT - Notification Manager (UCY) (release v1.0)
        //for (int i = 0; i < 500; i++) {
        //    event_user_disconnect();
        //}

        //// 7) ASSET_VIEWED - Query explorer (SUITE5) (release v1.5)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_asset_viewed();
        //}
        //
        //// 8) ASSET_STARRED - Query explorer (SUITE5) (release v1.5)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_asset_starred();
        //}
        //
        //// 9) ASSET_UNSTARRED - Query explorer (SUITE5) (release v1.5)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_asset_unstarred();
        //}
        //
        //// 10) ASSET_REQUESTED - Data License and Agreement Manager (SUITE5) (release v1.0)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_asset_requested();
        //}
        //
        //// 11) ASSET_REQUEST_REJECTED - Data License and Agreement Manager (SUITE5) (release v1.0)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_asset_request_rejected();
        //}
        //
        //// 12) CONTRACT_OFFERED - Data License and Agreement Manager (SUITE5) (release v1.0)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_contract_offered();
        //}
        //
        //// 15) CONTRACT_ACCEPTED - Data License and Agreement Manager (SUITE5) (release v1.0)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_contract_accepted();
        //}
        //
        //// 16) CONTRACT_REJECTED - Data License and Agreement Manager (SUITE5) (release v1.0)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_contract_rejected();
        //}
        //
        //// 17) CONTRACT_PAID - Data License and Agreement Manager (SUITE5) (release v1.0)
        //for (int i = 0; i < rand.nextInt(100); i++) {
        //    event_contract_paid();
        //}
        // 18) DATA_CHECKIN_JOB_STARTED - Data Handler (UBITECH) (release v1.0)
        //event_data_checkin_job_started();

        // 19) DATA_CHECKIN_JOB_FINISHED - Data Handler (UBITECH) (release v1.0)
        //event_data_checkin_job_finished();

        // 20) DATA_ASSET_METADATA_CHANGE - Data Handler (UBITECH) (release v1.0)
        //event_data_asset_metadata_change();

        // 21) ASSET_UPDATED - Data Handler (UBITECH) (release v1.5)
        //event_asset_updated();

        // 22) ANALYTIC_JOB_QUEUED - Analytics and Visualization Workbench (ENG) (release v1.5)
        //event_analytic_job_queued();

        // 23) ANALYTIC_JOB_STARTED - Analytics and Visualization Workbench (ENG) (release v1.0)
        //event_analytic_job_started();

        // 24) ANALYTIC_JOB_FINISHED - Analytics and Visualization Workbench (ENG) (release v1.0)
        //event_analytic_job_finished();

        // 25) NEW_VIRTUAL_DATASET - SUITE5 (release v1.5)
        event_new_virtual_dataset();
    }
}
