package org.adempiere.apps.graph;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import org.compiere.model.MMeasure;
import org.compiere.util.DB;
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
		// Data Structure
		double cashIn[] = new double[13];
		double cashOut[] = new double[13];
		
		// Query
		String sql_getIncome = 
			"SELECT date_part('month', dateinvoiced) AS month, sum(grandtotal) AS total "
			+ "FROM adempiere.c_cashflow "
			+ "WHERE grandtotal > 0.0 AND date_part('year', dateinvoiced) = ? "
			+ "GROUP BY month "
			+ "ORDER BY month ASC";
		
		String sql_getOutcome =
			"SELECT date_part('month', dateinvoiced) AS month, sum(grandtotal) AS total "
			+ "FROM adempiere.c_cashflow "
			+ "WHERE grandtotal < 0.0 AND date_part('year', dateinvoiced) = ? "
			+ "GROUP BY month "
			+ "ORDER BY month ASC";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// ***** INCOME *****
			ps = DB.prepareStatement(sql_getIncome, null);
			ps.setInt(1, year);
			rs = ps.executeQuery();
			while(rs.next()) {
				int monthRes = rs.getInt("month");
				double grandtotalRes = rs.getDouble("total");
				cashIn[monthRes] = grandtotalRes;
			}
			
			// ***** OUTCOME *****
			ps = DB.prepareStatement(sql_getOutcome, null);
			ps.setInt(1, year);
			rs = ps.executeQuery();
			while(rs.next()) {
				int monthRes = rs.getInt("month");
				double grandtotalRes = rs.getDouble("total");
				cashOut[monthRes] = grandtotalRes;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		for(int i=1; i<=12; i++) System.out.println("" + cashIn[i] + " " + cashOut[i]);
		
		dataset = new DefaultCategoryDataset();
		linearDataset = new DefaultCategoryDataset();
		// Tambahin Pemasukan disini
		for (int i = 1; i <= 12; i++) {
			String series = "Income";
			//ganti sama nilai income
			double value = cashIn[i];
			//ganti sama data bulan/tanggal dari database
			String label = "Bln " + i;
			dataset.addValue(value, series, label);
		}
		
		//Tambahin Pengeluaran disini
		for (int i = 1; i <= 12; i++) {
			String series = "Outcome";
			//ganti sama nilai outcome
			double value = cashOut[i];
			//ganti sama data bulan/tanggal dari database
			String label = "Bln " + i;
			//ga harus * -1 kalo datanya udah negatif
			dataset.addValue(value, series, label);
		}
		
		for (int i = 1; i <= 12; i++) {
			double value = cashIn[i] + cashOut[i];
//			double value = i * 10 * Math.pow(-1, i);
			String label = "Bln " + i;
			linearDataset.addValue(value, "Cash", label);
		}
		
		return null;
	}
}
