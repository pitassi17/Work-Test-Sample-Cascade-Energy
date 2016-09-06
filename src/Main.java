import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.BadElementException;

/**
 * This class controls the program. It creates the objects that
 * fetch, transform, and show the data in a pdf file.
 * 
 * @author Triton Pitassi
 * 
 */
public class Main {
	
	/**
	 * The main method. This is run first.
	 * 
	 * @param args 
	 * 				any arguments that are given (not used, but required)
	 * 
	 */
	public static void main(String[] args) throws BadElementException, MalformedURLException, IOException {
		
		//create the object that will utilize the forecast.io api
		FetchForecast fetcher = new FetchForecast();
		
		//get the forecast data for the range 6/1/2016 - 6/30/2016
		boolean[][] heatingCoolingData = fetcher.getForecasts();
		
		//Creates and runs the pdf creator
		ShowData pdfCreator = new ShowData(heatingCoolingData);
	}
	
}
