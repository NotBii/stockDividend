package com.zerobase.stock.persist.repository;

import com.zerobase.stock.persist.entity.CompanyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);

    List<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
