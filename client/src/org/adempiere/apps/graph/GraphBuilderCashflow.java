package org.adempiere.apps.graph;

import java.util.ArrayList;
import java.util.Calendar;

import org.compiere.model.MMeasure;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class GraphBuilderCashflow extends GraphBuilder {
	
	public GraphBuilderCashflow() {
		super();
	}
	
	public GraphBuilderCashflow(String name) {
		super();
		graphName = name;
	}
	
	public ArrayList<GraphColumn> loadData(int year) {
		System.out.println("yearselected:" + year);
		
		//query data disini
		
		dataset = new DefaultCategoryDataset();
		
		// Tambahin Pemasukan disini
		for (int i = 0; i < 5; i++){
			String series = "Income";
			//ganti sama nilai income
			int value = i*10;
			//ganti sama data bulan/tanggal dari database
			String label = "Bulan/Tahun" + i;
			dataset.addValue(value, series, label);
		}
		
		//Tambahin Pengeluaran disini
		for (int i = 0; i < 5; i++){
			String series = "Outcome";
			//ganti sama nilai outcome
			int value = i*10;
			//ganti sama data bulan/tanggal dari database
			String label = "Bulan/Tahun" + i;
			//ga harus * -1 kalo datanya udah negatif
			dataset.addValue(value * -1, series, label);
		}
		
		return null;
	}
}
