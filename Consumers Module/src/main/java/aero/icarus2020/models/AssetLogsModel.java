package aero.icarus2020.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "assetlogs")
@Table(name = "assetlogs")
@IdClass(AssetLogsModel.CompositeKey.class)
public class AssetLogsModel {

    @Id
    @Column(name = "asset_id")
    private long assetId;

    @Id
    @Column(name = "event_id")
    private long eventId;

    @Column(name = "asset_type")
    private char assetType;

    public AssetLogsModel() {}

    public AssetLogsModel(long assetId, long eventId, char assetType) {
        this.assetId = assetId;
        this.eventId = eventId;
        this.assetType = assetType;
    }

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public char getAssetType() {
        return assetType;
    }

    public void setAssetType(char assetType) {
        this.assetType = assetType;
    }

    @Override
    public String toString() {
        return "AssetLogsModel{" +
                "assetId=" + assetId +
                ", eventId=" + eventId +
                ", assetType=" + assetType +
                '}';
    }


    static class CompositeKey implements Serializable {
        private long assetId;
        private long eventId;
    }
}
