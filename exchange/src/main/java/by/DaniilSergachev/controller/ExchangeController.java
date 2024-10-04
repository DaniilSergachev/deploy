package by.DaniilSergachev.controller;

import by.DaniilSergachev.Dto.CurrencyDto;
import by.DaniilSergachev.Dto.ExchangeRateDto;
import by.DaniilSergachev.Dto.ExchangeResponse;
import by.DaniilSergachev.exception.CurrencyNotExistExistsException;
import by.DaniilSergachev.exception.ExchangeRateAlreadyExistsException;
import by.DaniilSergachev.mapper.ExchangeRateMapper;
import by.DaniilSergachev.model.entity.Currency;
import by.DaniilSergachev.model.entity.ExchangeRate;
import by.DaniilSergachev.model.service.CurrencyService;
import by.DaniilSergachev.model.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;


@RestController
@RequestMapping()
@CrossOrigin
@RequiredArgsConstructor
public class ExchangeController {

    private final CurrencyService currencyService;
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateMapper exchangeRateMapper;

    @GetMapping("/currencies")
    public ResponseEntity<List<CurrencyDto>> findAllCurrencies() {
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();
        if (currencies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(currencies);

    }

    @GetMapping("/currencies/{code}")
    public ResponseEntity<CurrencyDto> findCurrencyById(@PathVariable("code") String code) {
        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(null);
        }
        try {
            CurrencyDto currencyDto = currencyService.getCurrencyByCode(code);
            return ResponseEntity.ok(currencyDto);
        } catch (CurrencyNotExistExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }


    @PostMapping("/currencies")
    public ResponseEntity<Void> saveCurrency(@RequestParam("name") String name,
                                             @RequestParam("code") String code,
                                             @RequestParam("sign") String sign) {

        if (name == null || code == null || sign == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (currencyService.findByCode(code) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        Currency currency = Currency.builder().
                code(code).
                fullName(name).
                sign(sign).
                build();

        currencyService.saveCurrency(currency);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRateDto>> findAllExchangeRate() {
        List<ExchangeRateDto> exchangeRates = exchangeRateService.findAllExchangeRate();
        if (exchangeRates == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok().body(exchangeRates);
    }

    @GetMapping("/exchangeRates/{currencyPair}")
    public ResponseEntity<ExchangeRateDto> findByBaseCurrencyCodeAndTargetCurrencyCode(@PathVariable("currencyPair") String currencyPair) {
        if (currencyPair.length() != 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3, 6);

        ExchangeRate exchangeRate = exchangeRateService.findByBaseCurrencyCodeAndTargetCurrencyCode(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        ExchangeRateDto exchangeRateDto = exchangeRateMapper.toDto(exchangeRate);
        return ResponseEntity.ok().body(exchangeRateDto);
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRateDto> saveExchangeRate(@RequestParam(value = "baseCurrencyCode") String baseCurrencyCode, @RequestParam(value = "targetCurrencyCode") String targetCurrencyCode, @RequestParam(value = "rate") BigDecimal rate) {
        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            ExchangeRateDto newRate = exchangeRateMapper.toDto(exchangeRateService.createExchangeRate(baseCurrencyCode, targetCurrencyCode, rate));
            return ResponseEntity.status(HttpStatus.CREATED).body(newRate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ExchangeRateAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @PatchMapping("/exchangeRates/{currencyPair}")
    public ResponseEntity<ExchangeRateDto> updateExchangeRate(@PathVariable("currencyPair") String currencyPair, @RequestParam(value = "rate") BigDecimal rate) {
        if (rate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3, 6);
        ExchangeRate exchangeRate = exchangeRateService.findByBaseCurrencyCodeAndTargetCurrencyCode(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        exchangeRate.setRate(rate);
        exchangeRateService.saveExchangeRate(exchangeRate);
        ExchangeRateDto exchangeRateDto = exchangeRateMapper.toDto(exchangeRate);
        return ResponseEntity.status(HttpStatus.OK).body(exchangeRateDto);
    }

    @GetMapping("/exchange")
    public ResponseEntity<Object> getExchangeRate(@RequestParam("from") String fromCurrencyCode,
                                                  @RequestParam("to") String toCurrencyCode,
                                                  @RequestParam("amount") BigDecimal amount) {
        if (fromCurrencyCode == null || toCurrencyCode == null || amount == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ExchangeRate exchangeRate = exchangeRateService.findByBaseCurrencyCodeAndTargetCurrencyCode(fromCurrencyCode, toCurrencyCode);
        if (exchangeRate == null) {
            exchangeRate = exchangeRateService.findByBaseCurrencyCodeAndTargetCurrencyCode(toCurrencyCode, fromCurrencyCode);
            if (exchangeRate != null) {
                BigDecimal inverseRate = BigDecimal.ONE.divide(exchangeRate.getRate(), MathContext.DECIMAL128);
                BigDecimal convertedAmount = inverseRate.multiply(amount);
                return ResponseEntity.ok(ExchangeResponse.builder()
                        .baseCurrency(exchangeRate.getTargetCurrency())
                        .targetCurrency(exchangeRate.getBaseCurrency())
                        .rate(inverseRate)
                        .amount(amount)
                        .convertedAmount(convertedAmount)
                        .build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Валюта не найдена");
        }

        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount);
        ExchangeResponse response = ExchangeResponse.builder()
                .baseCurrency(exchangeRate.getBaseCurrency())
                .targetCurrency(exchangeRate.getTargetCurrency())
                .rate(exchangeRate.getRate())
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();

        return ResponseEntity.ok().body(response);

    }


}


