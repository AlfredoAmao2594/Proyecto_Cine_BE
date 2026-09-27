package com.cine.premieres.mapper;

import com.cine.premieres.dto.EstrenoResponse;
import com.cine.premieres.entity.Estrenos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstrenosMapper {

    public EstrenoResponse toResponse(Estrenos estreno) {
        return EstrenoResponse.builder()
                .id(estreno.getId())
                .title(estreno.getTitulo())
                .description(estreno.getDescripcion())
                .imageUrl(estreno.getUrlImagen())
                .releaseDate(estreno.getFechaEstreno())
                .build();
    }

    public List<EstrenoResponse> toResponseList(List<Estrenos> estrenos) {
        return estrenos.stream().map(this::toResponse).collect(Collectors.toList());
    }
}