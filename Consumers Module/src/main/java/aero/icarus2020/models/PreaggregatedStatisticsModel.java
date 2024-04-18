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

    public long getMetricId() {
        return id;
    }
    public void setMetricId(long id) {
        this.id = id;
    }

    public String getMetricName() {
        return name;
    }
    public void setMetricName(String metric_name) {
        this.name = metric_name;
    }

    public long getMetricValue() {
        return value;
    }
    public void setMetricValue(long metric_value) {
        this.value = metric_value;
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
