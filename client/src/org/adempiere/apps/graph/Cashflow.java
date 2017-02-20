package org.adempiere.apps.graph;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.compiere.Adempiere;
import org.compiere.apps.AEnv;
import org.compiere.apps.ConfirmPanel;
import org.compiere.model.MGoal;
import org.compiere.model.X_PA_Goal;
import org.compiere.swing.CFrame;
import org.compiere.util.Env;

public class Cashflow extends CFrame implements ActionListener{
	private static final long serialVersionUID = -5994488373513922522L;
	
	/**
	 * 	Constructor.
	 * 	Called from PAPanel, ViewPI (Performance Indicator)
	 *	@param goal goal
	 */
	public Cashflow ()
	{
		super ("Cashflow");
		setIconImage(Adempiere.getImage16());
//		MGoal[] goals = MGoal.getUserGoals(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));
//		MGoal goal = goals[1];
//		if (goal.getMeasure() != null)
//			barPanel = new Graph(goal, true);
		
		init();
		AEnv.addToWindowManager(this);
		AEnv.showCenterScreen(this);
	}	//	PerformanceDetail

	Graph barPanel = null;
	ConfirmPanel confirmPanel = new ConfirmPanel();

	JComboBox<Integer> yearList;
	JComboBox<String> graphChoice;
	/**
	 * 	Static init
	 */
	private void init()
	{
		yearList = new JComboBox<>();
		for (int i = 1980; i < 2030; i++) {
			yearList.addItem(i);;
		}
		yearList.setSelectedIndex(2017 - 1980);
		yearList.addActionListener(this);
//		yearList.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	int year = ((Integer) yearList.getSelectedItem()).intValue();
//            	
//            	
//            	barPanel = new GraphCashflow(year);
//            	getContentPane().removeAll();
//            	
//            	getContentPane().add(yearList, BorderLayout.NORTH);
//                getContentPane().add(barPanel, BorderLayout.CENTER);
//                getContentPane().add(confirmPanel, BorderLayout.SOUTH);
//                getContentPane().repaint();
//                pack();
//                validate();
//            }
//        });
		getContentPane().add(yearList, BorderLayout.NORTH);
		
		graphChoice = new JComboBox<>();
		graphChoice.addItem("Bar Chart");
		graphChoice.addItem("Line Chart");
		graphChoice.setSelectedIndex(0);
		graphChoice.addActionListener(this);
		getContentPane().add(graphChoice, BorderLayout.SOUTH);
		
		int year = ((Integer) yearList.getSelectedItem()).intValue();
        
		barPanel = new GraphCashflow(year, X_PA_Goal.CHARTTYPE_BarChart);
		getContentPane().add(barPanel, BorderLayout.CENTER);
//		getContentPane().add(confirmPanel, BorderLayout.SOUTH);
		confirmPanel.addActionListener(this);
	}	//	init
	
	/**
	 * 	Action Listener
	 *	@param e event
	 */
	public void actionPerformed (ActionEvent e)
	{
		if (e.getActionCommand().equals(ConfirmPanel.A_OK))
			dispose();
		
		int year = ((Integer) yearList.getSelectedItem()).intValue();
    	String choice = (String) graphChoice.getSelectedItem();
    	
    	if (choice.equals("Bar Chart"))
    		choice = X_PA_Goal.CHARTTYPE_BarChart;
    	else if (choice.equals("Line Chart"))
    		choice = X_PA_Goal.CHARTTYPE_LineChart;
    	
    	barPanel = new GraphCashflow(year, choice);
    	getContentPane().removeAll();
    	
    	getContentPane().add(yearList, BorderLayout.NORTH);
    	getContentPane().add(graphChoice, BorderLayout.SOUTH);
        getContentPane().add(barPanel, BorderLayout.CENTER);
//        getContentPane().add(confirmPanel, BorderLayout.SOUTH);
        getContentPane().repaint();
        pack();
        validate();
	}	//	actionPerformed
	
}	//	PerformanceDetail
