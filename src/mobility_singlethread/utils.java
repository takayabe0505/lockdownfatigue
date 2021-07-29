package mobility_singlethread;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class utils {


	public static Date nextday_date(Date day) throws ParseException{
		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(day);
		nextCal.add(Calendar.DAY_OF_MONTH, 1);
		Date nextDate = nextCal.getTime();
		return nextDate;
	}
	
	public static Date beforeday_date(Date day) throws ParseException{
		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(day);
		nextCal.add(Calendar.DAY_OF_MONTH, -1);
		Date nextDate = nextCal.getTime();
		return nextDate;
	}
	
	
//	public static String AreaOverlap(
//			LonLat point,
//			GeometryChecker gchecker
//			){
//		List<String> zonecodeList = gchecker.listOverlaps("JCODE",point.getLon(),point.getLat());
//		if(zonecodeList == null || zonecodeList.isEmpty()) { return "no"; }
//		else{ return zonecodeList.get(0);	}
//	}
	
}
