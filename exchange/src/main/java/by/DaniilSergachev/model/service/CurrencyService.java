package by.DaniilSergachev.model.service;

import by.DaniilSergachev.Dto.CurrencyDto;
import by.DaniilSergachev.exception.CurrencyNotExistExistsException;
import by.DaniilSergachev.mapper.CurrencyMapper;
import by.DaniilSergachev.model.entity.Currency;
import by.DaniilSergachev.model.repository.CurrencyRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll().stream().map(currencyMapper::toDto).toList();
    }

    public CurrencyDto getCurrencyByCode(String code) {
        if (currencyRepository.findByCode(code) == null) {
            throw new CurrencyNotExistExistsException("Валюта не найдена");
        }
        return currencyMapper.toDto(currencyRepository.findByCode(code));

    }

    public void saveCurrency(Currency currency) {
        currencyRepository.save(currency);
    }

    public Currency findByCode(String code) {
        return currencyRepository.findByCode(code);
    }


}
