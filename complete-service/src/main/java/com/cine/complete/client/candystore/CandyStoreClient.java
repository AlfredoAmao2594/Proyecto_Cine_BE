package com.cine.complete.client.candystore;


import java.util.List;
import java.util.UUID;

public interface CandyStoreClient {
    /**
     * @param authorization el mismo header "Bearer ..." que envió el usuario: candystore también exige token
     */
    List<PrecioProducto> obtenerPrecios(List<UUID> ids, String authorization);
}