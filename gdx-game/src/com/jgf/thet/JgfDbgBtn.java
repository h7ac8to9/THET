package com.jgf.thet;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.*;

public class JgfDbgBtn extends JgfBtn
{
	MyTxt m_txt;
	float m_rate;
	int m_iValueStart;
	float m_fValueStart;

	public JgfDbgBtn()
	{
		super();
		
		m_txt = new MyTxt();
		m_rate = 0f;
	}
	
	public boolean checkChanging(JgfDbgValue value)
	{
		if(isPush())
		{
			switch(value.type)
			{
			case N:
				return true;
			case B:
				value.setValueB(!value.bValue);
				updateRate(value);
				return true;
			case I:
				m_iValueStart = value.iValue;
				return false;
			case F:
				m_fValueStart = value.fValue;
				return false;
			}
		}
		else if(isPressed())
		{
			Rectangle rect = m_sprite.getBoundingRectangle();
			float drag = m_tchPosCur.x - m_tchPosStart.x;
			drag /= rect.width;
			switch(value.type)
			{
			case I:
				int iRange = value.iMax - value.iMin;
				int iPlus = (int)((float)iRange * drag);
				value.setValueI(m_iValueStart + iPlus);
				updateRate(value);
				return true;
			case F:
				float fRange = value.fMax - value.fMin;
				float fPlus = fRange * drag;
				value.setValueF(m_fValueStart + fPlus);
				updateRate(value);
				return true;
			}
		}
		else if(isRelease())
		{
		}
		return false;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		
		m_txt.clear();
	}

	@Override
	public void draw()
	{
		super.draw();
		
		Rectangle rect = m_sprite.getBoundingRectangle();
		rect.x += 0.02f;
		rect.y += 0.02f;
		rect.width -= 0.02f * 2f;
		rect.height -= 0.02f * 2f;
		rect.width *= m_rate;
		m_main.batch.end();
		ShapeRenderer m_shape = new ShapeRenderer();
		m_shape.setProjectionMatrix(m_main.batch.getProjectionMatrix());
		m_shape.begin(ShapeRenderer.ShapeType.Filled);
		m_shape.setColor(Color.DARK_GRAY);
		m_shape.rect(rect.x, rect.y, rect.width, rect.height);
		m_shape.end();
		m_main.batch.begin();
		
		if(!m_txt.str.equals(""))
		{
			//現在のMatrixをとっておく
			Matrix4 matProjPrev = m_main.batch.getProjectionMatrix().cpy();
			Matrix4 matTransPrev = m_main.batch.getTransformMatrix().cpy();

			//Matrixを一時的に変える
			Matrix4 matProj = new Matrix4();
			matProj.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			m_main.batch.setProjectionMatrix(matProj);
			Vector2 screen;
			Matrix4 matTrans = new Matrix4();
			BitmapFont font = m_main.dbg.getFont();

			{
				//font
				font.setColor(m_txt.color);
				font.setScale(m_txt.scale);
				BitmapFont.TextBounds bnds = font.getBounds(m_txt.str);
				int type = m_txt.type;

				if(type == MyTxt.kType_1P2P || type == MyTxt.kType_1P)
				{//1P
					screen = m_main.gameCam.WorldToScreen(+m_txt.pos.x, +m_txt.pos.y);
					screen.x -= bnds.width / 2;
					screen.y += bnds.height / 2;
					matTrans.idt();
					matTrans.trn(screen.x, screen.y, 0f);
					m_main.batch.setTransformMatrix(matTrans);
					font.draw(m_main.batch, m_txt.str, 0f, 0f);
				}

				if(type == MyTxt.kType_2P)
				{//2P
					screen = m_main.gameCam.WorldToScreen(-m_txt.pos.x, -m_txt.pos.y);
					screen.x += bnds.width / 2;
					screen.y -= bnds.height / 2;
					matTrans.idt();
					matTrans.rotate(new Vector3(0f, 0f, 1f), 180f);
					matTrans.trn(screen.x, screen.y, 0f);
					m_main.batch.setTransformMatrix(matTrans);
					font.draw(m_main.batch, m_txt.str, 0f, 0f);
				}
			}

			//Matrixを元に戻す
			m_main.batch.setTransformMatrix(matTransPrev);
			m_main.batch.setProjectionMatrix(matProjPrev);
		}
	}
	
	public MyTxt getTxt()
	{
		return m_txt;
	}
	
	public void setRate(float rate)
	{
		m_rate = rate;
	}
	
	public void updateRate(JgfDbgValue value)
	{
		switch(value.type)
		{
		case N:
			m_rate = 0f;
			return;
		case B:
			if(value.bValue) m_rate = 1f;
			else m_rate = 0f;
			return;
		case I:
			m_rate = (float)(value.iValue - value.iMin) / (float)(value.iMax - value.iMin);
			m_rate = MathUtils.clamp(m_rate, 0f, 1f);
			return;
		case F:
			m_rate = (value.fValue - value.fMin) / (value.fMax - value.fMin);
			m_rate = MathUtils.clamp(m_rate, 0f, 1f);
			return;
		}
	}
}
