package com.example.springboot.servicios;

import com.example.springboot.Repositorios.ProductoRepository;
import com.example.springboot.dto.ProductoDTO;
import com.example.springboot.model.Producto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
public class ProductoService {

    @Autowired
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto registerProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<ProductoDTO> listProductoDTO(){
        ModelMapper modelMapper = new ModelMapper();
        List<Producto> productos = productoRepository.findAll();
        //Recorro la lista
        ListIterator iterator = productos.listIterator();
        //Creo la lista de DTOs
        List <ProductoDTO> responses = new ArrayList<>();
        while (iterator.hasNext()) {
            Producto producto = (Producto) iterator.next();
            ProductoDTO productoDTO = new ProductoDTO();
            modelMapper.map(producto, productoDTO);
            responses.add(productoDTO);
        }
        return responses;
    }

    public ProductoDTO saveProd(Producto producto) {
        Producto productoGuardado = productoRepository.save(producto);
        ModelMapper modelMapper = new ModelMapper();
        ProductoDTO productoDTO = new ProductoDTO();
        modelMapper.map(productoGuardado, productoDTO);
        return productoDTO;
    }

    public ProductoRepository getRepo(){
        return productoRepository;
    }
}
