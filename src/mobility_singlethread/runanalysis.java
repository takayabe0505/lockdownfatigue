package mobility_singlethread;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class runanalysis {

	public static void main(String[] args) throws ParseException, IOException {

		String root = "/mnt/tyabe/"; File root_f = new File(root); root_f.mkdir();
		String home = root+"fatigue_0717/"; File home_f = new File(home); home_f.mkdir();
		String gpspath    = "/mnt/log/covid/loc/";

		String startdate = "20210701";
		String enddate   = "20210101";


		Date start_date_date = new SimpleDateFormat("yyyyMMdd").parse(startdate);
		Date end_date_date   = new SimpleDateFormat("yyyyMMdd").parse(enddate);
		Date date = start_date_date;
		while(date.after(end_date_date)){
			String date_str = new SimpleDateFormat("yyyyMMdd").format(date);
			Date next_date = utils.beforeday_date(date);
			File gps1 = new File(gpspath+date_str+".tsv");

			if((gps1.exists()) && (gps1.length()>0) ) {
				String idhomedir  = home+"homes/"; File idhomedir_f = new File(idhomedir); idhomedir_f.mkdir();
				File idhome = new File(idhomedir+"id_homelocs_"+date_str+".csv"); 
				if(!idhome.exists()) {
					HashMap<String, HashMap<String, LonLat>> id_datetime_ll_morning = homelocs_singthr.getmorninglogs(gps1, date_str);
					HashMap<String, LonLat> id_home = homelocs_singthr.gethomelocs(id_datetime_ll_morning, idhome);
					//				HashMap<String, String> id_code = homelocs_singthr.getidcode(idhome);

					String resdir  = home+"metrics_bydays/"; File resdir_f = new File(resdir); resdir_f.mkdir();
					File out = new File(resdir+date_str+"_metrics.csv");
					HashMap<String, HashMap<String, LonLat>> id_datetime_ll = mobilitymetrics_singthr.getlogs(gps1, id_home);
					System.out.println("--- got logs "+date_str+" "+String.valueOf(id_datetime_ll.size()));
					mobilitymetrics_singthr.getRG_TTD_disp(id_datetime_ll, date_str, out, id_home);
					System.out.println("### DONE metrics for "+date_str);
				}
				else {
					System.out.println("!!! already done "+date_str); 
				}
			}
			else {
				System.out.println("!!! no data for "+date_str); 
			}	

			date = next_date;
		}


	}


}
