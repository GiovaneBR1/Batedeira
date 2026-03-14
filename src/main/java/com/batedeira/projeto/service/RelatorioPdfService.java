package com.batedeira.projeto.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.EtapaExecutada;

@Service
public class RelatorioPdfService {

	LocalDateTime agora = LocalDateTime.now();
	DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public byte[] gerarRelatorioBatelada(Batelada batelada) {
		Document relatorio = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter.getInstance(relatorio, out);
		relatorio.open();

		Font fonteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
		Paragraph texto = new Paragraph("Relatorio da batelada #" + batelada.getId(), fonteTitulo);
		texto.setAlignment(Element.ALIGN_CENTER);
		texto.setSpacingAfter(20f);
		relatorio.add(texto);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);

		criarCelulaCabecalho(table);

		for (EtapaExecutada e : batelada.getEtapasExecutadas()) {

			table.addCell(String.valueOf(e.getOrdem()));
			table.addCell(String.valueOf(e.getIngredienteNome()));
			table.addCell(String.valueOf(e.getQuantidadeEsperada()));
			table.addCell(String.valueOf(e.getQuantidadeReal()));
			table.addCell(String.valueOf(e.getStatus()));

		}

		relatorio.add(table);
		relatorio.close();
		return out.toByteArray();
	}

	private void criarCelulaCabecalho(PdfPTable tabela) {

		String[] colunas = { "Ordem", "Ingrediente", "Quantidade Esperada", "Quantidade real", "Status" };

		for (String nomeColuna : colunas) {
			PdfPCell cell = new PdfPCell(new Phrase(nomeColuna));
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPadding(5f);

			tabela.addCell(cell);

		}

	}

	private void criarCelulaCabecalho(PdfPTable tabela, String[] colunas) {

		for (String nomeColuna : colunas) {
			PdfPCell cell = new PdfPCell(new Phrase(nomeColuna));
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPadding(5f);

			tabela.addCell(cell);
		}

	}

	public byte[] gerarRelatorioLista(List<Batelada> listaBateladas) {
		Document relatorioBateladas = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter.getInstance(relatorioBateladas, out);
		relatorioBateladas.open();

		Font fonteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
		Paragraph texto = new Paragraph("Relatorio Geral - " + agora.format(fmtHora), fonteTitulo);
		texto.setAlignment(Element.ALIGN_CENTER);
		texto.setSpacingAfter(20f);
		relatorioBateladas.add(texto);

		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);

		String[] colunasResumo = { "ID", "Data", "Modo", "Status" };
		criarCelulaCabecalho(table, colunasResumo);
		
		int contadorLinha = 0;

		for (Batelada b : listaBateladas) {
			Color corDaLinha = (contadorLinha % 2 == 0) ? new Color(220,220,220) : Color.WHITE;
			adicionarCelulaDado(table, String.valueOf(b.getId()), corDaLinha);
			adicionarCelulaDado(table, String.valueOf(b.getDataInicio()), corDaLinha);
			adicionarCelulaDado(table, String.valueOf(b.getModo()), corDaLinha);
			adicionarCelulaDado(table, String.valueOf(b.getStatus()), corDaLinha);
			
			contadorLinha++;
		}

		relatorioBateladas.add(table);
		relatorioBateladas.close();
		return out.toByteArray();
	}
	
	private void adicionarCelulaDado(PdfPTable tabela, String texto, Color corFundo) {
	    PdfPCell cell = new PdfPCell(new Phrase(texto));
	    cell.setBackgroundColor(corFundo);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setPadding(4f);
	    tabela.addCell(cell);
	}
}
