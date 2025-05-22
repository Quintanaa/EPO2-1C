package com.example.springboot.controller;

import com.example.springboot.dto.CategoriaDTO;
import com.example.springboot.dto.ProductoDTO;
import com.example.springboot.model.Categoria;
import com.example.springboot.model.Producto;
import com.example.springboot.servicios.CategoriaService;
import com.example.springboot.servicios.ProductoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProductoController {

    final ProductoService productoService;

    final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    //Parte de productos
    @GetMapping("/productos")
    public String listarProductos(ModelMap interfaz) {
        interfaz.addAttribute("productos", productoService.listProductoDTO());
        return "productos/productos";
    }

    @GetMapping("/productos/nuevo")
    public String formularioProducto(ModelMap interfaz) {
        interfaz.addAttribute("producto", new Producto());
        interfaz.addAttribute("categorias", categoriaService.categoriaList());
        return "productos/nuevoProducto";
    }

    @PostMapping("/productos/nuevo")
    public String guardarProducto(@ModelAttribute("producto") Producto product, ModelMap interfaz,
                                  @RequestParam(required = false) List<Long> selectedCategorias) {
        if (selectedCategorias != null && selectedCategorias.isEmpty()) {
            Set<Categoria> categorias = new HashSet<>(categoriaService.getCategoriasByIds(selectedCategorias));
            product.setCategorias(categorias);
        }
        productoService.saveProd(product);
        return "redirect:/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, ModelMap interfaz) {
        Producto producto = productoService.getRepo().findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }

        ProductoDTO productoDTO = new ProductoDTO();
        new ModelMapper().map(producto, productoDTO);

        interfaz.addAttribute("producto", productoDTO);
        interfaz.addAttribute("categorias", categoriaService.categoriaList());
        interfaz.addAttribute("productoCategorias", producto.getCategorias());
        return "productos/editarProducto";
    }

    @PostMapping("/productos/editar/{id}")
    public String guardarProducto(@PathVariable Long id,
                                  @ModelAttribute(name = "producto") ProductoDTO productoDTO,
                                  @RequestParam("selectedCategorias") List<Long> categoriasIds) {
        Producto producto = productoService.getRepo().findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }

        producto.setNombre(productoDTO.getNombre());
        producto.setCantidad(productoDTO.getCantidad());
        producto.setPrecio(productoDTO.getPrecio());

        Set<Categoria> categorias = new HashSet<>(categoriaService.getCategoriasByIds(categoriasIds));
        producto.setCategorias(categorias);

        productoService.getRepo().save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String confirmarEliminarProducto(@PathVariable Long id, ModelMap interfaz) {
        Producto producto = productoService.getRepo().findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }

        ProductoDTO productoDTO = new ProductoDTO();
        new ModelMapper().map(producto, productoDTO);
        interfaz.addAttribute("producto", productoDTO);
        return "productos/eliminarProducto";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, ModelMap interfaz) {
        productoService.getRepo().deleteById(id);
        return "redirect:/productos";
    }

    //Categoría
    @GetMapping("/categorias")
    public String listarCategorias(ModelMap interfaz) {
        interfaz.addAttribute("categorias", categoriaService.listCategoriaDTO());
        return "categorias/categorias";
    }

    @GetMapping("/categorias/nuevo")
    public String formularioCategoria(ModelMap interfaz) {
        interfaz.addAttribute("categoria", new Categoria());
        return "categorias/nuevaCategoria";
    }

    @PostMapping("/categorias/nuevo")
    public String guardarCategorias(@ModelAttribute("categoria") Categoria categoria, ModelMap interfaz) {
        categoriaService.save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(@PathVariable Long id, ModelMap interfaz) {
        Categoria categoria = categoriaService.getRepo().findById(id).orElse(null);
        if (categoria == null) {
            return "redirect:/categorias";
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        new ModelMapper().map(categoria, categoriaDTO);

        interfaz.addAttribute("categoria", categoriaDTO);
        return "categorias/editarCategoria";
    }

    @PostMapping("/categorias/editar/{id}")
    public String guardarProducto(@PathVariable Long id,
                                  @ModelAttribute(name = "categoria") CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaService.getRepo().findById(id).orElse(null);
        if (categoria == null) {
            return "redirect:/categorias";
        }

        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());

        categoriaService.getRepo().save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String confirmarEliminarCategoria(@PathVariable Long id, ModelMap interfaz) {
        Categoria categoria = categoriaService.getRepo().findById(id).orElse(null);
        if (categoria == null) {
            return "redirect:/categorias";
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        new ModelMapper().map(categoria, categoriaDTO);
        interfaz.addAttribute("categoria", categoriaDTO);
        return "categorias/eliminarCategoria";
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, ModelMap interfaz) {
        categoriaService.getRepo().deleteById(id);
        return "redirect:/categorias";
    }
}
