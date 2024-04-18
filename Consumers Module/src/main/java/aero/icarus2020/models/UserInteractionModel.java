package aero.icarus2020.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "userinteractions")
@Table(name = "userinteractions")
@IdClass(UserInteractionModel.CompositeKey.class)
public class UserInteractionModel {

    @Id
    @Column(name = "organization_id ")
    private long organization_id;

    @Id
    @Column(name = "asset_id")
    private long asset_id;

    @Column(name = "score")
    private short score;

    public UserInteractionModel() {
    }

    public UserInteractionModel(long organization_id, long asset_id, short score) {
        this.organization_id = organization_id;
        this.asset_id = asset_id;
        this.score = score;
    }

    public long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(long organization_id) {
        this.organization_id = organization_id;
    }

    public long getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(long asset_id) {
        this.asset_id = asset_id;
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UserInteraction{" +
                "organization_id=" + organization_id +
                ", asset_id=" + asset_id +
                ", score=" + score +
                '}';
    }

    static class CompositeKey implements Serializable {
        private long organization_id;
        private long asset_id;
    }

}
