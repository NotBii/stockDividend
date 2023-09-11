package com.zerobase.stock.scheduler;

import com.zerobase.stock.model.Company;
import com.zerobase.stock.model.ScrapedResult;
import com.zerobase.stock.persist.entity.CompanyEntity;
import com.zerobase.stock.persist.entity.DividendEntity;
import com.zerobase.stock.persist.repository.CompanyRepository;
import com.zerobase.stock.persist.repository.DividendRepository;
import com.zerobase.stock.scrapper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;
    //일정주기마다 수행
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        //저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        //회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scrapping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                                    .name(company.getName())
                                    .ticker(company.getTicker())
                                    .build());
            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값 저장
            scrapedResult.getDividends().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적 스크래핑 요청 방지
            try {
                Thread.sleep(3000); //3sec
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }
}
