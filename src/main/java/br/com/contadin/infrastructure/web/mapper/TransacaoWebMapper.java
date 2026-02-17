package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.application.dto.transacao.TransacaoResponse;
import br.com.contadin.domain.model.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransacaoWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Transacao toDomain(TransacaoRequest request);

    @Mapping(source = "dataTransacao", target = "data_transacao")
    @Mapping(source = "fimRecorrencia", target = "fim_recorrencia")
    TransacaoResponse toResponse(Transacao transacao);
}