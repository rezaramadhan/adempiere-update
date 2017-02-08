/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.grid.ed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.adempiere.plaf.AdempierePLAF;
import org.compiere.apps.ADialog;
import org.compiere.model.MConversionRate;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.jfree.util.Log;

/**
 *  Calculator with currency conversion
 *
 *  @author 	Jorg Janke
 *  @version 	$Id: Calculator.java,v 1.2 2006/07/30 00:51:27 jjanke Exp $
 */
public final class Calculator extends CDialog
	implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 467225451773238663L;

	/**
	 *	Create Calculator
	 * 	@param frame parent
	 * 	@param title title
	 * 	@param displayType date or datetime or time
	 * 	@param format display format
	 * 	@param number initial amount
	 */
	public Calculator(Frame frame, String title, int displayType,
		DecimalFormat format, BigDecimal number)
	{
		super(frame, title, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		//  Get WindowNo for Currency
		m_WindowNo = Env.getWindowNo(frame);
		//
		m_DisplayType = displayType;
		if (!DisplayType.isNumeric(m_DisplayType))
			m_DisplayType = DisplayType.Number;
		//
		m_format = format;
		if (m_format == null)
			m_format = DisplayType.getNumberFormat(m_DisplayType);
		//
		m_number = number;
		if (m_number == null)
			m_number = new BigDecimal(0.0);
		//
		try
		{
			jbInit();
			finishSetup();
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "Calculator" + ex);
		}
	}	//	Calculator

	/**
	 *  Abbreviated  Constructor
	 * 	@param frame parent
	 */
	public Calculator(Frame frame)
	{
		this (frame, Msg.getMsg(Env.getCtx(), "Calculator"), DisplayType.Number, null, null);
	}   //  Calculator

	/**
	 *  Abbreviated  Constructor
	 * 	@param frame parent
	 * 	@param number initial amount
	 */
	public Calculator(Frame frame, BigDecimal number)
	{
		this (frame, Msg.getMsg(Env.getCtx(), "Calculator"), DisplayType.Number, null, number);
	}   //  Calculator

	private BigDecimal		m_number;			//	the current number
	private String			m_display = "";		//	what is displayed
	private boolean			statDisplay = false; //input user or answer , false = input user
	private int 			m_DisplayType;
	private DecimalFormat 	m_format;
	private int				m_WindowNo;
	private boolean		    m_abort = true;
	private boolean			m_currencyOK = false;
	private boolean			p_disposeOnEqual = true;	//teo_sarca, bug[ 1628773 ] 

	private final static String OPERANDS = "/*-+%^msctler";
	private final static String UNARY_OPERANDS = "%sctler";
	private char			m_decimal = '.';
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(Calculator.class);
	//
	private CPanel mainPanel = new CPanel();
	private CPanel displayPanel = new CPanel();
	private BorderLayout mainLayout = new BorderLayout();
	private CPanel keyPanel = new CPanel();
	private JLabel display = new JLabel();
	private BorderLayout displayLayout = new BorderLayout();
	private JButton b7 = new JButton();
	private JButton b8 = new JButton();
	private JButton b9 = new JButton();
	private JButton b4 = new JButton();
	private JButton b5 = new JButton();
	private JButton b6 = new JButton();
	private JButton b1 = new JButton();
	private JButton b2 = new JButton();
	private JButton b3 = new JButton();
	private GridLayout keyLayout = new GridLayout();
	private JButton bCur = new JButton();
	private JButton bC = new JButton();
	private JButton bDiv = new JButton();
	private JButton bM = new JButton();
	private JButton bMin = new JButton();
	private JButton bProc = new JButton();
	private JButton bAC = new JButton();
	private JButton bResult = new JButton();
	private JButton bDec = new JButton();
	private JButton b0 = new JButton();
	private JButton bPlus = new JButton();
	private JButton bSin = new JButton();
	private JButton bCos = new JButton();
	private JButton bTan = new JButton();
	private JButton bLog = new JButton();
	private JButton bPow = new JButton();
	private JButton bSqrt = new JButton();
	private JButton bExp = new JButton();
	private JButton bMod = new JButton();
	private CPanel bordPanel = new CPanel();
	private CPanel currencyPanel = new CPanel();
	private BorderLayout bordLayout = new BorderLayout();
	private JComboBox curFrom = new JComboBox();
	private JComboBox curTo = new JComboBox();
	private JLabel curLabel = new JLabel();
	private FlowLayout currencyLayout = new FlowLayout();

	/**
	 *	Static init
	 * 	@throws Exception
	 */
	void jbInit() throws Exception
	{
		mainPanel.setLayout(mainLayout);
		displayPanel.setLayout(displayLayout);
		keyPanel.setLayout(keyLayout);
		mainLayout.setHgap(2);
		mainLayout.setVgap(2);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				
		mainPanel.addKeyListener(this);
		display.setBackground(Color.white);
		display.setFont(new java.awt.Font("SansSerif", 0, 14));
		display.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2, 0, 2, 1), 
				BorderFactory.createLineBorder(AdempierePLAF.getPrimary1())));
		display.setText("0");
		display.setHorizontalAlignment(SwingConstants.RIGHT);
		
		b7.setText("7");
		b8.setText("8");
		b9.setText("9");
		b4.setText("4");
		b5.setText("5");
		b6.setText("6");
		b1.setText("1");
		b2.setText("2");
		b3.setText("3");
		keyLayout.setColumns(5);
		keyLayout.setHgap(3);
		keyLayout.setRows(4);
		keyLayout.setVgap(3);
		bCur.setForeground(Color.yellow);
		bCur.setToolTipText(Msg.getMsg(Env.getCtx(), "CurrencyConversion"));
		bCur.setText("$");
		bC.setForeground(Color.red);
		bC.setText("C");
		bDiv.setForeground(Color.blue);
		bDiv.setText("/");
		bM.setForeground(Color.blue);
		bM.setText("*");
		bMin.setForeground(Color.blue);
		bMin.setText("-");
		bProc.setForeground(Color.blue);
		bProc.setText("%");
		bAC.setForeground(Color.red);
		bAC.setText("AC");
		bResult.setForeground(Color.green);
		bResult.setText("=");
		bDec.setText(".");
		b0.setText("0");
		bPlus.setForeground(Color.blue);
		bPlus.setText("+");
		bSin.setText("sin");
		bCos.setText("cos");
		bTan.setText("tan");
		bPow.setText("^");
		bSqrt.setText("sqrt");
		bLog.setText("log");
		bExp.setText("exp");
		bMod.setText("mod");
		bordPanel.setLayout(bordLayout);
		curLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		curLabel.setText(" >> ");
		currencyPanel.setLayout(currencyLayout);
		bordLayout.setHgap(2);
		bordLayout.setVgap(2);
		displayLayout.setHgap(2);
		displayLayout.setVgap(2);
		currencyLayout.setHgap(3);
		currencyLayout.setVgap(2);
		displayPanel.setBackground(Color.white);
		getContentPane().add(mainPanel);
		mainPanel.add(displayPanel, BorderLayout.NORTH);
		displayPanel.add(display, BorderLayout.CENTER);
		mainPanel.add(bordPanel, BorderLayout.CENTER);
		bordPanel.add(currencyPanel, BorderLayout.NORTH);
		currencyPanel.add(curFrom, null);
		currencyPanel.add(curLabel, null);
		currencyPanel.add(curTo, null);
		bordPanel.add(keyPanel, BorderLayout.CENTER);
		keyPanel.add(bSin, null);
		keyPanel.add(bSqrt, null);		
		keyPanel.add(bAC, null);
		keyPanel.add(b7, null);
		keyPanel.add(b8, null);
		keyPanel.add(b9, null);
		keyPanel.add(bM, null);
		keyPanel.add(bCos, null);
		keyPanel.add(bMod, null);
		keyPanel.add(bC, null);
		keyPanel.add(b4, null);
		keyPanel.add(b5, null);
		keyPanel.add(b6, null);
		keyPanel.add(bDiv, null);
		keyPanel.add(bTan, null);
		keyPanel.add(bExp, null);
		keyPanel.add(bProc, null);
		keyPanel.add(b1, null);
		keyPanel.add(b2, null);
		keyPanel.add(b3, null);
		keyPanel.add(bMin, null);
		keyPanel.add(bLog, null);
		keyPanel.add(bPow, null);
		keyPanel.add(bCur, null);
		keyPanel.add(b0, null);
		keyPanel.add(bDec, null);
		keyPanel.add(bResult, null);
		keyPanel.add(bPlus, null);
	}	//	jbInit

	/**
	 *	Finish Setup
	 */
	private void finishSetup()
	{
		Insets in = new Insets(2, 2, 2, 2);

		//	For all buttons
		Component[] comp = keyPanel.getComponents();
		for (int i = 0; i < comp.length; i++)
		{
			if (comp[i] instanceof JButton)
			{
				JButton b = (JButton)comp[i];
				b.setMargin(in);
				b.addActionListener(this);
				b.addKeyListener(this);
			}
		}
		//	Currency
		toggleCurrency();

		//	Format setting
		m_decimal = m_format.getDecimalFormatSymbols().getDecimalSeparator();

		//	display start number
		if (m_number.doubleValue() != 0.00 )
		{
			m_display = m_format.format(m_number);
			display.setText(m_display);
		}
	}	//	finishSetup

	/**
	 *	Action Listener
	 * 	@param e event
	 */
	public void actionPerformed(ActionEvent e)
	{
		//	Handle Button input
		if (e.getSource() instanceof JButton)
		{
			String cmd = e.getActionCommand();
			if (cmd != null && cmd.length() > 0)
				if (cmd.equals("sqrt"))
					handleInput('r');
				else
					handleInput(cmd.charAt(0));
		}
		//	Convert Amount
		else if (e.getSource() == curTo)
		{
			KeyNamePair p = (KeyNamePair)curFrom.getSelectedItem();
			int curFromID = p.getKey();
			p = (KeyNamePair)curTo.getSelectedItem();
			int curToID = p.getKey();
			//	convert
			int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
			int AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
			m_number = MConversionRate.convert(Env.getCtx(),
				evaluate(), curFromID, curToID, AD_Client_ID, AD_Org_ID);
			m_display = m_format.format(m_number);
			display.setText(m_display);
			curFrom.setSelectedItem(p);
		}
	}	//	actionPerformed

	/**
	 *	handle input
	 * 	@param c input character
	 */
	public void handleInput(char c)
	{
	//	System.out.println("Input: " + c);
		switch (c)
		{
			//	Number		===============================
			case '0':		case '1':		case '2':
			case '3':		case '4':		case '5':
			case '6':		case '7':		case '8':
			case '9':
				if (statDisplay) { //handle answer
					m_display = ""+c;
					statDisplay = false;
				} else {//handle input user
					m_display += c;
				}
				
				break;

			//	Decimal		===============================
			case '.':
			case ',':
				m_display += m_decimal;
				break;

			//	Commands	===============================
			case '/':		case '*':
			case '-':       case '+':
			case '%':		case '^':
			case 's':       case 'c':
			case 'l':		case 't':
			case 'm':		case 'e':
			case 'r':
				if (m_display.length() > 0)
				{
					char last = m_display.charAt(m_display.length()-1);
					if (OPERANDS.indexOf(last) == -1)
						m_display += c;
					else
						m_display = m_display.substring(0, m_display.length()-1) + c;
				}
				if (c == 'm')
					m_display += "od";
				m_display = m_format.format(evaluate());
				if (UNARY_OPERANDS.indexOf(c) == -1)
					m_display += c;
				if (c == 'm')
					m_display += "od";
				break;
			//	Clear last char
			case 'C':
				if (m_display.length() > 0)
					m_display = m_display.substring(0, m_display.length()-1);
				break;

			//	Clear all
			case 'A':
				m_display = "";
				break;

			//	Currency convert toggle
			case '$':
				m_display = m_format.format(evaluate());
				toggleCurrency();
				break;

			//	fini
			case '=':
				m_display = m_format.format(evaluate());
				m_abort = false;
				statDisplay = true; //answer
				if (isDisposeOnEqual()) //teo_sarca, bug [ 1628773 ] 
					dispose();
				break;
			
			//	Error		===============================
			default:
				ADialog.beep();
				break;
		}	//	switch

		if (m_display.equals(""))
			m_display = "0";

		//	Eliminate leading zeroes
		if (m_display.length() > 1 && m_display.startsWith("0"))
			if (m_display.charAt(1) != ',' && m_display.charAt(1) != '.')
				m_display = m_display.substring(1);

		//	Display it
		display.setText(m_display);
	}	//	handleInput

	/**
	 *	Evaluate.
	 *	- evaluate info in display and set number
	 * 	@return result
	 */
	private BigDecimal evaluate()
	{
		//	nothing or zero
		if (m_display == null || m_display.equals("") || m_display.equals("0"))
		{
			m_number = new BigDecimal(0.0);
			return m_number;
		}
		System.out.println("display "+m_display);
		StringTokenizer st = new StringTokenizer(m_display, OPERANDS, true);

		//	first token
		String token = st.nextToken();
		System.out.println("token "+token);
		
		//	do we have a negative number ?
		boolean isNegative = true;
		if (token.equals("-"))
		{
			isNegative = true;
			if (st.hasMoreTokens())
				token += st.nextToken();
			else
			{
				m_number = new BigDecimal(0.0);
				return m_number;
			}
		}
		else {
			isNegative = false;
		}
		
		//	First Number
		Number firstNumber;
		try
		{
			firstNumber = m_format.parse(token);
		}
		catch (ParseException pe1)
		{
			log.log(Level.SEVERE, "Calculator.evaluate - token: " + token, pe1);
			m_number = new BigDecimal(0.0);
			return  m_number;
		}
		BigDecimal firstNo =  new BigDecimal(firstNumber.toString());

		//	intermediate result
		m_number = firstNo;

		//	only one number
		if (!st.hasMoreTokens())
			return m_number;

		//	now we should get an operand
		token = st.nextToken();
		System.out.println("token2 "+token);
		
		if (OPERANDS.indexOf(token) == -1)
		{
			log.log(Level.SEVERE, "Calculator.evaluate - Unknown token: " + token);
			return m_number;
		}
		//	get operand
		char op = token.charAt(0);
		
		if((op == 'l' || op == 'r') && (isNegative)) {
			log.log(Level.SEVERE, "Invalid Operations with negative numbers");
			return m_number;
		}
		
		switch (op)
		{
			case '%':
				m_number = firstNo.divide(new BigDecimal(100.0), m_format.getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
				break;
			case 's':
				m_number = new BigDecimal(Math.sin(Math.toRadians(firstNo.doubleValue())));
				break;
			case 'c':
				m_number = new BigDecimal(Math.cos(Math.toRadians(firstNo.doubleValue())));
				break;
			case 't':
				m_number = new BigDecimal(Math.tan(Math.toRadians(firstNo.doubleValue())));
				break;
			case 'l':
				m_number = new BigDecimal(Math.log10(firstNo.doubleValue()));
				break;
			case 'e':
				m_number = new BigDecimal(Math.exp(firstNo.doubleValue()));
				break;
			case 'r':
				m_number = new BigDecimal(Math.sqrt(firstNo.doubleValue()));
			default:
				break;
		}
		
		//	no second number
		if (!st.hasMoreTokens())
			return m_number;

		token = st.nextToken();
		System.out.println("token3 "+token);
		if (token.contains("od")) {
			if (token.equals("od"))
				return m_number;
			token = token.substring(2, token.length());
		}
		System.out.println("token3 "+token);
		
		Number secondNumber;
		try
		{
			secondNumber = m_format.parse(token);
		}
		catch (ParseException pe2)
		{
			log.log(Level.SEVERE, "Calculator.evaluate - token: " + token, pe2);
			m_number = new BigDecimal(0.0);
			return m_number;
		}
		BigDecimal secondNo = new BigDecimal(secondNumber.toString());

		//	Check the next operand
		char op2 = 0;
		if (st.hasMoreTokens())
		{
			token = st.nextToken();
			if (OPERANDS.indexOf(token) == -1)
			{
				log.log(Level.SEVERE, "Calculator.evaluate - Unknown token: " + token);
				return m_number;
			}
			//	get operand
			op2 = token.charAt(0);
		}

		//	Percent operation
		if (op2 == '%')
			secondNo = secondNo.divide(new BigDecimal(100.0), m_format.getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);

		switch (op)
		{
			case '/':
				m_number = firstNo
					.divide(secondNo, m_format.getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
				break;
			case '*':
				m_number = firstNo.multiply(secondNo);
				break;
			case '-':
				m_number = firstNo.subtract(secondNo);
				break;
			case '+':
				m_number = firstNo.add(secondNo);
				break;
			case '^':
				m_number = firstNo.pow(secondNo.intValue());
				break;
			case 'm':
				m_number = firstNo.remainder(secondNo);
			default:
				break;
		}
		return m_number.setScale(m_format.getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
	}	//	evaluate


	/**
	 *	Display or don't display Currency
	 */
	private void toggleCurrency()
	{
		if (currencyPanel.isVisible())
			currencyPanel.setVisible(false);
		else
		{
			if (!m_currencyOK)
				loadCurrency();
			currencyPanel.setVisible(true);
		}
		pack();
	}	//	toggleCurrency

	/**
	 *	Load Currency
	 */
	private void loadCurrency()
	{
		//	Get Default
		int C_Currency_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "C_Currency_ID");
		if (C_Currency_ID == 0)
			C_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID");

		String sql = "SELECT C_Currency_ID, ISO_Code FROM C_Currency "
			+ "WHERE IsActive='Y' ORDER BY 2";
		KeyNamePair defaultValue = null;
		try
		{
			Statement stmt = DB.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				int id = rs.getInt("C_Currency_ID");
				String s = rs.getString("ISO_Code");
				KeyNamePair p = new KeyNamePair(id, s);
				curFrom.addItem(p);
				curTo.addItem(p);
				//	Default
				if (id == C_Currency_ID)
					defaultValue = p;
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, "Calculator.loadCurrency", e);
		}

		//	Set Defaults
		if (defaultValue != null)
		{
			curFrom.setSelectedItem(defaultValue);
			curTo.setSelectedItem(defaultValue);
		}
		//	Set Listener
		curTo.addActionListener(this);
		m_currencyOK = true;
	}	//	loadCurrency


	/**
	 *	Return Number
	 * 	@return result
	 */
	public BigDecimal getNumber()
	{
		if (m_abort)
			return null;

		return m_number;
	}   //	getNumber
	
	public boolean isDisposeOnEqual()
	{
		return p_disposeOnEqual;
	}
	
	public void setDisposeOnEqual(boolean b)
	{
		p_disposeOnEqual = b;
	}

	/*************************************************************************/

	/**
	 *  KeyPressed Listener
	 * 	@param e event
	 */
	public void keyPressed(KeyEvent e)
	{
		//  sequence:	pressed - typed(no KeyCode) - released

		char input = e.getKeyChar();
		int code = e.getKeyCode();

		e.consume();		//	does not work on JTextField

		if (code == KeyEvent.VK_DELETE)
			input = 'A';
		else if (code == KeyEvent.VK_BACK_SPACE)
			input = 'C';
		else if (code == KeyEvent.VK_ENTER)
			input = '=';
		else if (code == KeyEvent.VK_SHIFT)
			// ignore
			return;
		//	abort
		else if (code == KeyEvent.VK_CANCEL || code == KeyEvent.VK_ESCAPE)
		{
			m_abort = true;
			dispose();
			return;
		}
		handleInput(input);
	}

	/**
	 *  KeyTyped Listener (nop)
	 * 	@param e event
	 */
	public void keyTyped(KeyEvent e) {}
	/**
	 *  KeyReleased Listener (nop)
	 * 	@param e event
	 */
	public void keyReleased(KeyEvent e) {}

}	//	Calculator

