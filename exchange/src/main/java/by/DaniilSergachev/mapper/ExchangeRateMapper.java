package by.DaniilSergachev.mapper;

import by.DaniilSergachev.Dto.ExchangeRateDto;
import by.DaniilSergachev.model.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRateDto toDto(ExchangeRate rate);

    ExchangeRate toEntity(ExchangeRateDto dto);
}
