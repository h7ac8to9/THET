package com.jgf.thet;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import java.util.*;

public class JgfMain
{
	public SpriteBatch batch;
	public JgfAsset asset;
	public MyDbg dbg;
	public JgfCam gameCam;
	public JgfCam uiCam;
	
	public void dispose()
	{
		asset.dispose();
	}
	
	public void draw()
	{
		
	}
	
	public void init()
	{
		batch = new SpriteBatch();
		asset = new JgfAsset();
		gameCam = new JgfCam();
		uiCam = new JgfCam();
	}
	
	public boolean update()
	{
		if(!dbg.update()) return false;
		asset.update();
		gameCam.update();
		uiCam.update();
		return true;
	}
}
