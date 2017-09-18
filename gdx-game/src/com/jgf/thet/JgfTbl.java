package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import java.util.*;

public class JgfTbl
{
	MyMain m_main;
	
	HashMap<String,Integer> m_keys;
	HashMap<String,Integer> m_labels;
	boolean m_useKey;
	boolean m_useLabel;
	ArrayList<ArrayList<String>> m_datas;

	public JgfTbl()
	{
		m_main = MyGame.getMain();
	}
	
	public String getCellS(String key, String label)
	{
		if(m_keys == null) return "[ERROR]";
		if(m_labels == null) return "[ERROR]";
		int row = m_keys.get(key);
		int col = m_labels.get(label);
		return getCellS(row, col);
	}
	
	public String getCellS(String key, int col)
	{
		if(m_keys == null) return "[ERROR]";
		int row = m_keys.get(key);
		return getCellS(row, col);
	}
	
	public String getCellS(int row, String label)
	{
		if(m_labels == null) return "[ERROR]";
		int col = m_labels.get(label);
		return getCellS(row, col);
	}
	
	public String getCellS(int row, int col)
	{
		if(m_datas == null) return "[ERROR]";
		if(m_datas.size() <= row) return "[ERROR]";
		if(m_datas.get(row).size() <= col) return "[ERROR]";
		return m_datas.get(row).get(col);
	}
	
	public int getColCnt()
	{
		if(m_datas == null) return -1;
		if(m_datas.size() == 0) return -1;
		return m_datas.get(0).size();
	}
	
	public int getRowCnt()
	{
		if(m_datas == null) return -1;
		return m_datas.size();
	}
	
	public void setupCsv(String csvPath, boolean useKey, boolean useLabel)
	{
		FileHandle file = Gdx.files.internal(csvPath);
		String text = file.readString();
		
		m_useKey = useKey;
		if(m_useKey) m_keys = new HashMap<>();
		m_useLabel = useLabel;
		if(m_useLabel) m_labels = new HashMap<>();
		
		m_datas = new ArrayList<>();
		String br = "\r\n|[\n\r\u2028\u2029\u0085]";
		String[] lines = text.split(br);
		int row = 0;
		int col = 0;
		for(String line : lines)
		{
			col = 0;
			ArrayList<String> data = new ArrayList<>();
			String[] cells = line.split(",",-1); //-1を入れないと空セルがスキップされちゃう.
			for(String cell : cells)
			{
				data.add(cell);
				if(m_useKey && col == 0)
				{
					m_keys.put(cell, row);
				}
				if(m_useLabel && row == 0)
				{
					m_labels.put(cell, col);
				}
				col++;
			}
			m_datas.add(data);
			row++;
		}
	}
}
