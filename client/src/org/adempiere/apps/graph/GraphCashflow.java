package org.adempiere.apps.graph;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;

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

public class GraphCashflow extends Graph {
	/**
	 *
	 */
	private static final long serialVersionUID = -4150122585550132822L;

	/**
	 * 	Constructor
	 */
	
	/**
	 * 	Constructor
	 *	@param goal goal
	 */
	

	/**
	 * 	Constructor
	 *	@param goal goal
	 */
	public GraphCashflow()
	{
		super();
		builder = new GraphBuilderCashflow("cashflow");
		builder.setYAxisLabel("Y_Label");
		builder.setXAxisLabel("X_Label");
		m_userSelection = false;
		loadData();
		//addComponentListener(this);
	}	//	BarGraph
	
	protected void loadData()
	{

		list = builder.loadData();
		JFreeChart chart = builder.createChart(X_PA_Goal.CHARTTYPE_BarChart);
		if (chartPanel != null)
			remove(chartPanel);

		chartPanel = new ChartPanel(chart);
		chartPanel.setSize(getSize());
		chartPanel.addChartMouseListener(this);
		add(chartPanel,BorderLayout.CENTER);

		if (m_userSelection)
		{
			int AD_Reference_Value_ID = DB.getSQLValue(null, "SELECT AD_Reference_ID FROM AD_Reference WHERE Name = ?", "PA_Goal ChartType");
			MLookupInfo info = MLookupFactory.getLookup_List(Env.getLanguage(Env.getCtx()), AD_Reference_Value_ID);
			MLookup mLookup = new MLookup(info, 0);
			VLookup lookup = new VLookup("ChartType", false, false, true,
					mLookup);
			lookup.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent evt)
						throws PropertyVetoException {
					Object value = evt.getNewValue();
					if (value == null) return;
					JFreeChart chart = null;
					chart = builder.createChart(value.toString());

					if (chart != null)
					{
						if (chartPanel != null)
							remove(chartPanel);

						chartPanel = new ChartPanel(chart);
						chartPanel.setSize(getSize());
						chartPanel.addChartMouseListener(GraphCashflow.this);
						add(chartPanel,BorderLayout.CENTER);
						getParent().validate();

					}
				}

			});
			add(lookup, BorderLayout.NORTH);
		}
		this.setMinimumSize(paneldimension);
	}	//	loadData

}
