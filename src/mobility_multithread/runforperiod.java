package mobility_multithread;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

class runforperiod extends Thread{

	private String startdate = "20200101";
	private String enddate   = "20200301";
	private Integer threadNum = 0;

	public runforperiod(
			String startdate, 
			String enddate,
			Integer threadNum
			){
		this.startdate = startdate;
		this.enddate   = enddate;
		this.threadNum = threadNum;
	}

	public void run(){
		String root = "/mnt/tyabe/"; 
		String home = root+"fatigue_0717/"; File home_f = new File(home); home_f.mkdir();
		String gpspath    = "/mnt/log/covid/loc/";
		System.out.println("T"+String.valueOf(threadNum)+": starting "+startdate+" "+enddate);

		//		 home estimation --------------------
		File idhome_f = new File(home+"id_homelocs_T"+String.valueOf(threadNum)+".csv");

		HashMap<String, LonLat> id_home = new HashMap<String, LonLat>();
		HashMap<String, String> id_code = new HashMap<String, String>();

		if(idhome_f.exists()) {
			try {
				id_home = homelocs.getidll(idhome_f);
			} catch (IOException e) {e.printStackTrace();}
		}
		else {
			try {
				id_home = homelocs.getHomes(startdate, enddate, gpspath, idhome_f);
			} 
			catch (NumberFormatException | IOException | ParseException e) {
				e.printStackTrace();
			}
			System.out.println("T"+String.valueOf(threadNum)+": got home locations");
		}

		// get id-code 
		try {
			id_code = homelocs.getidcode(idhome_f);
		} catch (IOException e) {e.printStackTrace();}

		// get mobility metrics -------------------------

		// get avg,max distance from home, total travel distance, rg
		String resdir  = home+"metrics_bydays/"; File resdir_f = new File(resdir); resdir_f.mkdir();
		try {
			mobilitymetrics.runmetrics(threadNum, startdate, enddate, gpspath, resdir, id_home, id_code);
		} catch (ParseException | IOException e) {e.printStackTrace();}


		// total nighttime population in bustling areas 
		/**
		 * TODO when I get data from JORAS
		 */


		//		try {
		//			mobilitymetrics.runmetrics_dummy(startdate, enddate);
		//		} catch (ParseException | IOException e) {e.printStackTrace();}


		System.out.println("T"+String.valueOf(threadNum)+": finished "+startdate+" "+enddate);		
	}
}






