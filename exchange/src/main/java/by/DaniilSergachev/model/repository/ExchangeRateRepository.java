package by.DaniilSergachev.model.repository;

import by.DaniilSergachev.model.entity.Currency;
import by.DaniilSergachev.model.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("""
                SELECT ex FROM ExchangeRate ex 
                JOIN ex.baseCurrency base 
                JOIN ex.targetCurrency target 
                WHERE base.code = :code1 AND target.code = :code2
            """)
    ExchangeRate findByBaseCurrencyCodeAndTargetCurrencyCode(@Param("code1") String code1, @Param("code2") String code2);


    boolean existsByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
}
