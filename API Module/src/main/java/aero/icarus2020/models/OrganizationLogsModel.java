package aero.icarus2020.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "organizationlogs")
@Table(name = "organizationlogs")
@IdClass(OrganizationLogsModel.CompositeKey.class)
public class OrganizationLogsModel {

    @Id
    @Column(name = "organization_id")
    private long organizationId;

    @Id
    @Column(name = "event_id")
    private long eventId;

    public OrganizationLogsModel() {}

    public OrganizationLogsModel(long organizationId, long eventId) {
        this.organizationId = organizationId;
        this.eventId = eventId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "OrganizationLogsModel{" +
                "organizationId=" + organizationId +
                ", eventId=" + eventId +
                '}';
    }

    static class CompositeKey implements Serializable {
        private long organizationId;
        private long eventId;
    }
}
