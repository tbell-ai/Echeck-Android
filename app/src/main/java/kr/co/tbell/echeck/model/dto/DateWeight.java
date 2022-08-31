package kr.co.tbell.echeck.model.dto;

public class DateWeight {

    private int month;

    private int monthUsageCount;

    private int dayCount;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonthUsageCount() {
        return monthUsageCount;
    }

    public void setMonthUsageCount(int monthLastDay) {
        this.monthUsageCount = monthLastDay;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    @Override
    public String toString() {
        return "DateWeight{" +
                "month=" + month +
                ", monthLastDay=" + monthUsageCount +
                ", dayCount=" + dayCount +
                '}';
    }
}
