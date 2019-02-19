package com.danielrgn.cursomc.domain.enums;

public enum Perfil {
	
	ADMIN(1, "ROLE_ADMIN"),
	CLIENTE(2, "ROLE_CLIENTE");
	
	private Integer id;
	private String tipo;
	
	private Perfil(Integer id, String tipo) {
		this.id = id;
		this.tipo = tipo;
	}
	
	public Integer getId() {
		return id;
	}
	public String getTipo() {
		return tipo;
	}
	
	public static Perfil toEnum(Integer id) {
		
		if (id == null) {
			return null;
		}
		
		for (Perfil x : Perfil.values()) {
			if (x.getId().equals(id)) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id invalido" + id);
	}
}
