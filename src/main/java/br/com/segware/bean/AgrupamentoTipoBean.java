package br.com.segware.bean;

import br.com.segware.Tipo;

public class AgrupamentoTipoBean {
	
	private Tipo tipoEvento;
	private Integer quantidade;
	
	public Tipo getTipoEvento() {
		return tipoEvento;
	}
	
	public void setTipoEvento(Tipo tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
	
	public Integer getQuantidade() {
		return (quantidade != null ? quantidade : 0);
	}
	
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	

}
