package com.cine.premieres.service.impl;

import com.cine.premieres.dto.EstrenoResponse;
import com.cine.premieres.entity.Estrenos;
import com.cine.premieres.mapper.EstrenosMapper;
import com.cine.premieres.repository.EstrenosRepository;
import com.cine.premieres.service.EstrenosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstrenosServiceImpl implements EstrenosService {

    private final EstrenosRepository estrenosRepository;
    private final EstrenosMapper estrenosMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EstrenoResponse> listarEstrenos() {
        log.info("Consultando estrenos activos");
        List<Estrenos> estrenos = estrenosRepository.listarActivos();
        log.info("Se encontraron {} estrenos", estrenos.size());
        return estrenosMapper.toResponseList(estrenos);
    }
}