package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import java.util.ArrayList;
import java.util.*;

public class JgfDbg
{
	final int STR_CNTMAX = 10;
	final int BTN_CNTMAX = 6;
	final float BTN_SIZE_X = 0.60f;
	final float BTN_SIZE_Y = 0.15f;
	final float BTN_MARGIN = 0.01f;
	
	MyMain m_main;
	BitmapFont m_font;
	ArrayList<String> m_logs;
	JgfDbgMenu m_menu;
	
	public JgfDbg()
	{
		m_main = JgfGame.getMain();
		m_logs = new ArrayList<String>();
		m_menu = new JgfDbgMenu();
	}
	
	public void changedValueCB(String label)
	{
		//overrideして使う.
	}
	
	public void draw()
	{
		m_menu.draw();
		
		if(!getValueB("Show Log")) return;
		
		m_font.setScale(1f);
		m_font.setColor(Color.RED);
		//spriteは左下座標指定だけどfontは左上
		Matrix4 mat = new Matrix4();
		mat.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		m_main.batch.setProjectionMatrix(mat);
		for(int i = 0; i < m_logs.size(); i++)
		{
			m_font.draw(m_main.batch, m_logs.get(i), 0, Gdx.graphics.getHeight() - 40 * i);
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
	
	public boolean getValueB(String label)
	{
		return m_menu.getValueB(label);
	}
	
	public int getValueI(String label)
	{
		return m_menu.getValueI(label);
	}
	
	public float getValueF(String label)
	{
		return m_menu.getValueF(label);
	}
	
	public void print(String str)
	{
		if(STR_CNTMAX <= m_logs.size())
		{
			m_logs.remove(0);
		}
		m_logs.add(str);
	}
	
	public void registItemsCB()
	{
		//overrideして使う.
	}
	
	public void init()
	{
		m_font = m_main.asset.getFnt("verdana39.fnt");
		m_font.setColor(0, 0, 0, 1);
		m_menu.init();
	}
	
	public boolean update()
	{
		if(!m_menu.update()) return false;
		
		return true;
	}
	
	class JgfDbgMenu
	{
		MyMain m_main;
		String m_path;
		ArrayList<String> m_labels;
		HashMap<String, JgfDbgValue> m_values;
		int m_tapCnt;
		boolean m_taped;
		int m_curPage;
		int m_maxPage;
		JgfBtn m_closeBtn;
		JgfBtn m_upBtn;
		JgfBtn m_downBtn;
		JgfDbgBtn[] m_btns;
		JgfDbgBtn m_pageBtn;

		public JgfDbgMenu()
		{
			m_main = JgfGame.getMain();
			m_labels = new ArrayList<String>();
			m_values = new HashMap<String, JgfDbgValue>();
			m_btns = new JgfDbgBtn[BTN_CNTMAX];
			for(int i = 0; i < BTN_CNTMAX; i++)
			{
				m_btns[i] = new JgfDbgBtn();
			}
			m_closeBtn = new JgfBtn();
			m_upBtn = new JgfBtn();
			m_downBtn = new JgfBtn();
			m_pageBtn = new JgfDbgBtn();
		}

		void addItem(String label)
		{
			m_labels.add(label);
			m_values.put(label, new JgfDbgValue());
		}
		
		void addItem(String label, boolean value)
		{
			m_labels.add(label);
			m_values.put(label, new JgfDbgValue(value));
		}
		
		void addItem(String label, int value, int min, int max)
		{
			m_labels.add(label);
			m_values.put(label, new JgfDbgValue(value, min, max));
		}
		
		void addItem(String label, float value, float min, float max)
		{
			m_labels.add(label);
			m_values.put(label, new JgfDbgValue(value, min, max));
		}
		
		public void draw()
		{
			if(!m_path.equals(""))
			{
				for(int i = 0; i < BTN_CNTMAX; i++)
				{
					if(m_btns[i].isPressed())
					{
						m_btns[i].getSprite().setColor(Color.RED);
					}
					else
					{
						m_btns[i].getSprite().setColor(Color.WHITE);
					}
					m_btns[i].draw();
				}
				m_pageBtn.draw();
				m_closeBtn.draw();
				if(m_upBtn.isPressed())
				{
					m_upBtn.getSprite().setColor(Color.RED);
				}
				else
				{
					m_upBtn.getSprite().setColor(Color.WHITE);
				}
				m_upBtn.draw();
				if(m_downBtn.isPressed())
				{
					m_downBtn.getSprite().setColor(Color.RED);
				}
				else
				{
					m_downBtn.getSprite().setColor(Color.WHITE);
				}
				m_downBtn.draw();
			}
		}

		String getLabelValue(int idx)
		{
			String label = m_labels.get(idx);
			switch(m_values.get(label).type)
			{
			case N: break;
				default: label += " [" + m_values.get(label).getStr() + "]"; break;
			}
			return(label);
		}
		
		String getLabel(int idx)
		{
			return m_labels.get(idx);
		}
		
		JgfDbgValue getValue(int idx)
		{
			String label = m_labels.get(idx);
			return m_values.get(label);
		}
		
		public boolean getValueB(String label)
		{
			return m_values.get(label).bValue;
		}
		
		public int getValueI(String label)
		{
			return m_values.get(label).iValue;
		}
		
		public float getValueF(String label)
		{
			return m_values.get(label).fValue;
		}
		
		public void init()
		{
			m_path = "";
			m_main.dbg.registItemsCB();
			int needBtnCnt = BTN_CNTMAX * MathUtils.ceil((float)m_labels.size() / (float)BTN_CNTMAX);
			for(int iLabel = m_labels.size(); iLabel < needBtnCnt; iLabel++)
			{
				print("ilanel="+iLabel);
				addItem("--"+iLabel+"--");
			}
			m_curPage = 0;
			m_maxPage = m_labels.size() / BTN_CNTMAX;
			Texture tex = m_main.asset.getTex("btn00_close00.png");
			{
				float x = -BTN_SIZE_X/2f;
				float y = BTN_SIZE_Y * (0.5f + BTN_CNTMAX * 0.5f);
				y += BTN_MARGIN * (BTN_CNTMAX * 0.5f + 1f);
				m_closeBtn.setSprite(new Sprite(tex));
				m_closeBtn.getSprite().setSize(BTN_SIZE_Y, BTN_SIZE_Y);
				m_closeBtn.getSprite().setOrigin(BTN_SIZE_Y/2f, BTN_SIZE_Y/2f);
				m_closeBtn.getSprite().setColor(Color.WHITE);
				m_closeBtn.getSprite().setPosition(x, y);
			}
			{
				tex = m_main.asset.getTex("btn00_up00.png");
				float x = +BTN_SIZE_X/2f - BTN_SIZE_Y * 2f -BTN_MARGIN;
				float y = BTN_SIZE_Y * (-0.5f - BTN_CNTMAX * 0.5f);
				y -= BTN_MARGIN * (BTN_CNTMAX * 0.5f);
				m_upBtn.setSprite(new Sprite(tex));
				m_upBtn.getSprite().setSize(BTN_SIZE_Y, BTN_SIZE_Y);
				m_upBtn.getSprite().setOrigin(BTN_SIZE_Y/2f, BTN_SIZE_Y/2f);
				m_upBtn.getSprite().setColor(Color.WHITE);
				m_upBtn.getSprite().setPosition(x, y);
			}
			{
				tex = m_main.asset.getTex("btn00_down00.png");
				float x = +BTN_SIZE_X/2f - BTN_SIZE_Y;
				float y = BTN_SIZE_Y * (-0.5f - BTN_CNTMAX * 0.5f);
				y -= BTN_MARGIN * (BTN_CNTMAX * 0.5f);
				m_downBtn.setSprite(new Sprite(tex));
				m_downBtn.getSprite().setSize(BTN_SIZE_Y, BTN_SIZE_Y);
				m_downBtn.getSprite().setOrigin(BTN_SIZE_Y/2f, BTN_SIZE_Y/2f);
				m_downBtn.getSprite().setColor(Color.WHITE);
				m_downBtn.getSprite().setPosition(x, y);
			}
			{
				tex = m_main.asset.getTex("tex00_window01.png");
				float x = -BTN_SIZE_X/2f;
				float y = BTN_SIZE_Y * (-0.5f - BTN_CNTMAX * 0.5f);
				y -= BTN_MARGIN * (BTN_CNTMAX * 0.5f);
				m_pageBtn.setSprite(new Sprite(tex));
				m_pageBtn.getSprite().setSize(BTN_SIZE_X/2f, BTN_SIZE_Y);
				m_pageBtn.getSprite().setOrigin(BTN_SIZE_X/4f, BTN_SIZE_Y/2f);
				m_pageBtn.getSprite().setColor(Color.WHITE);
				m_pageBtn.getSprite().setPosition(x, y);
				m_pageBtn.getTxt().type = MyTxt.kType_1P;
				m_pageBtn.getTxt().color = Color.WHITE;
				m_pageBtn.getTxt().pos.x = x + BTN_SIZE_X * 0.25f;
				m_pageBtn.getTxt().pos.y = y + BTN_SIZE_Y * 0.5f;
				m_pageBtn.setRate(0f);
			}
			tex = m_main.asset.getTex("tex00_window02.png");
			for(int i = 0; i < BTN_CNTMAX; i++)
			{
				float x = -BTN_SIZE_X/2f;
				float y = BTN_SIZE_Y * (-0.5f + BTN_CNTMAX * 0.5f - i);
				y += BTN_MARGIN * ((BTN_CNTMAX - 1) * 0.5f - i);
				m_btns[i].setSprite(new Sprite(tex));
				m_btns[i].getSprite().setSize(BTN_SIZE_X, BTN_SIZE_Y);
				m_btns[i].getSprite().setOrigin(BTN_SIZE_X/2f, BTN_SIZE_Y/2f);
				m_btns[i].getSprite().setColor(Color.WHITE);
				m_btns[i].getSprite().setPosition(x, y);
				m_btns[i].getTxt().type = MyTxt.kType_1P;
				m_btns[i].getTxt().color = Color.WHITE;
				m_btns[i].getTxt().pos.x = x + BTN_SIZE_X * 0.5f;
				m_btns[i].getTxt().pos.y = y + BTN_SIZE_Y * 0.5f;
			}
			setPage(0);
		}
		
		public boolean isStartDbgMode()
		{
			int tchCnt = 0;
			for(int i = 0; i < 4; i++)
			{
				if(Gdx.input.isTouched(i)) tchCnt++;
			}
			if(tchCnt == 4)
			{
				boolean tap = Gdx.input.isTouched(4);
				if(!m_taped && tap)
				{
					m_tapCnt++;
					m_taped = true;
					if(2 <= m_tapCnt)
					{
						return true;
					}
				}
				else if(m_taped && !tap)
				{
					m_taped = false;
				}
			}
			else
			{
				m_tapCnt = 0;
				m_taped = false;
			}
			return false;
		}
		
		void setPage(int page)
		{
			m_curPage = MathUtils.clamp(page, 0, m_maxPage - 1);
			int iLabel = BTN_CNTMAX * m_curPage;
			for(int iBtn = 0; iBtn < BTN_CNTMAX; iBtn++)
			{
				m_btns[iBtn].getTxt().str = getLabelValue(iLabel);
				m_btns[iBtn].updateRate(getValue(iLabel));
				iLabel++;
			}
			m_pageBtn.getTxt().str = String.valueOf(m_curPage+1) + "/" + m_maxPage;
		}
		
		public boolean update()
		{
			if(m_path.equals(""))
			{
				if(isStartDbgMode())
				{
					m_path = "/";
					print(m_path);
				}
			}
			else
			{
				m_closeBtn.update();
				if(m_closeBtn.isPush())
				{
					m_path = "";
					return false;
				}
				m_upBtn.update();
				if(m_upBtn.isPush())
				{
					setPage(m_curPage - 1);
				}
				m_downBtn.update();
				if(m_downBtn.isPush())
				{
					setPage(m_curPage + 1);
				}
				int iLabel = BTN_CNTMAX * m_curPage;
				for(int iBtn = 0; iBtn < BTN_CNTMAX; iBtn++)
				{
					m_btns[iBtn].update();
					if(m_btns[iBtn].checkChanging(getValue(iLabel)))
					{
						m_main.dbg.changedValueCB(getLabel(iLabel));
						m_btns[iBtn].getTxt().str = getLabelValue(iLabel);
					}
					iLabel++;
				}
				return false;
			}
			return true;
		}
	}
}

class JgfDbgValue
{
	enum kType
	{
		N,
		B,
		I,
		F,
	}
	public kType type;
	public boolean bValue;
	public int iValue;
	public int iMin;
	public int iMax;
	public float fValue;
	public float fMin;
	public float fMax;

	public JgfDbgValue()
	{
		type = kType.N;
	}
	
	public JgfDbgValue(boolean _bValue)
	{
		type = kType.B;
		setValueB(_bValue);
	}

	public JgfDbgValue(int _iValue, int _iMin, int _iMax)
	{
		type = kType.I;
		iMin = _iMin;
		iMax = _iMax;
		setValueI(_iValue);
	}
	
	public JgfDbgValue(float _fValue, float _fMin, float _fMax)
	{
		type = kType.F;
		fMin = _fMin;
		fMax = _fMax;
		setValueF(_fValue);
	}
	
	public String getStr()
	{
		switch(type)
		{
		case B: return String.valueOf(bValue);
		case I: return String.valueOf(iValue);
		case F: return String.format("%1$.2f", fValue);
		}
		return "";
	}
	
	public void setValueB(boolean _bValue)
	{
		bValue = _bValue;
	}
	
	public void setValueI(int _iValue)
	{
		iValue = MathUtils.clamp(_iValue, iMin, iMax);
	}
	
	public void setValueF(float _fValue)
	{
		fValue = MathUtils.clamp(_fValue, fMin, fMax);
	}
}

