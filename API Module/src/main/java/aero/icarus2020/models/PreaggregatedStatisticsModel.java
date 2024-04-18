package aero.icarus2020.models;

import javax.persistence.*;

@Entity(name = "preaggregatedstatistics")
@Table(name = "preaggregatedstatistics")
public class PreaggregatedStatisticsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private long value;

    public PreaggregatedStatisticsModel() {}

    public PreaggregatedStatisticsModel(String metric_name, long metric_value) {
        this.name = metric_name;
        this.value = metric_value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void updateMetricValue(int t) {
        this.value += t;
    }

    @Override
    public String toString() {
        return "PreaggregatedStatisticsModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
