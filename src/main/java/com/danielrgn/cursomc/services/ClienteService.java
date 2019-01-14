package com.danielrgn.cursomc.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.danielrgn.cursomc.domain.Cidade;
import com.danielrgn.cursomc.domain.Cliente;
import com.danielrgn.cursomc.domain.Endereco;
import com.danielrgn.cursomc.domain.enums.TipoCliente;
import com.danielrgn.cursomc.dto.ClienteDTO;
import com.danielrgn.cursomc.dto.ClienteNewDTO;
import com.danielrgn.cursomc.repositories.ClienteRepository;
import com.danielrgn.cursomc.repositories.EnderecoRepository;
import com.danielrgn.cursomc.services.exceptions.DataIntegrityException;
import com.danielrgn.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> cat = clienteRepository.findById(id);
		return cat.orElseThrow(() -> 
				new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + 
				", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente clienteObj = find(obj.getId());
		updateData(obj, clienteObj);
		return clienteRepository.save(clienteObj);
	}

	private void updateData(Cliente obj, Cliente clienteObj) {
		clienteObj.setNome(obj.getNome()); 
		clienteObj.setEmail(obj.getEmail());
	}

	public void delete(Integer id) {
		try {
			find(id);
			clienteRepository.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados ao cliente");
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
	
	public Cliente fromDTO(ClienteNewDTO objDTO) throws Exception {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj() , TipoCliente.toEnum(objDTO.getTipoCliente()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		Optional.ofNullable(objDTO.getTelefone2()).ifPresent(tel2 -> cli.getTelefones().add(tel2));
		Optional.ofNullable(objDTO.getTelefone3()).ifPresent(tel3 -> cli.getTelefones().add(tel3));
		return cli;
	}
}
