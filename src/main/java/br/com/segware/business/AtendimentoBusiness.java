package br.com.segware.business;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.segware.Tipo;
import br.com.segware.bean.AgrupamentoTipoBean;

public class AtendimentoBusiness {
	
	private static final String ARQUIVO_CSV = "src/test/java/br/com/segware/relatorio.csv";
	private static final long MILLIS_POR_DIA = 86400000;
	private static final long MILLIS_CINCO_MINUTOS = 300000;
	
	
	public AtendimentoBusiness(){}
	
	private BufferedReader buffer;

	/**
	 * Calcula o total de eventos agrupados por cliente.
	 * 
	 * @return <code>Map</code> com os eventos agrupados por cliente onde:<br>
	 *  - Chave = Código do cliente.<br>
	 *  - Valor = Quantidade de ocorrencias do cliente.<br>
	 */
	public Map<String, Integer> getTotalEventosCliente() {
		Map<String, Integer> mapAtendimentos = new HashMap<String, Integer>();
		try {
			calculaTotalEventosCliente(mapAtendimentos);			
		} catch (FileNotFoundException e) {
			System.out.println("[ERRO] Arquivo não encontrado no caminho: '" + ARQUIVO_CSV + "'");
		} catch (IOException e) {
			System.out.println("[ERRO] Erro ao ler arquivo csv.");
			e.printStackTrace();
		} finally {
			fechaBuffer();
		}
		
		return mapAtendimentos;
	}

	/**
	 * Calcula o tempo médio de cada atendente.
	 * 
	 * @return <code>Map</code> com o tempo médio de cada atendente onde:<br>
	 * 	- Chave = Código do atendente.<br>
	 *  - Valor = Tempo médio do atendente<br>
	 */
	public Map<String, Long> getTempoMedioAtendimentoAtendente() {
		Map<String, Long> mapAtendimentos = new HashMap<String, Long>();
		
		try {
			calculaTempoMedioAtendimentoAtendente(mapAtendimentos);
		} catch (FileNotFoundException e) {
			System.out.println("[ERRO] Arquivo não encontrado no caminho: '" + ARQUIVO_CSV + "'");
		} catch (IOException e) {
			System.out.println("[ERRO] Erro ao ler arquivo csv.");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("[ERRO] Erro ao calcular tempo do atendimento.");
			e.printStackTrace();
		} finally {
			fechaBuffer();
		}
		
		return mapAtendimentos;
	}

	/**
	 * Verifica quais tipos ocorrem mais e retorna em ordem decrescente.
	 * 
	 * @return <code>List</code> com os tipos de eventos ordenados de forma decrescente.
	 */
	public List<Tipo> getTiposOrdenadosNumerosEventosDecrescente() {
		List<Tipo> listaOrdenada = new ArrayList<Tipo>();

		try {
			verificaTiposOrdenados(listaOrdenada);
			
		} catch (FileNotFoundException e) {
			System.out.println("[ERRO] Arquivo não encontrado no caminho: '" + ARQUIVO_CSV + "'");
		} catch (IOException e) {
			System.out.println("[ERRO] Erro ao ler arquivo csv.");
			e.printStackTrace();
		} finally {
			fechaBuffer();
		}
		return listaOrdenada;
	}

	
	/**
	 * Calcula se a data inicial do desarme ocorre após um tempo maior do que 5 minutos da data inicial do alarme
	 * 
	 * @return <code>List</code> com codigoSequencial dos desarmes que ocorrem com menos de 5 minutos após os alarmes. 
	 */
	public List<Integer> getCodigoSequencialEventosDesarmeAposAlarme() {
		List<Integer> listaCodigosDesarme = new ArrayList<Integer>();
		
		try {
			verificaCodigoSequencialDeDesarmes(listaCodigosDesarme);
			
		} catch (FileNotFoundException e) {
			System.out.println("[ERRO] Arquivo não encontrado no caminho: '" + ARQUIVO_CSV + "'");
		} catch (IOException e) {
			System.out.println("[ERRO] Erro ao ler arquivo csv.");
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("[ERRO] Erro ao calcular tempo de desarme em relação ao tempo de alerta.");
			e.printStackTrace();
		} finally {
			fechaBuffer();
		}
		
		return listaCodigosDesarme;
	}

	/**
	 * Verifica se a data inicial do desarme ocorre com menos de 5 minutos que a data inicial do último alarme. Se sim, adiciona em uma lista.
	 * 
	 * @param listaCodigosDesarme para adicionar os códigos de desarmes.
	 * @throws FileNotFoundException se o arquivo CSV não foi encontrado.
	 * @throws IOException se ocorreu algum erro ao ler o arquivo CSV
	 * @throws ParseException se ocorreu algum erro ao calcular as datas de alarme e desarme.
	 */
	private void verificaCodigoSequencialDeDesarmes(List<Integer> listaCodigosDesarme) throws FileNotFoundException, IOException, ParseException {
		String linha = null;
		buffer = new BufferedReader(new FileReader(ARQUIVO_CSV));
		String tempoAnterior = "";
		
		while ((linha = buffer.readLine()) != null) {
			String[] colunas = linha.split(",");
			Tipo tipoEvento = Enum.valueOf(Tipo.class, colunas[3]);
			if (tipoEvento.equals(Tipo.ALARME)){
				tempoAnterior = colunas[4];
			} else if (tipoEvento.equals(Tipo.DESARME)) {
				Boolean isMaiorQueCincoMinutos = calculaTempoAlarmeAnterior(tempoAnterior, colunas[4]);
				if (isMaiorQueCincoMinutos) {
					listaCodigosDesarme.add(new Integer(colunas[0]));
				}
			}
			
		}
	}

	/**
	 * Faz o calculo e o agrupamente de quantos eventos cada cliente tem.
	 * 
	 * @param mapAtendimentos para adicionar os eventos que cada cliente tem.
	 * @throws FileNotFoundException se o arquivo CSV não foi encontrado.
	 * @throws IOException se ocorreu algum erro ao ler o arquivo CSV
	 */
	private void calculaTotalEventosCliente(Map<String, Integer> mapAtendimentos) throws FileNotFoundException, IOException {
		String linha = null;
		buffer = new BufferedReader(new FileReader(ARQUIVO_CSV));
		
		while ((linha = buffer.readLine()) != null){
			String[] colunas = linha.split(",");
			Integer quantidade = mapAtendimentos.get(colunas[1]);
			mapAtendimentos.put(colunas[1], quantidade != null ? quantidade++ : 1);
		}
	}
	
	/**
	 * 
	 * Calcula qual o tempo médio de cada atendente.
	 * 
	 * @param mapAtendimentos para adicionar o tempo médio de cada atendente.
	 * @throws FileNotFoundException se o arquivo CSV não foi encontrado.
	 * @throws IOException se ocorreu algum erro ao ler o arquivo CSV
	 * @throws ParseException se ocorreu algum erro ao calcular as datas de alarme e desarme.
	 */
	private void calculaTempoMedioAtendimentoAtendente(Map<String, Long> mapAtendimentos) throws FileNotFoundException,IOException, ParseException {
		String linha = null;
		buffer = new BufferedReader(new FileReader(ARQUIVO_CSV));
		
		while ((linha = buffer.readLine()) != null) {
			String[] colunas = linha.split(",");
			
			Long tempoAtendimento = 0L;
			if ((mapAtendimentos.get(colunas[6])) != null) {
				tempoAtendimento = new Long(mapAtendimentos.get(colunas[6]));
			}
			Long tempoLinha = calculaTempoAtendimento(colunas[4], colunas[5]);
			mapAtendimentos.put(colunas[6], (tempoAtendimento + tempoLinha) / 2);
		}
	}
	
	/**
	 * Calcula quais os tipos de alarmes acontecem e com qual frequencia acontecem. Ordena em uma lista de forma decrescente.
	 * 
	 * @param listaOrdenada usada para adicionar os tipos de eventos ordenados pela quantidade que acontecem de forma decrescente.
	 * @throws FileNotFoundException se o arquivo CSV não foi encontrado.
	 * @throws IOException se ocorreu algum erro ao ler o arquivo CSV
	 */
	private void verificaTiposOrdenados(List<Tipo> listaOrdenada) throws FileNotFoundException, IOException {
		String linha = null;
		buffer = new BufferedReader(new FileReader(ARQUIVO_CSV));
		Map<Tipo, AgrupamentoTipoBean> mapQuantidadePorTipo = new HashMap<Tipo, AgrupamentoTipoBean>();
		
		while ((linha = buffer.readLine()) != null) {
			String[] colunas = linha.split(",");
			Tipo tipoEvento = Enum.valueOf(Tipo.class, colunas[3]);
			AgrupamentoTipoBean bean = mapQuantidadePorTipo.get(tipoEvento);
			if (bean == null) {
				bean = new AgrupamentoTipoBean();
			}
			bean.setQuantidade(bean.getQuantidade() + 1);
			mapQuantidadePorTipo.put(tipoEvento, bean);
			
		}
		
		List<AgrupamentoTipoBean> valoresMap = ordenaValoresTipo(mapQuantidadePorTipo);
		criaListaOrdenada(listaOrdenada, valoresMap);	
	}

	/**
	 * Cria a lista ordenada apenas com os tipos de eventos.
	 */
	private void criaListaOrdenada(List<Tipo> listaOrdenada, List<AgrupamentoTipoBean> valoresMap) {
		for (AgrupamentoTipoBean agrupamentoTipoBean : valoresMap) {
			listaOrdenada.add(agrupamentoTipoBean.getTipoEvento());
		}
	}
	
	/**
	 * Ordena os tipos de eventos pela frequencia que aparecem.
	 * 
	 * @param mapQuantidadePorTipo com cada tipo de evento e sua respsctiva frequencia.
	 * @return Lista com os eventos já ordenados.
	 */
	private List<AgrupamentoTipoBean> ordenaValoresTipo(Map<Tipo, AgrupamentoTipoBean> mapQuantidadePorTipo) {
		Collection<AgrupamentoTipoBean> colecaoAgrupamentos = mapQuantidadePorTipo.values();
		List<AgrupamentoTipoBean> valoresMap = new ArrayList<AgrupamentoTipoBean>();
		valoresMap.addAll(colecaoAgrupamentos);
		ordenaLista(valoresMap);
		return valoresMap;
	}

	/**
	 * Ordena a lista com os tipos de eventos.
	 * 
	 * @param valoresMap com os valores a serem ordenados.
	 */
	private void ordenaLista(List<AgrupamentoTipoBean> valoresMap) {
		Collections.sort(valoresMap, new Comparator<AgrupamentoTipoBean>() {
			
			@Override
			public int compare(AgrupamentoTipoBean o1, AgrupamentoTipoBean o2) {
				return o1.getQuantidade() > o2.getQuantidade() ? -1 : +1;
			}
			
		});
	}

	/**
	 * Calcula o tempo médio de atendimento dos atendentes.
	 * 
	 * @param dataInicio do evento.
	 * @param dataFim do evento
	 * @return Long contento o tempo médio em segundos.
	 * @throws ParseException caso ocorra erro ao manipular as datas.
	 */
	private Long calculaTempoAtendimento(String dataInicio, String dataFim) throws ParseException {
		DateFormat formatador = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dataInicial = formatador.parse(dataInicio);
		Date dataFinal = formatador.parse(dataFim);
		
		return (dataInicial.getTime() - dataFinal.getTime()) / MILLIS_POR_DIA;
	}
	
	/**
	 * Calcula se o tempo do desarme demora menos do que 5 minutos do que a data do último alarme.
	 * 
	 * @param dataAlarme - data inicial do alarme.
	 * @param dataDesarme - data inicial do desarme
	 * @return Boolean respondendo se desarme ocorreu em menos de 5 minutos.
	 * @throws ParseException caso ocorra um erro ao manipular as datas.
	 */
	private Boolean calculaTempoAlarmeAnterior(String dataAlarme, String dataDesarme) throws ParseException {
		DateFormat formatador = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dataInicial = formatador.parse(dataAlarme);
		Date dataFinal = formatador.parse(dataDesarme);
		
		return (dataInicial.getTime() - dataFinal.getTime()) > MILLIS_CINCO_MINUTOS;
	}

	/**
	 * Fecha o BufferedReader utilizado para ler o arquivo CSV.
	 */
	private void fechaBuffer() {
		if (buffer != null){
			try {
				buffer.close();
			} catch (IOException e) {
				System.out.println("[ERRO] Erro ao fechar stream do arquivo csv.");
				e.printStackTrace();
			}
		}
	}
	
	

}
