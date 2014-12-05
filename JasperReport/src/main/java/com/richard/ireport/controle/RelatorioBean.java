package com.richard.ireport.controle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.richard.ireport.modelo.Cliente;

@ManagedBean
@ViewScoped
public class RelatorioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String caminhoRelatorio;
	private FacesContext context;
	private List<Cliente> list;

	@PostConstruct
	public void PreparandoTela() {

		Cliente c1 = new Cliente("Caju Jones","caju@gmail.com","1111-1111");

		Cliente c2 = new Cliente("João Cana Brava","joao@gmail.com","2222-2222");

		Cliente c3 = new Cliente("Franscisco Garoto Super","franscisco@gmail.com","3333-3333");
		
		Cliente c4 = new Cliente("Fabricio Grande Polegar","fabricio@gmail.com","4444-4444");

		getList().add(c1);
		getList().add(c2);
		getList().add(c3);
		getList().add(c4);

	}

	public void gerarPdf(Cliente cliente) throws JRException {
		List<Cliente> lista = new ArrayList<Cliente>();
		lista.add(cliente);
		String caminho = "/WEB-INF/relatorios/RelatorioClientes.jrxml";
		context = FacesContext.getCurrentInstance();
		ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
		caminhoRelatorio = servletContext.getRealPath(caminho);
		String nomeArquivo = "Cliente "+cliente.getNome();
		enviarPdf(lista, nomeArquivo);
	}

	public void gerarPdfTodos(List<Cliente> lista) throws JRException {

		String caminho = "/WEB-INF/relatorios/RelatorioClientes.jrxml";
		context = FacesContext.getCurrentInstance();
		ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
		caminhoRelatorio = servletContext.getRealPath(caminho);
		String nomeArquivo = "todosClientes";
		enviarPdf(lista, nomeArquivo);
	}

	@SuppressWarnings("deprecation")
	public void enviarPdf(List<Cliente> lista, String nomeArquivo) {
		// Carrega o xml de definição do relatório
		try {
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			// Configura o response para suportar o relatório

			response.setContentType(nomeArquivo +"/pdf");
			response.addHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+".pdf\"");

			// Exporta o relatório
			JasperReport report = JasperCompileManager.compileReport(caminhoRelatorio);
			JasperPrint print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(lista));

			JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
			// Salva o estado da aplicação no contexto do JSF

			context.getApplication().getStateManager().saveView(context);
			// Fecha o stream do response
			context.responseComplete();
		} catch (Exception e) {
		}
	}

	public List<Cliente> getList() {
		if (list == null) {
			list = new ArrayList<Cliente>();
		}
		return list;
	}

	public void setList(List<Cliente> list) {
		this.list = list;
	}
}
