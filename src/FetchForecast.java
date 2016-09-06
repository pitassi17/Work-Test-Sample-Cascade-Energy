import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.ForecastIO;

/**
 * This class fetches the information for the date range
 * and transforms it into a boolean array to be used in
 * the creation of the pdf file
 * 
 * @author Triton Pitassi
 *
 */
public class FetchForecast {

	//=====Constants=====
	
	/** The api key to use */
	private static final String API_KEY = "f68ffe36fce74d960c89da421b025150";
	
	/** The longitude of the address */
	private static final String LONGITUDE = "45.5898";
			
	/** The latitude of the address */
	private static final String LATITUDE = "-122.5951";
	
	/** The index that contains information about the air conditioning */
	public static final int AIR_COND = 0;
	
	/** The index that contains information about the heating system */
	public static final int HEAT = 1;
	
	/** The temperature at which the air conditioning turns on */
	private static final double A_C_START_TEMP = 75;
	
	/** The temperature at which the heating system turns on */
	private static final double HEATING_START_TEMP = 62;
	
	//=====Instance Variables=====
	
	/** The forecast io object used to retrieve the weather data */
	private ForecastIO fio;
	
	/**
	 * The constructor
	 */
	public FetchForecast() {
		init();
	}
	
	/**
	 * Initializes certain properties of the forecast object
	 */
	private void init() {
		
		//necessary to use the api
		fio = new ForecastIO(API_KEY);  
		
		//set so we get the temperature in fahrenheit
		fio.setUnits(ForecastIO.UNITS_US); 
		
		//exclude data we don't need
		fio.setExcludeURL("hourly,minutely");
		
	}
	
	/**
	 * Gets the forecasts for the range 6/1/2016 - 6/30/2016. Uses the
	 * data to determine when the air conditioning and the heating
	 * system were turned on
	 * 
	 * @return a boolean array containing two arrays, one telling the days
	 * 		   the heat was turned on, the other when the AC was on
	 */
	public boolean[][] getForecasts() {
		
		//2D array to hold the info telling when heat/AC were on at least once
		boolean[][] heatingCoolingData = new boolean[2][ShowData.DAYS_IN_JUNE];
		
		//Loops for all the days in June to get the data and determine
		//the heating and cooling status
		for(int i = 1; i <= 30; i++) {
			
			//a string denoting the date to get weather data for
			String time;
			
			//makes sure the syntax is correct for each day
			if(i < 10) {
				time = "2016-06-0" + i + "T12:00:00-0700";
			} else {
				time = "2016-06-" + i + "T12:00:00-0700";
			}
			
			//set the time and get the forecast data
			fio.setTime(time);
			fio.getForecast(LONGITUDE, LATITUDE);
			
			//Setup to look at the daily info
			FIODaily dayInfo = new FIODaily(fio);
			
			//the maximum temperature for the day
			double dailyMaxTemp = dayInfo.getDay(0).temperatureMax();
			
			//determines if the AC was turned on at least once
			if(dailyMaxTemp >= A_C_START_TEMP) {
				heatingCoolingData[AIR_COND][i - 1] = true;
			}
			
			//the minimum temperature for the day
			double dailyMinTemp = dayInfo.getDay(0).temperatureMin();
			
			//determines if the heat was turned on at least once
			if(dailyMinTemp <= HEATING_START_TEMP) {
				heatingCoolingData[HEAT][i - 1] = true;
			}
		}
		
		//return the data
		return heatingCoolingData;
	}
}
