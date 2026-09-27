package com.cine.candystore.mapper;

import com.cine.candystore.dto.PrecioProductoResponse;
import com.cine.candystore.dto.ProductoResponse;
import com.cine.candystore.entity.Producto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .name(producto.getNombre())
                .description(producto.getDescripcion())
                .price(producto.getPrecio())
                .imageUrl(producto.getUrlImagen())
                .category(producto.getCategoria())
                .build();
    }

    public List<ProductoResponse> toResponseList(List<Producto> productos) {
        return productos.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PrecioProductoResponse toPrecioResponse(Producto producto) {
        return PrecioProductoResponse.builder()
                .id(producto.getId())
                .name(producto.getNombre())
                .price(producto.getPrecio())
                .build();
    }

    public List<PrecioProductoResponse> toPrecioResponseList(List<Producto> productos) {
        return productos.stream().map(this::toPrecioResponse).collect(Collectors.toList());
    }
}
