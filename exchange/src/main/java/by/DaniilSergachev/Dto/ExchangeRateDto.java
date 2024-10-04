package by.DaniilSergachev.Dto;

import by.DaniilSergachev.model.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDto {

    private Long id;
    private Currency baseCurrencyCode;
    private Currency targetCurrencyCode;
    private Double rate;

}
