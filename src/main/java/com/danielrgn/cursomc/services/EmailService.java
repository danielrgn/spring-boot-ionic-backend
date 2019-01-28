package com.danielrgn.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.danielrgn.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
