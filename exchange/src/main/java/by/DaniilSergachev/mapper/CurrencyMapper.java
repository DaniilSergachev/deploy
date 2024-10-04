package by.DaniilSergachev.mapper;

import by.DaniilSergachev.Dto.CurrencyDto;
import by.DaniilSergachev.model.entity.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    CurrencyDto toDto(Currency currency);

    Currency toEntity(CurrencyDto dto);
}
