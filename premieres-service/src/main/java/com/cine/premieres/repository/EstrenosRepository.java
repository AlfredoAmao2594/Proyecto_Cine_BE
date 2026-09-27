package com.cine.premieres.repository;

import com.cine.premieres.entity.Estrenos;

import java.util.List;

public interface EstrenosRepository {

    List<Estrenos> listarActivos();
}
