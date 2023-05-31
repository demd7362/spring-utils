package com.example.common.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    /*private static final Map<String,String> DAY_MAP = new HashMap<>(){{
        put("SUNDAY","일");
        put("MONDAY","월");
        put("TUESDAY","화");
        put("WEDNESDAY","수");
        put("THURSDAY","목");
        put("FRIDAY","금");
        put("SATURDAY","토");
    }};*/ // java 8버전 이하
    private static final Map<String,String> DAY_MAP =
            Map.of("SUNDAY","일","MONDAY","월","TUESDAY","화","WEDNESDAY","수","THURSDAY","목","FRIDAY","금","SATURDAY","토");
    /*
    yyyy 연도
    MM 월
    dd 일
    HH 시간
    mm 분
    ss 초
    SSS 밀리초
    */





    /**
     *
     * @param date1 두 날짜간의 차이를 구할 날짜 객체 1
     * @param date2 두 날짜간의 차이를 구할 날짜 객체 2
     * @return 두 날짜간의 차이를 초단위로 반환
     */
    public static long diffSecond(Temporal date1, Temporal date2){
        Duration duration = Duration.between(date1,date2);
        return Math.abs(duration.getSeconds());
    }

    /**
     *
     * @param date1 yyyyMMdd 형식의 String 객체 1
     * @param date2 yyyyMMdd 형식의 String 객체 2
     * @return 두 날짜간의 차이를 초단위로 반환
     */
    public static long diffSecond(String date1, String date2){
        Duration duration = Duration.between(parseDate(date1),parseDate(date2));
        return Math.abs(duration.getSeconds());
    }

    /**
     *
     * @param date 날짜로 파싱할 String 객체
     * @return String을 LocalDate로 파싱
     */
    public static LocalDate parseDate(String date){
        return LocalDate.parse(date,FORMATTER);
    }

    /**
     *
     * @param date 날짜 객체
     * @return 날짜 객체를 yyyyMMdd 형식으로 포맷
     */
    public static String formatDate(Temporal date){
        return FORMATTER.format(date);
    }
    /**
     *
     * @param date1 yyyyMMdd 형식의 String 객체 1
     * @param date2 yyyyMMdd 형식의 String 객체 2
     * @return 두 날짜간의 차잇값을 담은 Period 객체를 반환
     */
    private static Period dateDiff(String date1,String date2){
        LocalDate ld1 = parseDate(date1);
        LocalDate ld2 = parseDate(date2);
        return Period.between(ld1,ld2);
    }

    /**
     *
     * @param date1 두 날짜간의 차이를 구할 날짜 객체 1
     * @param date2 두 날짜간의 차이를 구할 날짜 객체 2
     * @return 두 날짜간의 차잇값을 담은 Period 객체를 반환
     */
    private static Period dateDiff(Temporal date1,Temporal date2){
        LocalDate ld1 = LocalDate.from(date1);
        LocalDate ld2 = LocalDate.from(date2);
        return Period.between(ld1,ld2);
    }

    /**
     *
     * @param date1 yyyyMMdd 형식의 String 객체 1
     * @param date2 yyyyMMdd 형식의 String 객체 2
     * @return 두 날짜간의 차이를 년수로 반환
     */
    public static int diffYears(String date1, String date2){
        return dateDiff(date1,date2).getYears();
    }
    /**
     *
     * @param date1 yyyyMMdd 형식의 String 객체 1
     * @param date2 yyyyMMdd 형식의 String 객체 2
     * @return 두 날짜간의 차이를 개월수로 반환
     */
    public static int diffMonths(String date1, String date2){
        return dateDiff(date1,date2).getMonths();
    }

    /**
     *
     * @param date1 yyyyMMdd 형식의 String 객체 1
     * @param date2 yyyyMMdd 형식의 String 객체 2
     * @return 두 날짜간의 차이를 일수로 반환
     */
    public static int diffDays(String date1, String date2){
        return dateDiff(date1,date2).getDays();
    }
    /**
     *
     * @param date1 Temporal을 구현하는 날짜 객체 1
     * @param date2 Temporal을 구현하는 날짜 객체 2
     * @return 두 날짜간의 차이를 년수로 반환
     */
    public static int diffYears(Temporal date1, Temporal date2){
        return dateDiff(date1,date2).getYears();
    }

    /**
     *
     * @param date1 Temporal을 구현하는 날짜 객체 1
     * @param date2 Temporal을 구현하는 날짜 객체 2
     * @return 두 날짜간의 차이를 개월수로 반환
     */
    public static int diffMonths(Temporal date1, Temporal date2){
        return dateDiff(date1,date2).getMonths();
    }

    /**
     *
     * @param date1 Temporal을 구현하는 날짜 객체 1
     * @param date2 Temporal을 구현하는 날짜 객체 2
     * @return 두 날짜간의 차이를 일수로 반환
     */
    public static int diffDays(Temporal date1, Temporal date2){
        return dateDiff(date1,date2).getDays();
    }

    /**
     *
     * @param date 감산할 yyyyMMdd 형식의 String 객체
     * @param years 감산할 년수
     * @return 감산된 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String subtractYears(String date,int years){
        LocalDate ld = parseDate(date);
        LocalDate targetDate = LocalDate.of(ld.getYear()-years,ld.getMonthValue(),ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 감산할 날짜 객체
     * @param years 감산할 년수
     * @return 감산할 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String subtractYears(Temporal date,int years){
        LocalDate ld = LocalDate.from(date);
        LocalDate targetDate = LocalDate.of(ld.getYear()-years,ld.getMonthValue(),ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 감산할 yyyyMMdd 형식의 String 객체
     * @param months 감산할 개월수
     * @return 감산된 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String subtractMonths(String date,int months){
        LocalDate ld = parseDate(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue()-months,ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 감산할 날짜 객체
     * @param months 감산할 개월수
     * @return 감산된 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String subtractMonths(Temporal date,int months){
        LocalDate ld = LocalDate.from(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue()-months,ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 감산할 yyyyMMdd 형식의 String 객체
     * @param days 감산할 일수
     * @return 감산된 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String subtractDays(String date,int days){
        LocalDate ld = parseDate(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue(),ld.getDayOfMonth()-days);
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 감산할 날짜 객체
     * @param days 감산할 일수
     * @return 감산된 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String subtractDays(Temporal date,int days){
        LocalDate ld = LocalDate.from(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue(),ld.getDayOfMonth()-days);
        return formatDate(targetDate);
    }
    /**
     *
     * @param date 더해질 yyyyMMdd 형식의 String 객체
     * @param years 더할 년수
     * @return 더해진 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String addYears(String date,int years){
        LocalDate ld = parseDate(date);
        LocalDate targetDate = LocalDate.of(ld.getYear()+years,ld.getMonthValue(),ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 더해질 날짜 객체
     * @param years 더할 년수
     * @return 더해진 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String addYears(Temporal date,int years){
        LocalDate ld = LocalDate.from(date);
        LocalDate targetDate = LocalDate.of(ld.getYear()+years,ld.getMonthValue(),ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 더해질 yyyyMMdd 형식의 String 객체
     * @param months 더할 개월수
     * @return 더해진 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String addMonths(String date,int months){
        LocalDate ld = parseDate(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue()+months,ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 더해질 날짜 객체
     * @param months 더할 개월수
     * @return 더해진 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String addMonths(Temporal date,int months){
        LocalDate ld = LocalDate.from(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue()+months,ld.getDayOfMonth());
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 더해질 yyyyMMdd 형식의 String 객체
     * @param days 더할 일수
     * @return 더해진 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String addDays(String date,int days){
        LocalDate ld = parseDate(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue(),ld.getDayOfMonth()+days);
        return formatDate(targetDate);
    }

    /**
     *
     * @param date 더해질 날짜 객체
     * @param days 더할 일수
     * @return 더해진 날짜를 yyyyMMdd 형식의 String으로 반환
     */

    public static String addDays(Temporal date,int days){
        LocalDate ld = LocalDate.from(date);
        LocalDate targetDate = LocalDate.of(ld.getYear(),ld.getMonthValue(),ld.getDayOfMonth()+days);
        return formatDate(targetDate);
    }


    /**
     *
     * @param isAscending 오름차순으로 정렬할건지의 여부
     * @param dates yyyyMMdd 형식의 String 객체 배열
     * @return 날짜순으로 정렬된 리스트를 반환
     */
    public static List<LocalDate> sortDates(boolean isAscending,String... dates) {
        if (dates.length < 2) {
            return new ArrayList<>(Arrays.asList(parseDate(dates[0])));
        }
        List<LocalDate> dateList = Arrays.stream(dates)
                .map(date -> LocalDate.parse(date, FORMATTER))
                .collect(Collectors.toList());

        dateList.sort(isAscending ? LocalDate::compareTo : Collections.reverseOrder());
        return dateList;
    }

    /**
     *
     * @param isAscending 오름차순으로 정렬할건지의 여부
     * @param dates 날짜 객체 배열
     * @return 날짜순으로 정렬된 리스트를 반환
     */
    public static List<LocalDate> sortDates(boolean isAscending,Temporal... dates) {
        if (dates.length < 2) {
            return new ArrayList<>(Arrays.asList(LocalDate.from(dates[0])));
        }
        List<LocalDate> dateList = Arrays.stream(dates)
                .map(date -> LocalDate.from(date))
                .collect(Collectors.toList());

        dateList.sort(isAscending ? LocalDate::compareTo : Collections.reverseOrder());
        return dateList;
    }

    /**
     *
     * @param year 연도
     * @param month 월
     * @return 말일
     */
    public static int lastDay(int year,int month){
        LocalDate ld = LocalDate.of(year,month,1);
        return ld.lengthOfMonth();
    }

    /**
     *
     * @param yyyyMM yyyyMM 형식의 String
     * @return 말일
     */
    public static int lastDay(String yyyyMM){
        if(!yyyyMM.matches("^\\d{6}$")){
            // do something
        }
        int year = Integer.parseInt(yyyyMM.substring(0,4));
        int month = Integer.parseInt(yyyyMM.substring(4,6));
        LocalDate ld = LocalDate.of(year,month,1);
        return ld.lengthOfMonth();
    }

    /**
     *
     * @param year 연도
     * @return 윤년인지 여부
     */
    public static boolean isLeapYear(int year){
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    /**
     *
     * @return 현재 날짜
     */
    public static String nowDate(){
        LocalDate ld = LocalDate.now();
        return formatDate(ld);
    }

    /**
     *
     * @param date 날짜 객체
     * @return 날짜에 해당하는 요일을 반환
     */
    public static String getDayOfWeekName(Temporal date){
        LocalDate ld = LocalDate.from(date);
        return DAY_MAP.get(ld.getDayOfWeek().name());
    }

    /**
     *
     * @param date yyyyMMdd 형식의 String 객체
     * @return 날짜에 해당하는 요일을 반환
     */
    public static String getDayOfWeekName(String date){
        LocalDate ld = parseDate(date);
        return DAY_MAP.get(ld.getDayOfWeek().name());
    }



}