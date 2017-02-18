package org.adempiere.apps.graph;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

import org.compiere.apps.AEnv;
import org.compiere.grid.ed.VLookup;
import org.compiere.model.MGoal;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MQuery;
import org.compiere.model.X_PA_Goal;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;

public class GraphCashflow extends Graph{
	/**
	 *
	 */
	private static final long serialVersionUID = -4150122585550132822L;
	JComboBox<Integer> yearList;
	private int year;

	/**
	 * 	Constructor
	 *	@param goal goal
	 */
	public GraphCashflow()
	{
		super();
		
		builder = new GraphBuilderCashflow("Cashflow");
		builder.setYAxisLabel("Besar");
		builder.setXAxisLabel("Bulan");
		m_userSelection = false;
		//addComponentListener(this);
	}	//	BarGraph
	
	public GraphCashflow(int y) {
		this();
		year = y;
		System.out.println("ctor" + year);
		loadData();
	}
	
	protected void loadData()
	{

		list = builder.loadData(year);
		JFreeChart chart = builder.createChart(X_PA_Goal.CHARTTYPE_BarChart);
		if (chartPanel != null)
			remove(chartPanel);

		chartPanel = new ChartPanel(chart);
		chartPanel.setSize(getSize());
		chartPanel.addChartMouseListener(this);
		add(chartPanel,BorderLayout.CENTER);

		this.setMinimumSize(paneldimension);
	}	//	loadData


}
