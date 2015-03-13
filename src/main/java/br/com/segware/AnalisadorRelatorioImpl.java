package br.com.segware;

import java.util.List;
import java.util.Map;

import br.com.segware.business.AtendimentoBusiness;

public class AnalisadorRelatorioImpl implements IAnalisadorRelatorio { 

	private AtendimentoBusiness business;

	@Override
	public Map<String, Integer> getTotalEventosCliente() {
		business = new AtendimentoBusiness();
		return business.getTotalEventosCliente();
	}

	@Override
	public Map<String, Long> getTempoMedioAtendimentoAtendente() {
		business = new AtendimentoBusiness();
		return business.getTempoMedioAtendimentoAtendente();
	}

	@Override
	public List<Tipo> getTiposOrdenadosNumerosEventosDecrescente() {
		business = new AtendimentoBusiness();
		return business.getTiposOrdenadosNumerosEventosDecrescente();
	}

	@Override
	public List<Integer> getCodigoSequencialEventosDesarmeAposAlarme() {
		business = new AtendimentoBusiness();
		return business.getCodigoSequencialEventosDesarmeAposAlarme();
	}
	

}
