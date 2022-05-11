package fr.cg44.plugin.assmat.pdf;

import java.io.IOException;
import java.net.MalformedURLException;

import com.jalios.jcms.Channel;
import com.jalios.util.Util;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Pied de page pour le pdf de recherche et de sélection des assmats
 *
 */
public class FooterAssmatPdf extends PdfPageEventHelper {

  private static Image logoAnnuaireAssmat = null;
  private static BaseFont bf = null;

  String mentionRam;

  public FooterAssmatPdf(){
    this("");
  }

  public FooterAssmatPdf(String mention) {
    this.mentionRam = mention;
  }


  @Override
  public void onEndPage(PdfWriter writer, Document document) {
	  try {
		  
		  
		  if (bf == null) {
	        bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
	      }
		  
		  
		  PdfContentByte cb = writer.getDirectContent();
	      cb.saveState();
	      cb.beginText();
	      cb.setFontAndSize(bf, 7);
	      
	      boolean isPageOrientationPortrait = document.getPageSize().width() == 595f ? true : false;
	      // Affichage des infos des relais
	      if(Util.notEmpty(mentionRam)) {
	        writer.getDirectContent();
	        	        
	        PdfPTable tblfooter = new PdfPTable(1);	
	        PdfPCell tblCell = new PdfPCell(new Paragraph(mentionRam, new Font(bf, 7)));
	        
			tblfooter.setWidths(new int[]{24});
			tblfooter.setTotalWidth(720);
			tblfooter.setLockedWidth(true);
			tblCell.setFixedHeight(40);
			tblCell.setLeading(0, 1.2f);
			tblCell.setBorder(Rectangle.NO_BORDER);
			tblCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			tblfooter.addCell(tblCell);
			tblfooter.writeSelectedRows(0, -1, 25, tblfooter.getTotalHeight() + 5, cb);	        
	        
	      }
	      
	      
	      // Gestion de la numérotation des pages
	      cb.setFontAndSize(bf, 10);
	      cb.setColorFill(new GrayColor(0.3f));
	      float pageNumberHorizontalPosition = isPageOrientationPortrait == true ? 570f : 800f;
	      cb.showTextAligned(PdfContentByte.ALIGN_RIGHT,
	          "Page " + String.valueOf(writer.getCurrentPageNumber()), pageNumberHorizontalPosition, 25f,
	          0f);
	      cb.setColorFill(new GrayColor(1f));

	      cb.endText();
	      // Gestion du logo
	      writer.getDirectContentUnder().addImage(getImageFondPdf(document, isPageOrientationPortrait));
	      cb.restoreState();
	  
	  }
	  
	  catch(DocumentException de) {
		  throw new ExceptionConverter(de);
	  } catch (IOException e) {
		  throw new ExceptionConverter(e);
	}
	  
      
      
  }


  /**
   * gestion en singleton de l'image de fond
   * 
   * @param doc
   *            le document afin de retailer l'image à la taille du document
   * @param isPageOrientationPortrait
   *            indique si la page est en orientation portrait ou non
   *
   * @return l'image de fond
   * @throws BadElementException
   * @throws MalformedURLException
   * @throws IOException
   */
  private static Image getImageFondPdf(final Document doc, boolean isPageOrientationPortrait)
      throws BadElementException, MalformedURLException, IOException {
    logoAnnuaireAssmat = Image.getInstance(Channel.getChannel().getRealPath("/plugins/AssmatPlugin/images/assmat_pdf.jpg"));
    float verticalPosition = isPageOrientationPortrait == true ? 800f : 550f;
    logoAnnuaireAssmat.setAbsolutePosition(10f, verticalPosition);
    logoAnnuaireAssmat.scalePercent(10f, 10f);
    // Si l'image doit comporter des logos en bas de page, le plus
    // simple est de créer une grande image et de la redimensionner par
    // rapport à la taille de la page
    // logoAnnuaireAssmat.scaleToFit(doc.getPageSize().width(),
    // doc.getPageSize().height());
    return logoAnnuaireAssmat;
  }


}
