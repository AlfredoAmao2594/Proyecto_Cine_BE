package com.cine.complete.client.candystore;


import java.util.List;
import java.util.UUID;

public interface CandyStoreClient {
    List<PrecioProducto> obtenerPrecios(List<UUID> ids, String authorization);
}