package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import java.util.ArrayList;

public class JgfLog
{
	final int STR_CNTMAX = 10;
	
	MyMain m_main;
	BitmapFont m_font;
	ArrayList<String> m_list;
	
	public JgfLog()
	{
		m_main = JgfGame.getMain();
		m_font = m_main.asset.getFnt("verdana39.fnt");
		m_font.setColor(0, 0, 0, 1);
		m_list = new ArrayList<String>();
	}
	
	public void draw(SpriteBatch batch)
	{
		m_font.setScale(1f);
		m_font.setColor(Color.RED);
		//spriteは左下座標指定だけどfontは左上
		Matrix4 mat = new Matrix4();
		mat.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setProjectionMatrix(mat);
		for(int i = 0; i < m_list.size(); i++)
		{
			m_font.draw(batch, m_list.get(i), 0, Gdx.graphics.getHeight() - 40 * i);
		}
	}
	
	public void drawScreen(SpriteBatch batch, String str, float x, float y)
	{
		//matrix設定しないのでdraw()の後で呼ぶこと
		m_font.setColor(Color.BLACK);
		x = (x + 0.5f) * Gdx.graphics.getWidth();
		y = (y + 0.5f) * Gdx.graphics.getHeight();
		m_font.draw(batch, str, x, y);
	}
	
	public BitmapFont getFont()
	{
		return m_font;
	}
	
	public void print(String str)
	{
		if(STR_CNTMAX <= m_list.size())
		{
			m_list.remove(0);
		}
		m_list.add(str);
	}
}
