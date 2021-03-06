package com.danielrgn.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danielrgn.cursomc.domain.Cliente;
import com.danielrgn.cursomc.domain.ItemPedido;
import com.danielrgn.cursomc.domain.PagamentoComBoleto;
import com.danielrgn.cursomc.domain.Pedido;
import com.danielrgn.cursomc.domain.enums.EstadoPagamento;
import com.danielrgn.cursomc.repositories.ItemPedidoRepository;
import com.danielrgn.cursomc.repositories.PagamentoRepository;
import com.danielrgn.cursomc.repositories.PedidoRepository;
import com.danielrgn.cursomc.security.UserSS;
import com.danielrgn.cursomc.services.exceptions.AuthorizationException;
import com.danielrgn.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		Optional<Pedido> cat = pedidoRepository.findById(id);
		return cat.orElseThrow(() -> 
				new ObjectNotFoundException("Objeto não encontrado! Id: "+ id + 
				", Tipo: " + Pedido.class.getName()));
	}
	
	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setCliente(clienteService.find(obj.getCliente().getId()));
		obj.setInstante(new Date());
		obj.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pgto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preenchePagamentoComBoleto(pgto, obj.getInstante());
			
		}
		
		obj = pedidoRepository.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		
		for (ItemPedido item: obj.getItens()) {
			item.setDesconto(0.0);
			item.setProduto(produtoService.find(item.getProduto().getId()));
			item.setPreco(item.getProduto().getPreco());
			item.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}
	
	public Page<Pedido> findPedidosPage(Integer page, Integer size, String direction, String orderBy) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		PageRequest pageRequest = PageRequest.of(page, size, Direction.valueOf(direction), orderBy);
		Cliente cliente = clienteService.find(user.getId());
		return pedidoRepository.findByCliente(cliente, pageRequest);
	}
}
