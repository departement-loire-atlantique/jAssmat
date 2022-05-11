package fr.cg44.plugin.assmat.pdf;

import java.awt.Color;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

public class Fonts {

  static String fontname = "Helvetica";
  
  static int titlesize = 11;
  
  static int textsize = 10;
	
  public static final Font TITRE_ENTETE = FontFactory.getFont(fontname, titlesize, Font.BOLD);
  
  public static final Font TITRE_BLANC = FontFactory.getFont(fontname, titlesize, Font.BOLD, new Color(255, 255, 255));

  public static final Font DESCRIPTION = FontFactory.getFont(fontname, 9, Font.ITALIC);
  
  public static final Font TITRE_TABLEAU = FontFactory.getFont(fontname, titlesize, Font.BOLD);
  
  public static final Font SOUSTITRE_TABLEAU = FontFactory.getFont(fontname, textsize, Font.ITALIC);
  
  public static final Font ENTETE_TABLEAU = FontFactory.getFont(fontname, titlesize, Font.BOLD);
  
  public static final Font TEXTE = FontFactory.getFont(fontname, textsize);
  
  public static final Font TEXTE_GRAS = FontFactory.getFont(fontname, textsize, Font.BOLD);
  
  public static final Font TEXTE_PIED = FontFactory.getFont(fontname, textsize, Font.ITALIC);
  
  public static final Font PAGE_PIED = FontFactory.getFont(fontname, 8);

}
