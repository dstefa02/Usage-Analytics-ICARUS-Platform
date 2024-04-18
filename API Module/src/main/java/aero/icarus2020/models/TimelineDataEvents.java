package aero.icarus2020.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class TimelineDataEvents implements Serializable {

    private Date date;
    private BigInteger count;

    TimelineDataEvents() {
    }

    public TimelineDataEvents(Date date, BigInteger count) {
        this.date = date;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigInteger getCount() {
        return count;
    }

    public void setCount(BigInteger count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TimelineDataEvents{" +
                "date=" + date +
                ", count=" + count +
                '}';
    }
}
