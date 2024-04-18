package aero.icarus2020.consumers;

import aero.icarus2020.exceptions.MissingAttributeException;
import aero.icarus2020.managers.*;
import aero.icarus2020.models.PreaggregatedStatisticsModel;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Service
public class UsageAnalyticsListener {

    private static final Logger log = LoggerFactory.getLogger(UsageAnalyticsListener.class);

    @Autowired
    private UsageAnalyticsLogsManager usageAnalyticsLogsManager;

    @Autowired
    private AssetLogsManager assetLogsManager;

    @Autowired
    private OrgLogsManager orgLogsManager;

    @Autowired
    private PreaggregatedStatsManager preaggregatedStatsManager;

    @Autowired
    private UserInteractionManager userInteractionManager;

    @KafkaListener(topics = "${kafka.topic.usage_analytics}")
    public void receive(@Payload String data, @Headers MessageHeaders headers) throws ExecutionException, InterruptedException, MissingAttributeException {

        HashMap<String, PreaggregatedStatisticsModel> preaggregatedStats = this.preaggregatedStatsManager.getAllStats();
        String event_type = "", orgId, assetId, ownerId;
        long eventId = -1;
        JSONObject properties = new JSONObject();
        JSONObject json_data = new JSONObject();

        try {
            // Convert string to json
            data = data.startsWith("\"") ? data.substring(1) : data;
            data = data.endsWith("\"") ? data.substring(0, data.length() - 1) : data;
            data = data.replace("\\", "");
            json_data = new JSONObject(data);

            if (!json_data.has("properties")) throw new MissingAttributeException("properties");
            properties = json_data.getJSONObject("properties");

            if (!json_data.has("event_type") && !json_data.has("eventType"))
                throw new MissingAttributeException("event_type");
            if (json_data.has("event_type"))
                event_type = json_data.getString("event_type");
            else event_type = json_data.getString("eventType");
            log.info("New event <" + event_type + ">");

            /* Based on the event store the data to "AssetLogs"/"OrgLogs"/"PreaggregatedStatistics" */
            switch (event_type) {
                case "ORG_REGISTER_REQUESTED":
                    this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_org_registrations"), +1);
                    break;
                case "ORG_REGISTER_FINISHED":
                    this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_org_registrations"), -1);
                    break;
                case "USER_REGISTER_REQUESTED":
                    if (!properties.has("org_id")) throw new MissingAttributeException("properties: org_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_user_registrations"), +1);
                    break;
                case "USER_REGISTER_FINISHED":
                    if (!properties.has("org_id")) throw new MissingAttributeException("properties: org_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_user_registrations"), -1);
                    break;
                case "USER_CONNECT":
                case "USER_DISCONNECT":
                    this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    break;
                case "ASSET_VIEWED":
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    break;
                case "ASSET_UNSTARRED":
                case "ASSET_STARRED":
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    if (!properties.has("org_id")) throw new MissingAttributeException("properties: org_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    this.userInteractionManager.storeInteractions(properties.getLong("org_id"), properties.getLong("asset_id"), event_type);
                    break;
                case "ASSET_REQUESTED":
                    if (!properties.has("org_applicant_id"))
                        throw new MissingAttributeException("properties: org_applicant_id");
                    if (!properties.has("org_owner_id"))
                        throw new MissingAttributeException("properties: org_owner_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_applicant_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_data_assets_requested"), +1);
                    this.userInteractionManager.storeInteractions(Long.parseLong(orgId), Long.parseLong(assetId), event_type);
                    break;
                case "ASSET_REQUEST_REJECTED":
                    if (!properties.has("org_applicant_id"))
                        throw new MissingAttributeException("properties: org_applicant_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_applicant_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_rejected_data_assets_requests"), +1);
                    break;
                case "CONTRACT_OFFERED":
                    if (!properties.has("org_applicant_id"))
                        throw new MissingAttributeException("properties: org_applicant_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_applicant_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    break;
                case "CONTRACT_ACCEPTED":
                    if (!properties.has("org_applicant_id"))
                        throw new MissingAttributeException("properties: org_applicant_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_applicant_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_contract_payment"), +1);
                    break;
                case "CONTRACT_REJECTED":
                    if (!properties.has("org_applicant_id"))
                        throw new MissingAttributeException("properties: org_applicant_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_applicant_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_rejected_contracts"), +1);
                    break;
                case "CONTRACT_PAID":
                    if (!properties.has("org_applicant_id"))
                        throw new MissingAttributeException("properties: org_applicant_id");
                    if (!properties.has("org_owner_id"))
                        throw new MissingAttributeException("properties: org_owner_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_applicant_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_paid_contracts"), +1);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_contract_payment"), -1);
                    this.userInteractionManager.storeInteractions(Long.parseLong(orgId), Long.parseLong(assetId), event_type);
                    break;
                case "DATA_CHECKIN_JOB_STARTED":
                    if (!properties.has("org_owner_id"))
                        throw new MissingAttributeException("properties: org_owner_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_owner_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_data_checkin_jobs"), +1);
                    break;
                case "DATA_CHECKIN_JOB_FINISHED":
                    if (!properties.has("org_owner_id"))
                        throw new MissingAttributeException("properties: org_owner_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_owner_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_pending_data_checkin_jobs"), -1);
                    break;
                case "DATA_ASSET_METADATA_CHANGE":
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.assetLogsManager.storeLogs("data_asset", Long.parseLong(assetId), eventId);
                    break;
                case "DATA_ASSET_UPDATED":
                    if (!properties.has("org_id")) throw new MissingAttributeException("properties: org_id");
                    if (!properties.has("asset_type")) throw new MissingAttributeException("properties: asset_type");
                    if (!properties.has("asset_id")) throw new MissingAttributeException("properties: asset_id");
                    if (!properties.has("status")) throw new MissingAttributeException("properties: status");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_id"));
                    assetId = String.valueOf(properties.get("asset_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.assetLogsManager.storeLogs(properties.getString("asset_type"), Long.parseLong(assetId), eventId);
                    if (properties.getString("status").equals("success"))
                        this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_updated_data_assets"), +1);
                    break;
                case "JOB_STARTED":
                    if (!properties.has("organizationId"))
                        throw new MissingAttributeException("properties: organizationId");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("organizationId"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_active_analytic_jobs"), +1);
                    break;
                case "JOB_COMPLETED":
                    if (!properties.has("organizationId"))
                        throw new MissingAttributeException("properties: organizationId");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("organizationId"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_active_analytic_jobs"), -1);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_completed_successfully_analytic_jobs"), +1);
                    break;
                case "JOB_FAILED":
                    if (!properties.has("organizationId"))
                        throw new MissingAttributeException("properties: organizationId");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("organizationId"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("now_active_analytic_jobs"), -1);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_failed_analytic_jobs"), +1);
                    break;
                case "NEW_VIRTUAL_DATASET":
                    if (!properties.has("org_id")) throw new MissingAttributeException("properties: org_id");
                    eventId = this.usageAnalyticsLogsManager.storeLogs(event_type, properties.toString());
                    orgId = String.valueOf(properties.get("org_id"));
                    this.orgLogsManager.storeLogs(Long.parseLong(orgId), eventId);
                    this.preaggregatedStatsManager.updateStat(preaggregatedStats.get("total_virtual_datasets"), +1);
                    break;
                default:
                    System.out.println("Unknown event received: \n" + event_type);
            }
        } catch (Exception e) {
            log.error("Failed to consume event!");
            log.error(data);
            log.error("kafka_listener: EXCEPTION..." + e.toString());
        }

    }
}
