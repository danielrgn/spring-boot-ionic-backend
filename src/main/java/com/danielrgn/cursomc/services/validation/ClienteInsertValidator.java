package com.danielrgn.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.danielrgn.cursomc.domain.Cliente;
import com.danielrgn.cursomc.domain.enums.TipoCliente;
import com.danielrgn.cursomc.dto.ClienteNewDTO;
import com.danielrgn.cursomc.repositories.ClienteRepository;
import com.danielrgn.cursomc.resources.exception.FieldMessage;
import com.danielrgn.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> list = new ArrayList<>();
		
		if (objDto.getTipoCliente().equals(TipoCliente.PESSOAFISICA.getId()) && !BR.isValidCPF(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido"));
		}
			
		if (objDto.getTipoCliente().equals(TipoCliente.PESSOAJURIDICA.getId()) && !BR.isValidCNPJ(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido"));
		}
		
		Cliente emailCliente = clienteRepository.findByEmail(objDto.getEmail());
		
		Optional.ofNullable(emailCliente).ifPresent(e -> list.add(new FieldMessage("email", "E-mail já existente")));
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
