package kr.co.tbell.echeck.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private DateUtil() {}

    /**
     * 오늘 날짜를 추출하여 int형의 배열로 return하는 함수
     *
     */
    public static String getCurrentToday() {

        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(mCalendar.getTime());

        return date;
    }

    /**
     * 해당 날짜의 바로 다음날을 구하는 함수
     *
     * @param date
     */
    public static String getTomorrow(Date date) {

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        mCalendar.add(Calendar.DATE, 1);
        Date tomorrow = mCalendar.getTime();

        System.out.println("tomorrow    :   @@@  " + tomorrow);

        return convertDateToString(tomorrow);
    }

    /**
     * 해당 월의 마지막 일자를 구하는 함수, 마지막 일자는 월별 일자 가중치를 구하는데 사용
     *
     * @param year
     * @param month
     * @param day
     */
    public static int getLastDay(int year, int month, int day) {

        // 1. Month의 마지막 날짜를 구함
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthUsageCount(String date, String type) {

        int result = 0;
        int monthLastDay = 0;
        int []splitDate = splitStringDate(date);
        monthLastDay = getLastDay(splitDate[0], splitDate[1], splitDate[2]);

        if(type.equals("start")) {
            result = monthLastDay - splitDate[2] + 1;
        } else if(type.equals("end")) {
            result = splitDate[2];
        }

        return result;
    }

    /**
     * 특정 날짜부터 특정 날짜까지의 일자 차이를 구하는 함수
     *
     * @param start
     * @param end
     */
    public static long getDayCount(Date start, Date end) {

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(start);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(end);

        long differentSec = (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 1000;
        long differentDays = differentSec / (24*60*60); //일자수 차이

        return (differentDays + 1);
    }


    /**
     * 하계와 기타계절, 동계를 구분하는 함수
     *
     *
     */
    public static String getSeason() {

        String season = "";
        int month = Integer.parseInt(convertDateToString(new Date()).split("-")[1]);

        if(month == 7 || month == 8) {
            season = "summer";
        } else if(month == 12 || month == 1 || month == 2) {
            season = "winter";
        } else {
            season = "other";
        }

        return season;
    }

    /**
     * String형식을 Date형식으로 변환하는 함수
     *
     * @param stringDate
     */
    public static Date convertStringToDate(String stringDate) {

        Date date = null;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * Date형식을 String형식으로 변환하는 함수
     *
     * @param date
     */
    public static String convertDateToString(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(date);

        return time;
    }

    /**
     * String 형식의 날짜를 '-' 기준으로 split하여 반환하는 함수
     *
     * @param target
     */
    public static int[] splitStringDate(String target) {
        // String 형의 날짜를 int 형으로 숫자만 획득
        int []result = {0, 0, 0};
        String[] date = target.split("-");

        for(int i=0; i<date.length; i++) {
            result[i] = Integer.parseInt(date[i]);
        }

        return result;
    }
}
