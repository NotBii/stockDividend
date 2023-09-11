package com.zerobase.stock.service;

import com.zerobase.stock.model.Company;
import com.zerobase.stock.model.ScrapedResult;
import com.zerobase.stock.persist.entity.CompanyEntity;
import com.zerobase.stock.persist.entity.DividendEntity;
import com.zerobase.stock.persist.repository.CompanyRepository;
import com.zerobase.stock.persist.repository.DividendRepository;
import com.zerobase.stock.scrapper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker ->" + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(final Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }
    private Company storeCompanyAndDividend(String ticker) {
        //ticker 기준으로 회사 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        //해당 회사가 존재하면 회사의 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        //결과 반환

        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                                                    .map(e -> new DividendEntity(companyEntity.getId(), e))
                                                    .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntityList);

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        List<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>)this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다"));
        this.dividendRepository.deleteAllById(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
