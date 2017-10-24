package com.jgf.thet;

public class MyDbg extends JgfDbg
{

	@Override
	public void changedValueCB(String label)
	{
		super.changedValueCB(label);
		
		//print(label);
		switch(label)
		{
		case "Temp":
			
			break;
		case "Show Log":
			break;
		}
	}

	@Override
	public void registItemsCB()
	{
		super.registItemsCB();
		
		m_menu.addItem("Temp");
		m_menu.addItem("Show Log", false);
		m_menu.addItem("Test2", 7.5f, 0f, 10f);
		m_menu.addItem("Temp3", false);
		m_menu.addItem("Temp4", false);
		m_menu.addItem("Temp5", false);
		m_menu.addItem("Temp6", false);
		m_menu.addItem("Temp7", false);
		m_menu.addItem("Temp8", false);
		m_menu.addItem("Temp9", false);
	}
	
	
}
