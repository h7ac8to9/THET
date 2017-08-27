package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

public class JgfInput
{
	final int kBtn_None = -1;
	static public final int kBtn_1P_L = 0;
	static public final int kBtn_1P_R = 1;
	static public final int kBtn_2P_L = 2;
	static public final int kBtn_2P_R = 3;
	final int kBtn_CntMax = 4;
	final int kTch_CntMax = 4;
	
	final int kBtnState_None = 0;
	final int kBtnState_Push = 1;
	final int kBtnState_Pressed = 2;
	final int kBtnState_Release = 3;
	
	int[] m_btnTchs;
	int[] m_btnState;
	
	public JgfInput()
	{
		m_btnTchs = new int[kBtn_CntMax];
		m_btnState = new int[kBtn_CntMax];
		for(int iBtn = 0; iBtn < kBtn_CntMax; iBtn++)
		{
			m_btnTchs[iBtn] = kBtn_None;
			m_btnState[iBtn] = kBtnState_None;
		}
	}
	
	public void update()
	{
		for(int iBtn = 0; iBtn < kBtn_CntMax; iBtn++)
		{
			if(m_btnState[iBtn] == kBtnState_Push)
			{
				m_btnState[iBtn] = kBtnState_Pressed;
			}
			if(m_btnState[iBtn] == kBtnState_Release)
			{
				m_btnState[iBtn] = kBtnState_None;
			}
			if(m_btnTchs[iBtn] == kBtn_None)
			{//まだこのボタンを押してない
				for(int iTch = 0; iTch < kTch_CntMax; iTch++)
				{
					if(!Gdx.input.isTouched(iTch)) continue;
					int tchX = Gdx.input.getX(iTch);
					int tchY = Gdx.input.getY(iTch);
					int centerX = Gdx.graphics.getWidth() / 2;
					int centerY = Gdx.graphics.getHeight() / 2;
					boolean isPush = false;
					switch(iBtn)
					{
						case kBtn_1P_L:
							isPush = (tchX < centerX && centerY < tchY);
							break;
						case kBtn_1P_R:
							isPush = (centerX < tchX && centerY < tchY);
							break;
						case kBtn_2P_L:
							isPush = (centerX < tchX && tchY < centerY);
							break;
						case kBtn_2P_R:
							isPush = (tchX < centerX && tchY < centerY);
							break;
					}
					if(isPush)
					{
						m_btnTchs[iBtn] = iTch;
						m_btnState[iBtn] = kBtnState_Push;
					}
				}
			}
			else
			{//このボタンを押してる中
				if(Gdx.input.isTouched(m_btnTchs[iBtn])) continue;
				m_btnTchs[iBtn] = kBtn_None;
				m_btnState[iBtn] = kBtnState_Release;
			}
		}
	}
	
	boolean isBtnPush(int btn)
	{
		return(m_btnState[btn] == kBtnState_Push);
	}
	
	boolean isBtnPressed(int btn)
	{
		return(m_btnState[btn] == kBtnState_Pressed);
	}
	
	boolean isBtnRelease(int btn)
	{
		return(m_btnState[btn] == kBtnState_Release);
	}
}
