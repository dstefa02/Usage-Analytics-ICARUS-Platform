package aero.icarus2020.models;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "usageanalyticslogs")
@Table(name = "usageanalyticslogs")
public class UsageAnalyticsLogsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "meta")
    private String meta;

    @CreationTimestamp
    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    public UsageAnalyticsLogsModel() {}

    public UsageAnalyticsLogsModel(String eventType, String meta, Date createdOn) {
        this.eventType = eventType;
        this.meta = meta;
        this.createdOn = createdOn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "UsageAnalyticsLogsModel{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", meta='" + meta + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }
}
