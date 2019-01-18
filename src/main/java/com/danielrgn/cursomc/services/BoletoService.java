package com.danielrgn.cursomc.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.danielrgn.cursomc.domain.PagamentoComBoleto;

@Service
public class BoletoService {

	public void preenchePagamentoComBoleto(PagamentoComBoleto pgto, Date instante) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(instante);
		cal.add(Calendar.DAY_OF_MONTH, 7);
		pgto.setDataVencimento(cal.getTime());
	}

}
