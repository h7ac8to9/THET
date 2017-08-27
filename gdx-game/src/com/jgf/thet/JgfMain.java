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
	public JgfAsset asset;
	public JgfCam gameCam;
	public JgfCam uiCam;
	
	public void dispose()
	{
		asset.dispose();
	}
	
	public void init()
	{
		asset = new JgfAsset();
		gameCam = new JgfCam();
		uiCam = new JgfCam();
	}
	
	public void update()
	{
		asset.update();
		gameCam.update();
		uiCam.update();
	}
}
