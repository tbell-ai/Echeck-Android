package kr.co.tbell.echeck.views.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.dto.Calculator;
import kr.co.tbell.echeck.model.dto.DateWeight;
import kr.co.tbell.echeck.model.dto.DiscountCount;
import kr.co.tbell.echeck.util.DateUtil;

/**
 * 전기요금 계산 공식이 적용된 Class
 * 2021년 최신 개편 요금제 적용(21년 1월 기준)
 * 요청 유형에 따라 계산 문장들을 유형별(전기요금계, 부가가치세, 전력산업기반기금, 최종 청구금액)로 제공
 *
 */
public class ElectCalcHelper {

    /**
     * 주택용(저압) 전기요금
     * 1주택 수 가구 요금은 주택용(저압) 요금표 기준으로 계산됨
     *
     * 하계(7월1일 ~ 8월31일)
     * 기타계절(1월1일 ~ 6월30일, 9월1일 ~ 12월31일)
     * 동계(12월1일 ~ 2월28(29)일)
     */
    // 구간1(하계(300kWh 이하), 기타계절(200kWh 이하) 기본요금 910원 적용
    private static final int LOW_STEP1_STANDARD = 910;

    // 구간2(하계(301kWh ~ 450kWh), 기타계절(201kWh ~ 400kWh) 기본요금 1600원 적용
    private static final int LOW_STEP2_STANDARD = 1600;

    // 구간3(하계(450kWh 초과), 기타계절(400kWh 초과) 기본요금 7300원 적용
    private static final int LOW_STEP3_STANDARD = 7300;

    // 구간1(하계(300kWh 이하), 기타계절(200kWh 이하) 전력량 요금 88.3원 적용
    private static final double LOW_STEP1_ELECT = 88.3;

    // 구간2(하계(301kWh ~ 450kWh), 기타계절(201kWh ~ 400kWh) 전력량 요금 182.9원 적용
    private static final double LOW_STEP2_ELECT = 182.9;

    // 구간3(하계(450kWh 초과), 기타계절(400kWh 초과) 전력량 요금 275.6원 적용
    private static final double LOW_STEP3_ELECT = 275.6;

    // 슈퍼유저요금(하계(1000kWh 초과), 동계(1000kWh 초과)) 전력량 요금 704.5원 적용
    private static final double LOW_STEP4_ELECT = 704.5;


    /**
     * 주택용(고압) 전기요금
     *
     * 하계(7월1일 ~ 8월31일)
     * 기타계절(1월1일 ~ 6월30일, 9월1일 ~ 12월31일)
     * 동계(12월1일 ~ 2월28(29)일)
     */
    // 구간1(하계(300kWh 이하), 기타계절(200kWh 이하) 기본요금 730원 적용
    private static final int HIGH_STEP1_STANDARD = 730;

    // 구간2(하계(301kWh ~ 450kWh), 기타계절(201kWh ~ 400kWh) 기본요금 1260원 적용
    private static final int HIGH_STEP2_STANDARD = 1260;

    // 구간3(하계(450kWh 초과), 기타계절(400kWh 초과) 기본요금 6060원 적용
    private static final int HIGH_STEP3_STANDARD = 6060;

    // 구간1(하계(300kWh 이하), 기타계절(200kWh 이하) 전력량 요금 73.3원 적용
    private static final double HIGH_STEP1_ELECT = 73.3;

    // 구간2(하계(301kWh ~ 450kWh), 기타계절(201kWh ~ 400kWh) 전력량 요금 142.3원 적용
    private static final double HIGH_STEP2_ELECT = 142.3;

    // 구간3(하계(450kWh 초과), 기타계절(400kWh 초과) 전력량 요금 210.6원 적용
    private static final double HIGH_STEP3_ELECT = 210.6;

    // 슈퍼유저요금(하계(1000kWh 초과), 동계(1000kWh 초과)) 전력량 요금 569.6원 적용
    private static final double HIGH_STEP4_ELECT = 569.6;


    /**
     * 200kWh 이하 사용시 필수사용량 보장공제 금액 만큼 추가할인 적용
     * 저압과 고압에 따라 보장공제 금액이 상이함
     */
    // 주택용(저압) 필수사용량 보장공제
    private static final int LOW_REQUIRE_CHARGE = 2000;

    // 주택용(고압) 필수사용량 보장공제
    private static final int HIGH_REQUIRE_CHARGE = 2000;

    // 필수사용량 보장공제 최저금액
    private static final int MINIMUM_REQUIRE_CHARGE = 1000;


    /**
     * 대가족, 복지할인은 경우에 따라 할인금액 제한선이 존재한다.
     * 정액 할인은 복지할인에만 적용되며, 할인과 게절에 따른 금액이 차등 적용됨
     * 정률 할인은 대가족/생명유지장치 할인에 적용되며 모든 계절, 모든 할인이 할인율 균등 적용됨
     * 단, 생명유지장치 요금은 할인액에 한도는 없으나 대가족은 할인금액 제한선이 존재한다.
     */
    // 장애인/유공자 할인 제한금액 하절기 20000원
    private static final int WELFARE1_SUMMER_LIMIT = 20000;

    // 장애인/유공자 할인 제한금액 기타계절 16000원
    private static final int WELFARE1_OTHER_LIMIT = 16000;

    // 기초생활수급자(생계, 의료) 할인 제한금액 하절기 20000원
    private static final int WELFARE2_SUMMER_LIMIT = 20000;

    // 기초생활수급자(생계, 의료) 할인 제한금액 기타계절 16000원
    private static final int WELFARE2_OTHER_LIMIT = 16000;

    // 기초생활수급자(주거, 교육) 할인 제한금액 하절기 12000원
    private static final int WELFARE3_SUMMER_LIMIT = 12000;

    // 기초생활수급자(주거, 교육) 할인 제한금액 기타계절 10000원
    private static final int WELFARE3_OTHER_LIMIT = 10000;

    // 차상위계층 할인 제한금액 하절기 10000원
    private static final int WELFARE4_SUMMER_LIMIT = 10000;

    // 차상위계층 할인 제한금액 기타계절 8000원
    private static final int WELFARE4_OTHER_LIMIT = 8000;

    // 대가족(5인이상/3자녀/출산) 할인 제한금액 계절상관없이 최대 16000원
    private static final int BIG_FAMILY_LIMIT = 16000;

    // 정률 할인 최대 제한 할인율
    private static final double MAXIMUM_PERCENTAGE = 0.3;

    // 전력 사용량
    private int electUsage;

    // 사용 시작 기간(사용자가 입력받은 종료일자 다음날)
    private String start;

    // 사용 종료 기간(실시간이라 오늘 날짜 받아야 함) ----> 사용 시작 기간의 마지막일(30 or 31 or 28/9)을 더한 날짜를 넘어가면 오류 출력!
    private String end;

    // 사용 시즌
    private String season;

    // 사용 용도
    private String use;

    // 1주택 수 가구 정보
    private List<House> houses;

   // 복지 할인 정보
    private String discount1;

    // 대가족 할인 정보
    private String discount2;

    // 기본 요금
    private int basicCharge;

    // 전력사용량 요금
    private int usageCharge;

    // 기후환경 요금
    private int climateCharge;

    // 연료비조정액
    private int fuelRatioCharge;

    // 전기요금계(기본 요금 + 전력사용량 요금)
    private int sumElectCharge;

    // 대가족(출산 가구, 3자녀이상 가구, 5인이상 가구) / 생명유지장치
    private int discountCharge1;

    // 복지할인(장애인/유공자, 기초생활, 차상위계층)
    private int discountCharge2;

    // 부가가치세
    private int taxCharge;

    // 전력산업기반기금
    private int fundCharge;

    // 계산 결과
    private int resultCharge;

    // 필수사용량 보장공제 금액
    private int requireUsageCharge;

    // 1주택 수 가구의 수
    private int houseCount;

    // 가구별 평균 사용량
    private int houseAverage;

    // 1주택 수 가구 전체가구 기본 요금
    private int housesBasicCharge;

    // 1주택 수 가구 할인별 카운팅
    private List<DiscountCount> housesDiscountCount;

    // 1주택 수 가구 복지 할인 금액
    private double housesWelfareCharge;

    // 1주택 수 가구 대가족 할인 금액
    private int housesBigFamCharge;

    // 1주택 수 가구 생명유지장치 할인 금액
    private int housesLifeDeviceCharge;

    // 1주택 수 가구 필수 사용량 보장공제 금액
    private int housesRequireCharge;

    // 월별 사용 일자
    private List<DateWeight> weights;

    // 측정 시작 날짜 분리
    private int[] splitStart;

    // 측정 종료 날짜 분리
    private int[] splitEnd;

    // 측정 시작 날짜 마지막 일자
    private int startLastDay;

    // 측정 종료 날짜 마지막 일자
    private int endLastDay;

    private int bigFamAndStandrdLife1 = 0;

    private int bigFamAndStandrdLife2 = 0;

    // 싱글톤 적용을 위한 인스턴스 선언
    private static ElectCalcHelper instance;

    public ElectCalcHelper() {}

    /**
     * 용도가 주택용일 경우 해당 생성자를 통해 초기화함
     * 생성자를 통해 사용량, 용도, 사용 시작 기간, 계산 계절, 복지 할인, 대가족 할인 입력받음
     *
     * @param electUsage        사용량
     * @param use               용도
     * @param start             사용 시작 기간
     * @param season            계산 계절
     * @param discount1         복지 할인
     * @param discount2         대가족 할인
     */
    private ElectCalcHelper(int electUsage, String use, String start, String end, String season, String discount1, String discount2) {
        this.electUsage = electUsage;
        this.use = use;
        this.start = start;
        this.end = end;
        this.season = season;
        this.discount1 = discount1;
        this.discount2 = discount2;
    }

    /**
     * 용도가 1주택 수 가구일 경우 해당 생성자를 통해 초기화함
     * 생성자를 통해 사용량, 용도, 사용 시작 기간, 계산 계절, 다가구 할인 정보 입력받음
     *
     * @param electUsage        사용량
     * @param use               용도
     * @param start             사용 시작 기간
     * @param season            계산 계절
     * @param houses            다가구 할인 정보
     */
    private ElectCalcHelper(int electUsage, String use, String start, String end, String season, List<House> houses) {
        this.electUsage = electUsage;
        this.use = use;
        this.start = start;
        this.end = end;
        this.season = season;
        this.houses = houses;
        this.houseCount = houses.size();
        this.housesDiscountCount = new ArrayList<>();
        this.housesDiscountCount.add(new DiscountCount("복지 할인", "장애인/유공자", 0));
        this.housesDiscountCount.add(new DiscountCount("복지 할인", "기초생활(생계, 의료)", 0));
        this.housesDiscountCount.add(new DiscountCount("복지 할인", "기초생활(주거, 교육)", 0));
        this.housesDiscountCount.add(new DiscountCount("복지 할인", "차상위계층", 0));
        this.housesDiscountCount.add(new DiscountCount("생명유지장치 할인", "생명유지장치", 0));
        this.housesDiscountCount.add(new DiscountCount("대가족 할인", "대가족", 0));
        this.housesDiscountCount.add(new DiscountCount("필수사용량 보장공제", "필수사용량", 0));

    }

    /**
     * 계산 클래스를 싱글톤으로 구현하여 관리
     *
     * @param info          전기 요금 계산에 필요한 정보를 담은 객체
     */
    public static ElectCalcHelper getInstance(Calculator info) {

        List<House> houses = info.getHouses();

        System.out.println("infooooooo" + info.toString());

        if(info.getUse().equals("1주택 수 가구")) {
            instance = new ElectCalcHelper(info.getElectUsage(), info.getUse(), info.getStart(), info.getEnd(), info.getSeason(), houses);
        } else {
            instance = new ElectCalcHelper(info.getElectUsage(), info.getUse(), info.getStart(), info.getEnd(), info.getSeason(), houses.get(0).getHouseDiscount1(), houses.get(0).getHouseDiscount2());
        }

        return instance;
    }

    /**
     *  init 호출을 통해 사용용도에 따라 계산 함수 호출하여 바로 계산 진행
     *  계산이 정상적으로 진행되면 Formula 함수 호출하여 List로 공식 반환(List 규칙은 각 함수 주석에 표기)
     *
     */
    public void init() {

        if(this.use.equals("1주택 수 가구")) {
            System.out.println("1주택 수 가구 ");
            calcManyHouseElectCharge();
        } else if(this.use.contains("주택용")) {
            System.out.println("주택용여기 ");
            clacOneHouseElectCharge();
        }

    }

    /*
     * 주택용 전력 사용 요금 계산 함수
     *
     *
     */
    public void clacOneHouseElectCharge() {

        // 측정 시작 날짜 분리
        this.splitStart = DateUtil.splitStringDate(this.start);

        // 측정 종료 날짜 분리
        this.splitEnd = DateUtil.splitStringDate(this.end);

        // 측정 시작 날짜의 마지막 일자 계산
        this.startLastDay = DateUtil.getLastDay(this.splitStart[0], this.splitStart[1], this.splitStart[2]);

        // 측정 종료 날짜의 마지막 일자 계산
        this.endLastDay = DateUtil.getLastDay(this.splitStart[0], this.splitStart[1], this.splitStart[2]);

        // 월별 가중치
        this.weights = calcMonthlyWeight();

        // 기본 요금
        this.basicCharge = calcDefaultCharge();

        // 전력사용량 요금
        this.usageCharge = (int)calcElectCharge(this.weights.get(0).getMonthUsageCount(), this.startLastDay);

        // 기후환경 요금 (전력량 요금에 환경 요금 참감액(-5원) 반영되어 있는 금액임 !!)
        this.climateCharge = (int)calcClimateCharge();

        // 연료비조정액
        this.fuelRatioCharge = calcFuelRatioCharge();

        // 필수사용량 보장공제 금액
        this.requireUsageCharge = calcRequireCharge();

        // 대가족(출산, 3자녀, 5인이상) / 생명유지장치
        this.discountCharge1 = calcBigFamDiscount();

        // 복지할인
        this.discountCharge2 = calcWelfareDiscount();

        // 필수 사용량 보장 공제랑 복지할인 중복 안되는거 처리해야함 !@@@@@@@@@@@@@@@@@@@@!!@!@!@!@!@!@

        // 전기요금계(기본 요금 + 전력사용량 요금)
        this.sumElectCharge = this.basicCharge + this.usageCharge + this.climateCharge + this.fuelRatioCharge - this.requireUsageCharge - this.discountCharge1 - this.discountCharge2;
        if(this.sumElectCharge < 0) {
            this.sumElectCharge = 0;
        }

        // 부가가치세
        this.taxCharge = calculatorTax();

        // 전력산업기반기금
        this.fundCharge = calculatorFund();

        // 청구금액(전기요금계 + 부가가치세 + 전력산업기반기금)
        this.resultCharge = ((this.sumElectCharge + this.taxCharge + this.fundCharge) / 10) * 10;
        if(this.resultCharge < 0) {
            this.resultCharge = 0;
        }

    }

    // 1주택 수 가구 전력 사용 요금 계산
    private void calcManyHouseElectCharge() {

        // 측정 시작 날짜 분리
        this.splitStart = DateUtil.splitStringDate(this.start);

        // 측정 종료 날짜 분리
        this.splitEnd = DateUtil.splitStringDate(this.end);

        // 측정 종료료 날짜 분리
        this.startLastDay = DateUtil.getLastDay(this.splitStart[0], this.splitStart[1], this.splitStart[2]);

        // 측정 종료료 날짜 분리
        this.endLastDay = DateUtil.getLastDay(this.splitStart[0], this.splitStart[1], this.splitStart[2]);

        // 월별 가중치
        this.weights = calcMonthlyWeight();

        // 가구별 평균 사용량
        this.houseAverage = getHouseByUsage();

        // 기본 요금
        this.basicCharge = calcDefaultCharge();

        // 전체 가구 기본 요금
        this.housesBasicCharge = this.basicCharge * this.houseCount;

        // 전력사용량 요금
        this.usageCharge = (int)calcElectCharge(this.weights.get(0).getMonthUsageCount(), this.startLastDay);

        // 기후환경 요금
        this.climateCharge = (int)calcClimateCharge();

        // 연료비조정액
        this.fuelRatioCharge = calcFuelRatioCharge();

        // 1주택 수 가구 할인별 카운팅
        countHousesDiscount();

        // 1주택 수 가구 할인 계산
        for(DiscountCount count : this.housesDiscountCount) {

            if(count.getCount() > 0) {
                switch (count.getType()) {
                    case "복지 할인":
                        this.housesWelfareCharge = this.housesWelfareCharge + calcHousesWelfareDiscount(count.getDiscountName());
                        break;
                    case "대가족 할인":
                        this.housesBigFamCharge = this.housesBigFamCharge + calcHousesBigFamDiscount();
                        break;
                    case "생명유지장치 할인":
                        this.housesLifeDeviceCharge = this.housesLifeDeviceCharge + calcHousesLifeDeviceDiscount();
                        break;
                    case "필수사용량 보장공제":
                        this.housesRequireCharge = this.housesRequireCharge + calcHousesRequireCharge();
                        break;
                }
            }
        }

        // 전기요금계(기본 요금 + 전력사용량 요금)
        this.sumElectCharge = this.housesBasicCharge + this.usageCharge + this.climateCharge + this.fuelRatioCharge - this.housesRequireCharge - (int)this.housesWelfareCharge - this.housesBigFamCharge - this.housesLifeDeviceCharge;
        if(this.sumElectCharge < 0) {
            this.sumElectCharge = 0;
        }

        // 부가가치세
        this.taxCharge = calculatorTax();

        // 전력산업기반기금
        this.fundCharge = calculatorFund();

        // 청구금액(전기요금계 + 부가가치세 + 전력산업기반기금)
        this.resultCharge = ((this.sumElectCharge + this.taxCharge + this.fundCharge) / 10) * 10;
        if(this.resultCharge < 0) {
            this.resultCharge = 0;
        }
    }


    /**
     * 필수사용량 보장공제, 기타 할인 금액을 적용하는 경우, 월별 일자수로 사용량 할당을 하기 위한 월별 사용일 가중치 계산
     *
     * @return                      월별 사용일 가중치
     */
    private List<DateWeight> calcMonthlyWeight() {

        List<DateWeight> weights = new ArrayList<>();
        DateWeight weightStart = new DateWeight();
        DateWeight weightEnd = new DateWeight();

        // 2. 주어진 년월일 값으로 월별 가중치를 계산하여 List 생성
        if(this.splitStart[0] == this.splitEnd[0]) {
            weightStart.setMonth(this.splitStart[1]);
            weightStart.setMonthUsageCount((int)DateUtil.getDayCount(DateUtil.convertStringToDate(start), DateUtil.convertStringToDate(end)));
            weightStart.setDayCount(this.startLastDay);
            weights.add(weightStart);
        } else {
            weightStart.setMonth(this.splitStart[1]);
            weightStart.setMonthUsageCount(DateUtil.getMonthUsageCount(start, "start"));
            weightStart.setDayCount(this.startLastDay);
            weights.add(weightStart);

            weightEnd.setMonth(this.splitEnd[1]);
            weightEnd.setMonthUsageCount(DateUtil.getMonthUsageCount(end, "end"));
            weightEnd.setDayCount(this.startLastDay);
            weights.add(weightEnd);
        }

        // 3. 계산 완료된 가중치 리스트 리턴턴
       return weights;
    }

    /**
     * 1주택 수 가구에서 가구별 평균 사용량을 구함
     *
     * @return                      가구별 평균 사용량
     */
    private int getHouseByUsage() {

        return new BigDecimal(this.electUsage / (double)this.houseCount).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 1주택 수 가구 할인 대상 가구의 할인별 카운팅
     *
     */
    private void countHousesDiscount() {

        int count = 0;

        for(House house : this.houses) {

            String dc1 = house.getHouseDiscount1();
            String dc2 = house.getHouseDiscount2();

            // 1. 복지 할인 대상 카운팅
            switch (house.getHouseDiscount2()) {
                case "독립유공자":
                case "국가유공자":
                case "5.18민주유공자":
                case "장애인":
                    count = this.housesDiscountCount.get(0).getCount();
                    this.housesDiscountCount.get(0).setCount(++count);
                    break;
                case "기초생활(생계, 의료)":
                    count = this.housesDiscountCount.get(1).getCount();
                    this.housesDiscountCount.get(1).setCount(++count);
                    break;
                case "기초생활(주거, 교육)":
                    count = this.housesDiscountCount.get(2).getCount();
                    this.housesDiscountCount.get(2).setCount(++count);
                    break;
                case "차상위계층":
                    count = this.housesDiscountCount.get(3).getCount();
                    this.housesDiscountCount.get(3).setCount(++count);
                    break;
            }

            // 2. 대가족 할인 대상 카운팅
            switch (house.getHouseDiscount1()) {
                case "5인이상 가구":
                case "출산 가구":
                case "3자녀이상 가구":
                    count = this.housesDiscountCount.get(5).getCount();
                    this.housesDiscountCount.get(5).setCount(++count);
                    break;
                case "생명유지장치":
                    count = this.housesDiscountCount.get(4).getCount();
                    this.housesDiscountCount.get(4).setCount(++count);
                    break;
            }

            if(dc2.equals("기초생활(생계, 의료)") && (dc1.equals("5인이상 가구") || dc1.equals("출산 가구") || dc1.equals("3자녀이상 가구"))) {
                bigFamAndStandrdLife1 = 1;
            } else if(dc2.equals("기초생활(주거, 교육)") && (dc1.equals("5인이상 가구") || dc1.equals("출산 가구") || dc1.equals("3자녀이상 가구"))) {
                bigFamAndStandrdLife2 = 1;
            }
        }


        if(this.houseAverage <= 200) {
            count = this.housesDiscountCount.get(6).getCount();
            this.housesDiscountCount.get(6).setCount(this.houseCount);
        }
    }

    /**
     * 기본 요금을 계산하는 함수
     *
     * @return                      기본 요금
     */
    private int calcDefaultCharge() {

        int result = 0;
        int target = 0;
        if(this.use.equals("1주택 수 가구")) {
            target = this.houseAverage;
        } else {
            target = this.electUsage;
        }

        // 1. 용도 비교(저압/고압), 1주택 수 가구는 저압
        if(this.use.equals("주택용(저압)") || this.use.equals("1주택 수 가구")) {
            // 2. 계절 비교(하계/기타계절)
            if(this.season.equals("summer")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인)
                if(target <= 300) {
                    result = LOW_STEP1_STANDARD;
                } else if(target <= 450) {
                    result = LOW_STEP2_STANDARD;
                } else {
                    result = LOW_STEP3_STANDARD;
                }
            } else if(this.season.equals("other") || this.season.equals("winter")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인)
                if(target <= 200) {
                    result = LOW_STEP1_STANDARD;
                } else if(target <= 400) {
                    result = LOW_STEP2_STANDARD;
                } else {
                    result = LOW_STEP3_STANDARD;
                }
            }
        } else {
            // 2. 계절 비교(하계/기타계절)
            if(this.season.equals("summer")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인)
                if(target <= 300) {
                    result = HIGH_STEP1_STANDARD;
                } else if(target <= 450) {
                    result = HIGH_STEP2_STANDARD;
                } else {
                    result = HIGH_STEP3_STANDARD;
                }
            } else if(this.season.equals("other") || this.season.equals("winter")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인)
                if(target <= 200) {
                    result = HIGH_STEP1_STANDARD;
                } else if(target <= 400) {
                    result = HIGH_STEP2_STANDARD;
                } else {
                    result = HIGH_STEP3_STANDARD;
                }
            }
        }

        return result;
    }

    /**
     * 전력 사용량 요금계를 계산하는 함수
     *
     * @return                      전력 사용량 요금계
     */
    private double calcElectCharge(int usageDayCount, int monthDayCount) {

        double result = 0.0;
        int interval = 0;

        // 월별 사용량 가중치 값에 따라 전력 사용량 구함
//        double useElect = new BigDecimal(((double)this.electUsage / monthDayCount) * usageDayCount).setScale(2, RoundingMode.HALF_UP).doubleValue();
//        if(this.use.equals("1주택 수 가구")) {
//            useElect = (double)this.electUsage;
//        }
        double useElect = (double)this.electUsage;
        int target = this.electUsage;

        // 1. 용도 비교(저압/고압), 1주택 수 가구는 저압
        if(this.use.equals("주택용(저압)")) {
            // 2. 계절 비교(하계/기타계절), 기타계절인 경우 동계여부 확인
            if (this.season.equals("summer")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인), 슈퍼유저여부 확인
                if(target <= 300) {
                    interval = 1;
                } else if(target <= 450) {
                    interval = 2;
                } else if(target <= 1000) {
                    interval = 3;
                } else {
                    interval = 4;
                }

                // 4. 요금 구간 구한다음 바로 계산
                switch (interval) {
                    case 1 :
                        result = result + (useElect * LOW_STEP1_ELECT);
                        break;
                    case 2 :
                        result = result + (300 * LOW_STEP1_ELECT);
                        result = result + ((useElect - 300) * LOW_STEP2_ELECT);
                        break;
                    case 3 :
                        result = result + (300 * LOW_STEP1_ELECT);
                        result = result + (150 * LOW_STEP2_ELECT);
                        result = result + ((useElect - 450) * LOW_STEP3_ELECT);
                        break;
                    case 4 :
                        result = result + (300 * LOW_STEP1_ELECT);
                        result = result + (150 * LOW_STEP2_ELECT);
                        result = result + (550 * LOW_STEP3_ELECT);
                        result = result + ((useElect - 1000) * LOW_STEP4_ELECT);
                        break;
                }
            } else if (season.equals("other") || season.equals("winter")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인), 슈퍼유저여부 확인
                if(target <= 200) {
                    interval = 1;
                } else if(target <= 400) {
                    interval = 2;
                } else if(target <= 1000) {
                    interval = 3;
                } else {
                    interval = 4;
                }

                // 4. 요금 구간 구한다음 바로 계산
                switch (interval) {
                    case 1 :
                        result = result + (useElect * LOW_STEP1_ELECT);
                        break;
                    case 2 :
                        result = result + (200 * LOW_STEP1_ELECT);
                        result = result + ((useElect - 200) * LOW_STEP2_ELECT);
                        break;
                    case 3 :
                        result = result + (200 * LOW_STEP1_ELECT);
                        result = result + (200 * LOW_STEP2_ELECT);
                        result = result + ((useElect - 400) * LOW_STEP3_ELECT);
                        break;
                    case 4 :
                        result = result + (200 * LOW_STEP1_ELECT);
                        result = result + (200 * LOW_STEP2_ELECT);
                        result = result + (600 * LOW_STEP3_ELECT);
                        result = result + ((useElect - 1000) * LOW_STEP4_ELECT);
                        break;
                }
            }
        } else if(this.use.equals("1주택 수 가구")) {
            if (this.season.equals("summer")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인), 슈퍼유저여부 확인
                if(target <= 600) {
                    interval = 1;
                } else if(target <= 900) {
                    interval = 2;
                } else if(target <= 2000) {
                    interval = 3;
                } else {
                    interval = 4;
                }

                // 4. 요금 구간 구한다음 바로 계산
                switch (interval) {
                    case 1 :
                        result = result + (useElect * LOW_STEP1_ELECT);
                        break;
                    case 2 :
                        result = result + (600 * LOW_STEP1_ELECT);
                        result = result + ((useElect - 600) * LOW_STEP2_ELECT);
                        break;
                    case 3 :
                        result = result + (600 * LOW_STEP1_ELECT);
                        result = result + (300 * LOW_STEP2_ELECT);
                        result = result + ((useElect - 900) * LOW_STEP3_ELECT);
                        break;
                    case 4 :
                        result = result + (600 * LOW_STEP1_ELECT);
                        result = result + (300 * LOW_STEP2_ELECT);
                        result = result + (1100 * LOW_STEP3_ELECT);
                        result = result + ((useElect - 2000) * LOW_STEP4_ELECT);
                        break;
                }
            } else if (season.equals("other") || season.equals("winter")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인), 슈퍼유저여부 확인
                if(target <= 400) {
                    interval = 1;
                } else if(target <= 800) {
                    interval = 2;
                } else {
                    interval = 3;
                }

                // 4. 요금 구간 구한다음 바로 계산
                switch (interval) {
                    case 1 :
                        result = result + (useElect * LOW_STEP1_ELECT);
                        break;
                    case 2 :
                        result = result + (400 * LOW_STEP1_ELECT);
                        result = result + ((useElect - 400) * LOW_STEP2_ELECT);
                        break;
                    case 3 :
                        result = result + (400 * LOW_STEP1_ELECT);
                        result = result + (400 * LOW_STEP2_ELECT);
                        result = result + ((useElect - 800) * LOW_STEP3_ELECT);
                        break;
                }
            }
        } else {
            // 2. 계절 비교(하계/기타계절), 기타계절인 경우 동계여부 확인
            if(this.season.equals("summer")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인), 슈퍼유저여부 확인

                if(target <= 300) {
                    interval = 1;
                } else if(target <= 450) {
                    interval = 2;
                } else if(target <= 1000) {
                    interval = 3;
                } else {
                    interval = 4;
                }

                // 4. 요금 구간 구한다음 바로 계산
                switch (interval) {
                    case 1 :
                        result = result + (useElect * HIGH_STEP1_ELECT);
                        break;
                    case 2 :
                        result = result + (300 * HIGH_STEP1_ELECT);
                        result = result + ((useElect - 300) * HIGH_STEP2_ELECT);
                        break;
                    case 3 :
                        result = result + (300 * HIGH_STEP1_ELECT);
                        result = result + (150 * HIGH_STEP2_ELECT);
                        result = result + ((useElect - 450) * HIGH_STEP3_ELECT);
                        break;
                    case 4 :
                        result = result + (300 * HIGH_STEP1_ELECT);
                        result = result + (150 * HIGH_STEP2_ELECT);
                        result = result + (550 * HIGH_STEP3_ELECT);
                        result = result + ((useElect - 1000) * HIGH_STEP4_ELECT);
                        break;
                }
            } else if(season.equals("other") || season.equals("winter")) {
                // 3. 구간 비교(1, 2, 3단계 중에 어디에 속하는지 확인), 슈퍼유저여부 확인
                if(target <= 200) {
                    interval = 1;
                } else if(target <= 400) {
                    interval = 2;
                } else if(target <= 1000) {
                    interval = 3;
                } else {
                    interval = 4;
                }

                // 4. 요금 구간 구한다음 바로 계산
                switch (interval) {
                    case 1 :
                        result = result + (useElect * HIGH_STEP1_ELECT);
                        break;
                    case 2 :
                        result = result + (200 * HIGH_STEP1_ELECT);
                        result = result + ((useElect - 200) * HIGH_STEP2_ELECT);
                        break;
                    case 3 :
                        result = result + (200 * HIGH_STEP1_ELECT);
                        result = result + (200 * HIGH_STEP2_ELECT);
                        result = result + ((useElect - 400) * HIGH_STEP3_ELECT);
                        break;
                    case 4 :
                        result = result + (200 * HIGH_STEP1_ELECT);
                        result = result + (200 * HIGH_STEP2_ELECT);
                        result = result + (600 * HIGH_STEP3_ELECT);
                        result = result + ((useElect - 1000) * HIGH_STEP4_ELECT);
                        break;
                }
            }
        }

        // 전력 사용량과 전령량 요금을 곱하여 전력사용량 요금 구함
        return new BigDecimal(result).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }


    /**
     * 필수사용량 보장공제 금액을 계산하는 함수
     * 저소득측 전기 요금 부담 완화를 위해 도입한 제도
     *
     * @return                      필수 사용량 보장공제 할인 금액
     */
    private int calcRequireCharge() {

        int result = 0;

        // 1. 사용량이 200 이하인 경우 필수사용량 보장공제 금액 대상이므로 가능 여부 판단
        if(this.electUsage <= 200) {

            int requireUsageCharge = 0;

            // 저압 또는 고압에 따른 필수사용량 보장공제 금액 결정
            if(this.use.equals("주택용(저압)") || this.use.equals("1주택 수 가구")) {
                requireUsageCharge = LOW_REQUIRE_CHARGE;
            } else if(use.equals("주택용(고압)")) {
                requireUsageCharge = HIGH_REQUIRE_CHARGE;
            }

            // 3. 월별 일자 가중치 계산 결과에 따라 적용 가능한 필수사용량 보장공제 금액 계산(전력 요금계 - 필수사용량 보장공제액)
            for(DateWeight weight : this.weights) {
                // 해당 월의 사용일 수
                double usageCount = weight.getMonthUsageCount();

                // 해당 월의 전체일 수
                double monthCount = weight.getDayCount();

                // 해당 월의 전력 사용량 요금계
                double electCharge = calcElectCharge(weight.getMonthUsageCount(), weight.getDayCount());

                // 해당 월의 사용일 수에 따른 기본요금 가중치
                double value1 = new BigDecimal((this.basicCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 기후환경요금 가중치
                double value3 = new BigDecimal((this.climateCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 연료비조정액 가중치
                double value4 = new BigDecimal((this.fuelRatioCharge / monthCount) * usageCount).setScale(0, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 필수사용량 보장공제 가중치
                double value5 = new BigDecimal((requireUsageCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 필수사용량 보장공제 최소금액 가중치
                double target1 = new BigDecimal((MINIMUM_REQUIRE_CHARGE / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 필수사용량 보장공제 금액 계산
                double target2 = value1 + electCharge + value3 - Math.abs(value4) - value5;

                // 4. 계산된 필수사용량 보장 금액이 최저 보장액인 1000원보다 작은지 비교
                if(target1 < target2) {
                    result = requireUsageCharge;
                } else {
                    // 5. 1000원보다 작을 경우, 4000 - (1000 - 필수사용량 보장 금액) 으로 계산, 월별 일자 가중치에 따라 구분하여 계산해야 함
                    result = result + (int)(requireUsageCharge - Math.abs(target2 - target1));
                }
            }
        }

        return result;
    }

    /**
     * 1주택 수 가구 필수 사용량 보장 공제 금액을 계산하는 함수
     *
     * @return                      필수 사용량 보장공제 할인 금액
     */
    private int calcHousesRequireCharge() {

        int result = 0;

        int housesRequireUsageCharge = 0;

        if(this.houseAverage <= 200) {
            // 0. 주택 수 가구 필수사용량 보장공제 금액 결정
            housesRequireUsageCharge = LOW_REQUIRE_CHARGE;

            // 1. 필수 사용량 보장공제 카운팅 받아옴
            int requireChargeCount = this.housesDiscountCount.get(6).getCount();

            // 3. 1주택 수 가구는 월별 가중치 계산을 하지 않음, 그대신 대상 가구에 대한 금액계를 계산해야함
            // 전체 가구 수 대비 필수 사용량 보장공제 대상 가구 수 비율
            double countRate = new BigDecimal((double)requireChargeCount / this.houseCount).setScale(4, RoundingMode.DOWN).doubleValue();

            // 대상 가구 전체의 최소 필수 사용량 보장공제 금액계
            double target1 = MINIMUM_REQUIRE_CHARGE * requireChargeCount;

            // 전체 가구 전력 요금계에서 필수 사용량 보장공제 대상 가구 할인 금액
            double target2 = (this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) * countRate;

            // 대상 가구 전체의 필수 사용량 보장공제 금액계
            double target3 = housesRequireUsageCharge * requireChargeCount;


            // 4. 가구 전체 전력 요금계와 필수 사용량 보장공제 금액계를 비교하고 최종 금액 결정
            if(target2 < target3) {
                // 필수 사용량 보장공제 금액계가 클 경우, 가구 전체 전력 요금계에서 필수 사용량 보장 공제 최소 보장액을 뺀 값을 리턴
                result = (int)(target2 - target1);
            } else {
                // 필수 사용량 보장공제 금액계가 작을 경우, 그대로 필수 사용량 보장공제 금액계를 리턴
                result = (int)target3;
            }

        }

        return result;
    }

    /**
     * 대가족 할인 금액을 계산하는 함수
     *
     * @return                      대가족 할인 금액
     */
    private int calcBigFamDiscount() {

        // 최종 결과 액수를 담아 리턴하는 변수
        int result = 0;

        // 할인 최대치를 담아두는 변수
        int limitDiscount = 0;

        // 2. 대가족 / 생명유지장치 여부 판단
        if(!this.discount1.equals("생명유지장치")) {
            limitDiscount = BIG_FAMILY_LIMIT;
        }

        // 필수사용량 보장공제를 먼저 계산하여 멤버변수인 supplyCharge에 값이 있어야 함!
        // 3. 월별 일자 가중치 계산 결과에 따라 할인 금액 계산(전기요금계 - 필수사용량 보장공제(있을경우)) x 30% = 결과)
        if(!this.discount1.equals("해당사항없음")) {
            for(DateWeight weight : this.weights) {
                // 해당 월의 사용일 수
                double usageCount = weight.getMonthUsageCount();

                // 해당 월의 전체일 수
                double monthCount = weight.getDayCount();

                // 해당 월의 전력 사용량 요금계
                double electCharge = calcElectCharge(weight.getMonthUsageCount(), weight.getDayCount());

                // 해당 월의 사용일 수에 따른 기본요금 가중치
                double value1 = new BigDecimal((this.basicCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 기후환경요금 가중치
                double value3 = new BigDecimal((this.climateCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 연료비조정액 가중치
                double value4 = new BigDecimal((this.fuelRatioCharge / monthCount) * usageCount).setScale(0, RoundingMode.HALF_UP).doubleValue();

                double value5 = this.requireUsageCharge;

                // 해당 월의 사용일 수에 따른 필수사용량 보장공제 가중치
                if(this.weights.size() > 1) {
                    value5 = new BigDecimal((this.requireUsageCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();
                }

                // 해당 월의 할인 한도액, 만약 0일 경우는 생명유지장치 할인이므로 한도가 없다.
                double target1 = new BigDecimal((limitDiscount / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 대가족 할인 금액 계산
                double target2 = new BigDecimal((value1 + electCharge + value3 - Math.abs(value4) - value5) * MAXIMUM_PERCENTAGE).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 4. 대가족할인의 경우, 계산 결과가 할인 한도 금액보다 높은지 비교, 높을 경우에는 한도액 만큼만 할인 적용 (생명유지장치는 적용하지 않음)
                if(target1 == 0) {
                    result = result +(int)target2;
                } else {
                    if(target1 < target2) {
                        result = limitDiscount;
                    } else {
                        result = result + (int)target2;
                    }
                }
            }
        }
        // 5. 최종 계산 금액 리턴
        return result;
    }

    /**
     * 1주택 수 가구 대가족 할인 금액을 구하는 함수
     *
     * @return                      대가족 할인 금액
     */
    private int calcHousesBigFamDiscount() {

        // 최종 결과 액수를 담아 리턴하는 변수
        int result = 0;

        // 1. 할인 최대치 확인
        int limitDiscount = BIG_FAMILY_LIMIT;

        // 2. 대가족 할인 대상 가구 카운트 받아옴
        int bigFamCount = this.housesDiscountCount.get(5).getCount();

        // 3. 전체 가구 수 대비 대상 가구 수로 비율 구함
        double bigFamRate = new BigDecimal((double)bigFamCount / this.houseCount).setScale(4, RoundingMode.DOWN).doubleValue();

        // 4. 전체 가구 전력 요금계에 대상가구 비율 및 할인 최대치 비율 적용
        double target1 = ((this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) * bigFamRate) * MAXIMUM_PERCENTAGE;

        // 5. 필수 사용량 보장공제 적용 가구가 있다면, 중복분을 제외한다.
        if(this.housesDiscountCount.get(6).getCount() > 0) {
            target1 = (((this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) * bigFamRate) - (LOW_REQUIRE_CHARGE * this.housesDiscountCount.get(6).getCount())) * MAXIMUM_PERCENTAGE;
        }

        // 6. 기초생활 대상 할인요금 중복분 제외
        if(bigFamAndStandrdLife1 == 1) {
            target1 = ((this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) - calcHousesWelfareDiscount("기초생활(생계, 의료)")) * MAXIMUM_PERCENTAGE;
        }
        // 6. 기초생활 대상 할인요금 중복분 제외
        if(bigFamAndStandrdLife2 == 1) {
            target1 = (((this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) * bigFamRate) - calcHousesWelfareDiscount("기초생활(주거, 교육)")) * MAXIMUM_PERCENTAGE;
        }

        // 7. 마이너스 값이 나오면 그냥 0으로 처리
        if(target1 < 0) {
            target1 = 0;
        }

        double target2 = limitDiscount * bigFamCount;

        // 8. 할인 상한선을 넘는지 확인하고 넘으면 상한가 적용
        if(target1 < target2) {
            result = (int)target1;
        } else {
            result = (int)target2;
        }

        // 9. 최종 계산 금액 리턴
        return result;
    }

    /**
     * 1주택 수 가구 생명유지장치 할인 금액을 구하는 함수
     *
     * @return                      생명유지장치 할인 금액
     */
    private int calcHousesLifeDeviceDiscount() {

        // 최종 결과 액수를 담아 리턴하는 변수
        int result = 0;

        // 1. 생명유지장치 할인 해당 가구 카운트 받아옴
        int lifeDeviceCount = this.housesDiscountCount.get(4).getCount();

        // 2. 전체 가구 수 대비 대상 가구 수로 비율 구함함
        double lifeDeviceRate = new BigDecimal((double)lifeDeviceCount / this.houseCount).setScale(4, RoundingMode.DOWN).doubleValue();

        // 3. 전체 가구 전력 요금계에 대상가구 비율 및 할인 최대치 비율 적용
        return (int)(((this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) * lifeDeviceRate) * MAXIMUM_PERCENTAGE);
    }

    /**
     * 복지 할인 대상 가구 할인 제한금액을 구하는 함수
     *
     * @return                      복지 할인 제한금액
     */
    private int getWelfareLimitDiscount(String discount) {

        int limitDiscount = 0;

        // 0. 사용 계절
        if(this.season.equals("summer")) {
            // 1. 어떤 복지 할인인지 확인
            switch (discount) {
                case "해당사항없음":
                    limitDiscount = 0;
                    break;
                case "장애인/유공자":
                    limitDiscount = WELFARE1_SUMMER_LIMIT;
                    break;
                case "기초생활(생계, 의료)":
                    limitDiscount = WELFARE2_SUMMER_LIMIT;
                    break;
                case "기초생활(주거, 교육)":
                    limitDiscount = WELFARE3_SUMMER_LIMIT;
                    break;
                case "차상위계층":
                    limitDiscount = WELFARE4_SUMMER_LIMIT;
                    break;
            }
        } else if(this.season.equals("other") || this.season.equals("winter")) {
            // 1. 어떤 복지 할인인지 확인
            switch (discount) {
                case "해당사항없음":
                    limitDiscount = 0;
                    break;
                case "장애인/유공자":
                    limitDiscount = WELFARE1_OTHER_LIMIT;
                    break;
                case "기초생활(생계, 의료)":
                    limitDiscount = WELFARE2_OTHER_LIMIT;
                    break;
                case "기초생활(주거, 교육)":
                    limitDiscount = WELFARE3_OTHER_LIMIT;
                    break;
                case "차상위계층":
                    limitDiscount = WELFARE4_OTHER_LIMIT;
                    break;
            }
        }

        return limitDiscount;
    }

    /**
     * 복지 할인 요금을 계산하는 함수
     *
     * @return                      복지 할인 금액
     */
    private int calcWelfareDiscount() {

        // 최종 결과 액수를 담아 리턴하는 변수
        int result = 0;

        // 1. 복지 할인 제한금액을 구함
        int limitDiscount = getWelfareLimitDiscount(this.discount2);

        // 2. 복지할인 요금 계산 (전력 요금계 > 복지 할인 유형에 따른 할인 한도액), 전기 요금계가 클 경우 한도액 적용, 전력 요금계가 작을 경우 요금계 만큼 할인
        if(!this.discount2.equals("해당사항없음")) {
            for(DateWeight weight : this.weights) {
                // 해당 월의 사용일 수
                double usageCount = weight.getMonthUsageCount();

                // 해당 월의 전체일 수
                double monthCount = weight.getDayCount();

                // 해당 월의 전력 사용량 요금계
                double electCharge = calcElectCharge(weight.getMonthUsageCount(), weight.getDayCount());

                // 해당 월의 사용일 수에 따른 기본요금 가중치
                double value1 = new BigDecimal((this.basicCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 기후환경요금 가중치
                double value3 = new BigDecimal((this.climateCharge / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 사용일 수에 따른 연료비조정액 가중치
                double value4 = new BigDecimal((this.fuelRatioCharge / monthCount) * usageCount).setScale(0, RoundingMode.HALF_UP).doubleValue();

                // 해당 월의 복지 할인 한도액
                double target1 = new BigDecimal((limitDiscount / monthCount) * usageCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

                // 복지 할인 금액 계산
                double target2 = new BigDecimal(value1 + electCharge + value3 - Math.abs(value4)).setScale(1, RoundingMode.HALF_UP).doubleValue();

                if(target2 <= target1) {
                    result = result + (int)target2;
                } else {
                    result = result + (int)target1;
                }
            }
        }

        if(result > limitDiscount) {
            result = limitDiscount;
        }

        // 3. 할인 금액 리턴
        return result;
    }

    /**
     * 1주택 수 가구의 개별 복지 할인을 계산하는 함수
     *
     * @param discount              복지 할인 정보
     * @return                      해당 복지 할인 계산 금액
     */
    private double calcHousesWelfareDiscount(String discount) {

        // 최종 결과 액수를 담아 리턴하는 변수
        double result = 0;

        // 1. 해당 가구의 복지 할인 제한금액을 구함
        int limitDiscount = getWelfareLimitDiscount(discount);

        // 2. 파라미터로 받아온 복지 할인에 해당 가구 카운트 받아옴
        int welfareCount = 0;

        for(DiscountCount count : this.housesDiscountCount) {

            if(count.getDiscountName().equals(discount)) {
                welfareCount = count.getCount();
            }

        }

        // 3. 전체 가구 수 대비 대상 가구 수로 비율 구함
        double welfareRate = new BigDecimal((double)welfareCount / this.houseCount).setScale(4, RoundingMode.HALF_UP).doubleValue();

        // 4. 전체 가구 전력 요금계에 대상가구 비율 및 할인 최대치 비율 적용
        double target1 = (this.housesBasicCharge + this.usageCharge + this.climateCharge - Math.abs(this.fuelRatioCharge)) * welfareRate;
        double target2 = limitDiscount * welfareCount;

        // 5. 할인 금액과 할인 제한금액을 비교
        if(target1 < target2) {
            result = target1;
        } else {
            result = target2;
        }

        // 6. 할인 금액 리턴
        return result;
    }

    /**
     * 환경비용차감액을 계산하는 함수
     * 개편전요금에 기후환경비용을 분리 부과하여 차감한다. 기후환경요금 항목이 21년 1월 완전 분리되어 생겼기 때문에 개편전요금에서 기존 기후환경비용을 차감해야 한다.
     * 이미 전력량 요금에 -5원이 반영되어 있어 별도로 함수를 호출하지 않아도 됨! 상세내역에서 호출할 일이 있을까 싶어 놔둠!
     * 기후환경비용 : RPS 비용(4.5원) + ETS 비용(0.5원) = 합계(5원)
     *
     *
     * @return                      환경비용차감액
     */
    private int calcEnvironmentCharge() {

        // 공식(사용량 x -5원)
        return this.electUsage * -5;
    }

    /**
     * 기후환경요금을 계산하는 함수
     * 신재생에너지 의무이행 비용(RPS), 온실가스 배출권 거래 비용(ETS), 미세먼지 계절 관리제 시행 등에 따른 석탄발전 감축 비용 등 발전업체가 환경오염 영향을 줄이기 위해 지출한 비용
     * 기후환경 요금 단가(21년 1월부터 적용) : RPS 비용(4.5원) + ETS 비용(0.5원) + 석탄발전 감축비용(0.3원) = 합계(5.3원)
     *
     *
     * @return                      기후환경요금
     */
    private double calcClimateCharge() {

        double result = 0.0;

        // 공식(사용량 x 5.3원)
        if(this.use.equals("1주택 수 가구")) {
            result = (this.houseAverage * 5.3) * this.houseCount;
        } else {
            result = this.electUsage * 5.3;
        }

        return result;
    }

    /**
     * 연료비조정액을 계산하는 함수
     * 연료비조정단가 = 기준연료비(요금개정월 기준 최근 1년간 평균연료비) - 실적연료비(적용월 기준 4~2개월전 평균연료비)
     * 국제유가에 따라 분기별로 연료비조정단가 변동될 수 있음, 국제유가가 올라가면 현재는 마이너스이지만 플러스로 변경될 수 있음
     *
     *
     * @return                      연료비조정액
     */
    private int calcFuelRatioCharge() {

        int result = 0;

        // 공식(사용량 x -3원)
        if(this.use.equals("1주택 수 가구")) {
            result = (this.houseAverage * -3) * this.houseCount;
        } else {
            result = this.electUsage * -3;
        }

        return result;
    }


    /**
     * 계산된 전기요금에서 부가가치세를 구하는 함수
     * 부가가치세 = 상품(재화, 여기선 전력)의 거래나 서비스의 제공과정에서 얻어지는 부가가치(이윤)에 대하여 과세하는 세금
     * 전기 요금의 부가가치세는 10%
     *
     *
     * @return                      부가가치세
     */
    private int calculatorTax() {

        int result = this.sumElectCharge * 10 / 100;

//        return ((result + 5) / 10) * 10;
        return (result / 10) * 10;
    }

    /**
     * 계산된 전기요금에서 전력산업기반기금을 구하는 함수
     * 전력산업의 지속적인 발전과 전력산업의 기반조성에 필요한 재원을 확보하기 위하여 전력산업기반기금을 설치
     * 기금조성 부담금은 전기사용자의 전기요금의 1천분의 37에 해당하는 금액으로 함
     *
     *
     * @return                      전력산업기반기금
     */
    private int calculatorFund() {

        int result = (int)(this.sumElectCharge * 3.7 / 100);

        return (result / 10) * 10;
    }


    public List<String> getFormula() {

        List<String> formulas = new ArrayList<String>();

        // 1. 전기요금계
        // 전기요금계(기본요금 ＋ 전력량요금 ＋ 기후환경요금 ± 연료비조정액 － 필수사용량 보장공제 － 복지할인 － 3자녀/대가족/출산가구)
        String electTotal = "";
        if(this.use.equals("1주택 수 가구")) {
            electTotal = this.housesBasicCharge + "원 ＋ " +
                        this.usageCharge + "원 ＋ " +
                        this.climateCharge +"원 ＋ " +
                        this.fuelRatioCharge + "원 － " +
                        this.requireUsageCharge + "원 － " +
                        this.housesRequireCharge + "원 － " +
                        (int)this.housesWelfareCharge + "원 － " +
                        this.housesBigFamCharge + "원 ＝ " +
                        this.housesLifeDeviceCharge + "원";
        } else {
            electTotal = this.basicCharge + "원 ＋ " +
                        this.usageCharge + "원 ＋ " +
                        this.climateCharge +"원 ＋ " +
                        this.fuelRatioCharge + "원 － " +
                        this.requireUsageCharge + "원 － " +
                        this.discountCharge2 + "원 － " +
                        this.discountCharge1 + "원 ＝ " +
                        this.sumElectCharge + "원";
        }
        formulas.add(electTotal);

        // 2. 부가가치세(원미만 4사 5입)
        String taxCharge = this.sumElectCharge + "원 × 0.1 ＝ " + this.taxCharge + "원(원미만 4사 5입)";
        formulas.add(taxCharge);

        // 3. 전력산업기반기금(10원미만 절사)
        String fundCharge = this.sumElectCharge + "원 × 0.037 ＝ " + this.fundCharge + "원(10원미만 절사)";
        formulas.add(fundCharge);

        String totalCharge = this.sumElectCharge + "원 + " + this.taxCharge + "원 + " + this.fundCharge + "원 = " + this.resultCharge + "원(10원미만 절사)";
        formulas.add(totalCharge);

        return formulas;
    }

    // 추후 사용할 예정!
    public List<String> getDetailFormula(String type) {

        List<String> formulas = new ArrayList<String>();

        // 1. 기본 요금
        String defaultCharge = " ×" + " " + " = " + "" + "원";
        formulas.add(defaultCharge);

        // 2. 전력량 요금
        String electCharge = "kWh ×" + " 원 - " +" 원(환경비용차감) = " + "원";
        formulas.add(electCharge);

        // 3. 기후환경요금
        String environmentCharge = "kWh ×" + "5.3원 = " + "원";
        formulas.add(environmentCharge);

        // 4. 연료비조정액
        String fuelCharge = "kWh ×" + "-3원 = " + "원";
        formulas.add(fuelCharge);

        // 5. 필수사용량 보장공제(해당시)
        String requireCharge = getRequireChargeFormula();
        formulas.add(requireCharge);

        // 6. 복지 할인(해당시)
        String welfareCharge = getWelfareChargeFormula();
        formulas.add(welfareCharge);

        // 7. 대가족 할인(해당시)
        String bigfamCharge = getBigfamChargeFormula();
        formulas.add(bigfamCharge);

        // 8. 가구별 평균 사용량(해당시)
        String manyHouseAvg = "";

        if(type.equals("1주택 수 가구")) {

            manyHouseAvg = "kWh ÷ " + "가구 = " + "kWh";
            formulas.add(manyHouseAvg);

        }

        return formulas;
    }

    // 추후 사용할 예정!
    private String getRequireChargeFormula() {

        return "";
    }

    // 추후 사용할 예정!
    private String getWelfareChargeFormula() {

        return "";
    }

    // 추후 사용할 예정!
    private String getBigfamChargeFormula() {

        return "";
    }

    /**
     * 일정 정수를 금액 표시 포맷으로 변환하는 함수
     *
     * @param money 계산된 일반 금액 정수
     */
    private String convertMoneyFormat(int money) {

        DecimalFormat formatter = new DecimalFormat("#,##0");
        String result = formatter.format(money);

        return result;
    }

    public int getResultCharge() {
        return this.resultCharge;
    }
}
