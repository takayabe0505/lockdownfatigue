package mobility_multithread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;


public class homelocs {

	//	protected static final SimpleDateFormat DOW      = new SimpleDateFormat("u");//change time format
	//	protected static final SimpleDateFormat DATE     = new SimpleDateFormat("yyyyMMdd");//change time format
	//	protected static final SimpleDateFormat DATETIME = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	public static HashMap<String, LonLat> getHomes(
			String startdate, 
			String enddate,
			String gpspath,
			File idhome
			) throws NumberFormatException, IOException, ParseException {
		HashMap<String, HashMap<String, LonLat>> id_datetime_ll = new HashMap<String, HashMap<String, LonLat>>();
		Date start_date_date = new SimpleDateFormat("yyyyMMdd").parse(startdate);
		Date end_date_date   = new SimpleDateFormat("yyyyMMdd").parse(enddate);
		Date date = start_date_date;
		while(date.before(end_date_date)){
			String date_str = new SimpleDateFormat("yyyyMMdd").format(date);
			Date next_date = utils.nextday_date(date);
			String dow = new SimpleDateFormat("u").format(date);
			if(Integer.valueOf(dow)<5) {
				File gps1 = new File(gpspath+date_str+".tsv");
				if((gps1.exists()) && (gps1.length()>0) ) {
					getlogs(gps1, date_str, id_datetime_ll);
				}
			}
			System.out.println("--- done "+date_str+", ID list size: "+String.valueOf(id_datetime_ll.size()));
			date = next_date;
		}
		HashMap<String, LonLat> id_home = gethomelocs(id_datetime_ll, idhome);
		return id_home;
	}

	public static void getlogs(
			File in,
			String date,
			HashMap<String, HashMap<String, LonLat>> id_datetime_ll
			) throws NumberFormatException, IOException, ParseException{
		BufferedReader br1 = new BufferedReader(new FileReader(in));
		String line1 = null;
		while((line1=br1.readLine())!=null){
			try {
				String[] tokens = line1.split("\t");
				if(tokens.length>=7){
					String id_br1 = tokens[0];
					if(!id_br1.equals("null")){ if(id_br1.length()>0){
						if(tokens[4].length()>=10){
							Double lon = Double.parseDouble(tokens[3]);
							Double lat = Double.parseDouble(tokens[2]);
							LonLat p = new LonLat(lon,lat);
							String unixtime = tokens[4];
							Date currentDate = new Date (Long.parseLong(unixtime)*((long)1000)); // UTC time
							new SimpleDateFormat("yyyyMMdd HH:mm:ss").setTimeZone(TimeZone.getTimeZone("GMT+9"));
							String datetime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(currentDate);
							String dow = new SimpleDateFormat("u").format(currentDate);
							String time = datetime.split(" ")[1];
							Integer hour = Integer.valueOf(time.split(":")[0]);
							if(Integer.valueOf(dow)<5) {
								if((hour<=8)|(hour>=21)) {
									if(id_datetime_ll.containsKey(id_br1)) {
										id_datetime_ll.get(id_br1).put(datetime, p);
									}
									else {
										HashMap<String, LonLat> tmp = new HashMap<String, LonLat>();
										tmp.put(datetime, p);
										id_datetime_ll.put(id_br1, tmp);
									}}}}}}}
			}
			catch (ArrayIndexOutOfBoundsException  e){
				System.out.println("OUT OF BOUNDS EXCEPTION ----");
				System.out.println(line1);
				System.out.println("----");
			}
			catch (Exception  e){
				System.out.println("OTHER ERROR IN LINE ----");
				System.out.println(line1);
				System.out.println("----");				
			}
		}
		br1.close();
	}


	public static LonLat meanshift(
			HashMap<String, LonLat> date_ll,
			Double bw,
			Double maxshift,
			Double cutoff
			) {
		HashMap<LonLat, Integer> p_count = new HashMap<LonLat, Integer>();
		while(date_ll.size()>0) {
			// choose initial point 
			LonLat init = null;
			Integer z = 0;
			for(String d : date_ll.keySet()) {
				init = date_ll.get(d);
				z+=1;
				if(z==1) {
					break;
				}
			}
			//			System.out.print("init: ");
			//			System.out.println(init);
			// 
			LonLat befmean = init;
			LonLat newmean = new LonLat(0d,0d);
			while(befmean.distance(newmean)>maxshift) { // 
				if(newmean.getLon()!=0d) {
					befmean = newmean;
				}
				Double tmplon = 0d;
				Double tmplat = 0d;
				Double tmpwei = 0d;
				for(String d : date_ll.keySet()) {
					LonLat p = date_ll.get(d);
					Double distance = befmean.distance(p);
					if(distance<cutoff) {
						Double dist2 = Math.pow(distance, 2d);
						Double wei = Math.exp((dist2)/(-2d*(Math.pow(bw, 2d))));
						tmplon += wei*p.getLon();
						tmplat += wei*p.getLat();
						tmpwei += wei;
					}
				}
				newmean = new LonLat(tmplon/tmpwei, tmplat/tmpwei);
				//				System.out.println(newmean);
			}
			//			System.out.println("---");
			// newmean is the stable point 
			Integer counter = 0;
			for(Iterator<String> i = date_ll.keySet().iterator();i.hasNext();){
				String k = i.next();
				LonLat p = date_ll.get(k);
				if(p.distance(newmean)<cutoff){
					i.remove();
					counter+=1;
				}
			}
			p_count.put(newmean, counter);
			//			System.out.print("newmean, counter: ");
			//			System.out.print(newmean); System.out.print(" ");
			//			System.out.print(counter);System.out.print(" ");
			//			System.out.println(date_ll.size());
		}
		// now we have the p_count --> get the p with most count
		Integer maxcount = 0;
		LonLat res = new LonLat(0d,0d);
		for(LonLat p : p_count.keySet()) {
			if(p_count.get(p)>maxcount) {
				res = p;
				maxcount = p_count.get(p);
			}
		}
		return res;
	}




	public static HashMap<String, LonLat> gethomelocs(
			HashMap<String, HashMap<String, LonLat>> id_datetime_ll,
			File out
			) throws IOException{

		String jpnshp = "/mnt/tyabe/japan_ver83/japan_ver83.shp"; 
		GeometryChecker g = new GeometryChecker(new File(jpnshp));

		HashMap<String, LonLat> ids = new HashMap<String, LonLat>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		for (String id : id_datetime_ll.keySet()) {
			HashMap<String, LonLat> datapoints = id_datetime_ll.get(id);
			LonLat home = meanshift(datapoints, 200d, 100d, 500d);
			if(home.getLon()!=0d) {
				Double lon = home.getLon();
				Double lat = home.getLat();
				String meshcode = new Mesh(6,lon,lat).getCode();
				String jcode = utils.AreaOverlap(home, g);
				ids.put(id, new LonLat(lon,lat));
				bw.write(id+","+
						String.valueOf(ids.get(id).getLon())+","+
						String.valueOf(ids.get(id).getLat())+","+
						meshcode+","+jcode); 
				bw.newLine();
			}
		}
		bw.close();
		return ids;
	}

	public static LonLat getavg(HashMap<String, LonLat> data_thisid) {
		Double tmplon = 0d;
		Double tmplat = 0d;
		for(String d : data_thisid.keySet()) {
			LonLat p = data_thisid.get(d);
			tmplon = tmplon + p.getLon();
			tmplat = tmplat + p.getLat();
		}
		Double lon = tmplon / (double)data_thisid.size(); 
		Double lat = tmplat / (double)data_thisid.size(); 
		LonLat avg = new LonLat(lon,lat);
		return avg;
	}

	public static HashMap<String, String> getidcode(File in) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		HashMap<String, String> id_code = new HashMap<String, String>();
		String line = null;
		while((line=br.readLine())!=null) {
			String[] tokens = line.split(",");
			String id = tokens[0];
			String code = tokens[4];
			id_code.put(id, code);
		}
		br.close();
		return id_code;
	}

	public static HashMap<String, LonLat> getidll(File in) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		HashMap<String, LonLat> id_ll = new HashMap<String, LonLat>();
		String line = null;
		while((line=br.readLine())!=null) {
			String[] tokens = line.split(",");
			String id = tokens[0];
			Double lon = Double.parseDouble(tokens[1]);
			Double lat = Double.parseDouble(tokens[2]);
			LonLat p = new LonLat(lon,lat);
			id_ll.put(id, p);
		}
		br.close();
		return id_ll;
	}


}
