package com.danielrgn.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.danielrgn.cursomc.domain.Categoria;
import com.danielrgn.cursomc.repositories.CategoriaRepository;
import com.danielrgn.cursomc.services.exceptions.DataIntegrityException;
import com.danielrgn.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Categoria find(Integer id) {
		Optional<Categoria> cat = categoriaRepository.findById(id);
		return cat.orElseThrow(() -> 
				new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + 
				", Tipo: " + Categoria.class.getName()));
	}
	
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return categoriaRepository.save(obj);
	}

	public Categoria update(Categoria obj) {
		find(obj.getId());
		return categoriaRepository.save(obj);
	}

	public void delete(Integer id) {
		try {
			find(id);
			categoriaRepository.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir categorias com produtos");
		}
	}
}
