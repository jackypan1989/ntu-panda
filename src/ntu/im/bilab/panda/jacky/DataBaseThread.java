package ntu.im.bilab.panda.jacky;

import java.util.ArrayList;

public class DataBaseThread implements Runnable{
	public static int num_of_threads = 0;
	private int thread_id;
	
	public DataBaseThread(){
		num_of_threads++;
		thread_id = num_of_threads;
	}
	
	public void run(){
	  /*
	  for(int i=1; i<=10; i++){ 
		 //String tName = Thread.currentThread().getName();       
		  String tName = "T"+thread_id;
		  System.out.println(tName + ":" + i); 
	  }*/ 
	  DataBaseUpdater dbu = new DataBaseUpdater();
	  dbu.updateParameter(thread_id);
	  dbu.Close();
    }
	
	public static void main(String[] args){
		ArrayList<Thread> thread_list = new ArrayList<Thread>();
		
		for(int i = 0 ; i<1000 ; i++){
			thread_list.add(new Thread(new DataBaseThread()));
		}
		
	    for(Thread t : thread_list)
	    	t.start();
	   
	    // 取得目前執行緒數量
	    System.out.println("there are "+Thread.activeCount()+" threads."); 
	}
}
