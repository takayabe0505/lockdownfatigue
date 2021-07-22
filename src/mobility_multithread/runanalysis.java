package mobility_multithread;

class LockdownFatigue{

	// parallel thread implementation from: 
	// http://www.itsenka.com/contents/development/java/topics/thread2.html

	public static void main(String args[]){

		//Threadã‚¯ãƒ©ã‚¹ç¶™æ‰¿ã?®ã‚µãƒ–ã‚¯ãƒ©ã‚¹ã?®ã‚¤ãƒ³ã‚¹ã‚¿ãƒ³ã‚¹ã‚’ç”Ÿæˆ?ã?™ã‚‹
		runforperiod analysis1 = new runforperiod("20200101","20200301",1);
		runforperiod analysis2 = new runforperiod("20200301","20200501",2);
		runforperiod analysis3 = new runforperiod("20200501","20200701",3);
		runforperiod analysis4 = new runforperiod("20200701","20200901",4);
		runforperiod analysis5 = new runforperiod("20200901","20201101",5);
		runforperiod analysis6 = new runforperiod("20201101","20210101",6);
		runforperiod analysis7 = new runforperiod("20210101","20210301",7);
		runforperiod analysis8 = new runforperiod("20210301","20210501",8);
		runforperiod analysis9 = new runforperiod("20210501","20210701",9);

		//ç”Ÿæˆ?ã?—ã?Ÿã‚¹ãƒ¬ãƒƒãƒ‰ã?®ã‚¤ãƒ³ã‚¹ã‚¿ãƒ³ã‚¹ã?®startãƒ¡ã‚½ãƒƒãƒ‰ã‚’å‘¼ã?³å‡ºã?™
		analysis1.start();
		analysis2.start();
		analysis3.start();
		analysis4.start();
		analysis5.start();
		analysis6.start();		
		analysis7.start();
		analysis8.start();		
		analysis9.start();

	}
}

