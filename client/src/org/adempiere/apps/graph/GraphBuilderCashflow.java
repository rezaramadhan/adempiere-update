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
	
	public ArrayList<GraphColumn> loadData() {
		//	Calculated

		dataset = new DefaultCategoryDataset();
		for (int i = 0; i < 5; i++){
			String series = "series" + i;
			int value = i*10;
			String label = "Label";
			System.out.println("------series "+ series);
			for (int i = 0; i < 2; i++)
			System.out.println("------X axislabel "+ m_X_AxisLabel);
			System.out.println("------getval "+ value);
			System.out.println("------getlabel "+ label + "\n-------------------------\n");
			dataset.addValue(value, series,
					label);

		}
		return null;
	}
}
