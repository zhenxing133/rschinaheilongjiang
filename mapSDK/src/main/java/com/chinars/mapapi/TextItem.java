package com.chinars.mapapi;
import android.graphics.Color;
import android.graphics.Typeface;
/**
 * 文本项类，表示单条文本。
 * @author rsclouds 中科遥感
 *
 */
public class TextItem {
	  public static final int ALIGN_CENTER = 0;
	  public static final int ALIGN_TOP = 1;
	  public static final int ALIGN_BOTTOM = 2;
	  /*文字显示的位置*/
	  public GeoPoint pt;
	  /*文字颜色*/
	  public int fontColor=Color.BLUE;
	  /*要显示的文字内容*/
	  public String text;
	  /*字号大小*/
	  public int fontSize=20;
	  public int align = ALIGN_CENTER;
	  public Typeface typeface;
	  
	  public TextItem(GeoPoint pt,String text){
		  this.pt=pt;
		  this.text=text;
	  }
}
