package aero.icarus2020.util;

import java.math.BigInteger;
import java.util.Date;

public class ReturnEntity {
    public Date date;
    public BigInteger count;

    public ReturnEntity(Date date, BigInteger count) {
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
        return "ReturnEntityObject{" +
                "date=" + date +
                ", count=" + count +
                '}';
    }
}
