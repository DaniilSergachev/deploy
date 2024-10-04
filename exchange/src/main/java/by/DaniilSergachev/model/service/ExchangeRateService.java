package by.DaniilSergachev.model.service;

import by.DaniilSergachev.Dto.ExchangeRateDto;
import by.DaniilSergachev.exception.ExchangeRateAlreadyExistsException;
import by.DaniilSergachev.exception.ExchangeRateNotExistExistsException;
import by.DaniilSergachev.mapper.ExchangeRateMapper;
import by.DaniilSergachev.model.entity.Currency;
import by.DaniilSergachev.model.entity.ExchangeRate;
import by.DaniilSergachev.model.repository.ExchangeRateRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRepository;
    private final CurrencyService currencyService;
    private final ExchangeRateMapper exchangeRateMapper;


    public List<ExchangeRateDto> findAllExchangeRate() {
        return exchangeRepository.findAll().stream().map(exchangeRateMapper::toDto).toList();
    }

    public ExchangeRate findByBaseCurrencyCodeAndTargetCurrencyCode(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRepository.findByBaseCurrencyCodeAndTargetCurrencyCode(baseCurrencyCode, targetCurrencyCode);
    }

    public void saveExchangeRate(ExchangeRate exchangeRate) {
        exchangeRepository.save(exchangeRate);
    }


    public ExchangeRate createExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        Currency baseCurrency = currencyService.findByCode(baseCurrencyCode);
        Currency targetCurrency = currencyService.findByCode(targetCurrencyCode);

        if (baseCurrency == null || targetCurrency == null) {
            throw new ExchangeRateNotExistExistsException("Валюты нет в бд");
        }

        if (exchangeRepository.existsByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)) {
            throw new ExchangeRateAlreadyExistsException("Валютная пара уже существует.");
        }

        ExchangeRate exchangeRate = ExchangeRate.
                builder().
                baseCurrency(baseCurrency).
                targetCurrency(targetCurrency).
                rate(rate).
                build();

        return exchangeRepository.save(exchangeRate);
    }
}
