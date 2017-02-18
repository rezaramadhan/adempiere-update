package org.adempiere.apps.graph;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.compiere.Adempiere;
import org.compiere.apps.AEnv;
import org.compiere.apps.ConfirmPanel;
import org.compiere.model.MGoal;
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
		barPanel = new GraphCashflow();
		init();
		AEnv.addToWindowManager(this);
		AEnv.showCenterScreen(this);
	}	//	PerformanceDetail

	Graph barPanel = null;
	ConfirmPanel confirmPanel = new ConfirmPanel();

	/**
	 * 	Static init
	 */
	private void init()
	{
		getContentPane().add(barPanel, BorderLayout.CENTER);
		getContentPane().add(confirmPanel, BorderLayout.SOUTH);
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
	}	//	actionPerformed
	
}	//	PerformanceDetail
