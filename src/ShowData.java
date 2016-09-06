import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * This class takes the transformed data and creates a pdf
 * showing a calendar where each day has a symbol indicating
 * whether the heat and/or air conditioning was turned on for
 * that day.
 * 
 * @author Triton Pitassi
 *
 */
public class ShowData {
	
	//=====Constants=====
	
	/** The location to create the pdf */
	private static final String FILE = "reports/Heating-Cooling.pdf";
	
	/** The font to use for the headings */
	private static final Font headingFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	
	/** The font to use for smaller parts */
	private static final Font smallerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	
	/** The number of days in June */
	public static final int DAYS_IN_JUNE = 30;
	
	//=====Instance Variables=====
	
	/** The array to hold the info telling when heat/AC were on at least once */
	private static boolean[][] heatingCoolingData;
	
	/** The images to be used to visualize the data 
	 * 
	 * fire image found at https://www.ncptt.nps.gov/articles/disasters/wildland-structural-fire/
	 * snowflake image found at http://www.clipartpanda.com/categories/snowflake-clipart-transparent-background
	 * */
	private static Image imgFire, imgSnowflake;
	
	/**
	 * The constructor
	 * 
	 * @param data 
	 * 				the array of data created by the FetchForecast class
	 * 
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public ShowData(boolean[][] data) throws BadElementException, MalformedURLException, IOException {
		
		//copy the array
		heatingCoolingData = data;
		
		//set up the images
		imgFire = Image.getInstance("img/fire.png");
		imgFire.setWidthPercentage(30);
		
		imgSnowflake = Image.getInstance("img/snowflake.png");
		imgSnowflake.setWidthPercentage(30);
		
		try {
			//create the file
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(FILE));
			
			//open the file
			document.open();
			
			//add the metadata
			addMetaData(document);
			
			//add the content
			addContent(document);
			
			//close the document
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the metadata to the file
	 * 
	 * @param document
	 * 				the file to use
	 */
	private static void addMetaData(Document document) {
		document.addTitle("Heating and Cooling Data");
		document.addSubject("Work Sample Test");
		document.addAuthor("Triton Pitassi");
		document.addCreator("Triton Pitassi");
	}

	/**
	 * Adds the content to the document
	 * 
	 * @param document
	 * 				the file to use
	 * 
	 * @throws DocumentException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void addContent(Document document) throws DocumentException, MalformedURLException, IOException {
		
		//creates the heading
		Anchor anchor = new Anchor("Heating and Cooling Data 6/1/2016 - 6/30/2016", headingFont);
		anchor.setName("Heating and Cooling Data 6/1/2016 - 6/30/2016");

		//the main element to add the heading to
		Paragraph docElement = new Paragraph(anchor);
		
		//creates an element that is only a few lines of space
		Paragraph space = new Paragraph("");
		addEmptyLine(space, 2);
		
		//adds the space to the main element
		docElement.add(space);
		
		//creates an element for the legend and adds it
		docElement.add(new Paragraph("Legend"));

		//adds the content for the two items in the legend
		docElement.add(new Paragraph("The fire symbol indicates the heater was turned on at least once during the day.\n", smallerFont));
		docElement.add(new Paragraph("The snowflake symbol indicates the air conditioning was turned on at least once during the day.", smallerFont));
		
		//adds more space
		docElement.add(space);
		
		//adds a table
		createCalendar(docElement);

		//now add all this to the document
		document.add(docElement);

	}

	/**
	 * This method creates the basic structure of the calendar that
	 * is used to visualize the data
	 * 
	 * @param docElement
	 * 				the main element to add other elements to
	 * 
	 * @throws DocumentException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void createCalendar(Paragraph docElement)
			throws DocumentException, MalformedURLException, IOException {
		
		//Creates the shell of the calendar
		PdfPTable calendar = new PdfPTable(7);
		
		//The next blocks add the headings of each of the 
		//columns, i.e. the days of the week
		PdfPCell c1 = new PdfPCell(new Phrase("Su"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);

		c1 = new PdfPCell(new Phrase("M"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);

		c1 = new PdfPCell(new Phrase("Tu"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("W"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Th"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("F"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Sa"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		calendar.addCell(c1);
		calendar.setHeaderRows(1);
		
		//June 1 is a Wednesday so we need to fill three cells with
		//the empty string
		calendar.addCell("");
		calendar.addCell("");
		calendar.addCell("");
		
		//add the data to the calendar
		addHeatCoolData(docElement, calendar);
	}
	
	/**
	 * Goes through each day and, using the transformed data, put 
	 * an image of fire on days when the heater turned on and a
	 * snowflake on days when the air conditioning turned on
	 * 
	 * @param docElement
	 * 				the main element to add other elements to
	 * 
	 * @param calendar
	 * 				the shell of the calendar
	 * 		
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void addHeatCoolData(Paragraph docElement, PdfPTable calendar) throws BadElementException, MalformedURLException, IOException {
		
		//loop through all the days in June
		for(int i = 1; i <= DAYS_IN_JUNE; i++) {
		
			//create a cell to put in the calendar
			PdfPCell tempCell = new PdfPCell();
			tempCell.setFixedHeight(75);
			
			//add the day of the month
			tempCell.addElement(new Phrase("" + i));
			
			//if the heater was on, put the fire image in the cell
			if(heatingCoolingData[FetchForecast.HEAT][i - 1]) {
				tempCell.addElement(imgFire);
			}
			
			//if the air conditioning was on, put a snowflake image in the cell
			if(heatingCoolingData[FetchForecast.AIR_COND][i - 1]) {
				tempCell.addElement(imgSnowflake);
			}
			
			//add the cell to the calendar
			calendar.addCell(tempCell);
			
		}
		
		//this is needed to make sure the calendar shows.
		//if the last row isn't completed, the table isn't visible
		calendar.completeRow();
		
		//add the calendar to the main document element
		docElement.add(calendar);
	}

	/**
	 * This method is used to create vertical space between elements
	 * using a Paragraph element
	 * 
	 * @param paragraph
	 * 				the element to add empty lines to
	 * 
	 * @param number
	 * 				the number of empty lines to add
	 * 
	 */
	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
} 	
