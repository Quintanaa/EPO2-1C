package com.example.springboot.servicios;

import com.example.springboot.Repositorios.CategoriaRepository;
import com.example.springboot.Repositorios.RoleRepository;
import com.example.springboot.Repositorios.UsuarioRepository;
import com.example.springboot.dto.CategoriaDTO;
import com.example.springboot.dto.ProductoDTO;
import com.example.springboot.model.Categoria;
import com.example.springboot.model.Producto;
import com.example.springboot.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoriaService {

    @Autowired
    private final CategoriaRepository categoriaRepository;
    @Autowired
    private ApplicationContext applicationContext;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> categoriaList() {
        return categoriaRepository.findAll();
    }

    public Set<Categoria> getCategoriasByIds(List<Long> ids) {
        return new HashSet<Categoria>(categoriaRepository.findAllById(ids));
    }

    public Categoria findByNombre(String nombre){
        return categoriaRepository.findByNombre(nombre);
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<CategoriaDTO> listCategoriaDTO(){
        ModelMapper modelMapper = new ModelMapper();
        List<Categoria> categorias = categoriaRepository.findAll();
        //Recorro la lista
        ListIterator iterator = categorias.listIterator();
        //Creo la lista de DTOs
        List <CategoriaDTO> responses = new ArrayList<>();
        while (iterator.hasNext()) {
            Categoria categoria = (Categoria) iterator.next();
            CategoriaDTO categoriaDTO = new CategoriaDTO();
            modelMapper.map(categoria, categoriaDTO);
            responses.add(categoriaDTO);
        }
        return responses;
    }

    public CategoriaRepository getRepo(){
        return categoriaRepository;
    }
}
