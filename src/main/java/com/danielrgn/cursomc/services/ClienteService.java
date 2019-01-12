package com.danielrgn.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.danielrgn.cursomc.domain.Cliente;
import com.danielrgn.cursomc.dto.ClienteDTO;
import com.danielrgn.cursomc.repositories.ClienteRepository;
import com.danielrgn.cursomc.services.exceptions.DataIntegrityException;
import com.danielrgn.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> cat = clienteRepository.findById(id);
		return cat.orElseThrow(() -> 
				new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + 
				", Tipo: " + Cliente.class.getName()));
	}
	

	public Cliente update(Cliente obj) {
		Cliente clienteObj = find(obj.getId());
		updateCliente(obj, clienteObj);
		return clienteRepository.save(clienteObj);
	}

	private void updateCliente(Cliente obj, Cliente clienteObj) {
		clienteObj.setNome(obj.getNome()); 
		clienteObj.setEmail(obj.getEmail());
	}


	public void delete(Integer id) {
		try {
			find(id);
			clienteRepository.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
	}

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer size, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, size, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null ,null);
	}
}
