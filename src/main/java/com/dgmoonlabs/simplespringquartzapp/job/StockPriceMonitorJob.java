package com.dgmoonlabs.simplespringquartzapp.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
public class StockPriceMonitorJob extends QuartzJobBean {
    private static final String[] SYMBOLS = {"005930", // 삼성전자
            "000660", // SK하이닉스
            "373220", // LG에너지솔루션
            "207940", // 삼성바이오로직스
            "005380", // 현대차
            "012450", // 한화에어로스페이스
            "068270", // 셀트리온
            "000270", // 기아
            "105560" // KB금융
    };
    public static final String URL = "https://fchart.stock.naver.com/sise.nhn?symbol=%s&timeframe=day&count=3000&requestType=0";
    private final RestTemplate restTemplate;

    @Override
    protected void executeInternal(final JobExecutionContext context) {
        String lastDate = getLastBusinessDay();

        try {
            FileWriter fileWriter = new FileWriter(String.format("output/stock-price.%s.txt", lastDate));
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            StringBuilder data = new StringBuilder(20);
            for (String symbol : SYMBOLS) {
                ResponseEntity<String> response = restTemplate.getForEntity(String.format(URL, symbol), String.class);
                log.info("lastDate: {}", lastDate);

                Pattern rowPattern = Pattern.compile(lastDate + "\\|[0-9]+\\|[0-9]+\\|[0-9]+\\|[0-9]+");
                Matcher rowMatcher = rowPattern.matcher(Objects.requireNonNull(response.getBody()));

                if (rowMatcher.find()) {
                    String price = rowMatcher.group().split("\\|")[4];

                    data.append(symbol)
                            .append(":")
                            .append(price)
                            .append("\n");
                    log.info("{} price = {}", symbol, price);
                }
            }
            bufferedWriter.append(data);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            log.error("Job Error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private String getLastBusinessDay() {
        LocalDateTime localDateTime = switch (LocalDateTime.now().minusDays(1).getDayOfWeek()) {
            case SATURDAY -> LocalDateTime.now().minusDays(2);
            case SUNDAY -> LocalDateTime.now().minusDays(3);
            default -> LocalDateTime.now().minusDays(1);
        };
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}