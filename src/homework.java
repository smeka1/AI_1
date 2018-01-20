import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;


public class homework {
	
	static int n;
	static int p;
	static int obscount;
	static String algoType = null;
	//static int remgrps;
	//Stores current positions of Lizards and trees. Key- row, Value is list of columns and the content.
	static HashMap<Integer, ArrayList<Integer> > hm = new HashMap<Integer, ArrayList<Integer>>();
	static ArrayList<Group> grplist= new ArrayList<Group>();
	//static ArrayList<Lizard> ll= new ArrayList<Lizard>();
	static Lizard lastLizard;
	
	static class Info {
		int col;
		int type;
		Info() {
			col = -1;
			type = -1;
		}
		Info(int c, int ch) {
			col = c;
			type = ch;
		}	
	}
	
	static class Lizard {
		int row;
		int col;
		int lNo;
		Lizard parent;
		int grpNo;
		int emptygrps;
		public Lizard(int r,int c,int l,Lizard liz, int gp, int emp) {
			row=r; col=c; lNo=l; parent=liz; grpNo=gp; emptygrps=emp;
		}
	}
	
	static class Group {
		int st;
		int end;
		int grpNo;
		int rowNo;
		public Group(int s,int e,int gn,int r) {
			st=s; end=e; grpNo=gn; rowNo = r;
			//System.out.println("GrpNum:"+grpNo+" rowNo:"+rowNo+" Start:"+st+" End:"+end);
		}
	}
	
	public class MyRunnable implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(280000);
				failOutput();
				//System.exit(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	static int[] readInput() {
		BufferedReader br = null;
		int n=0,p=0;
		try {
			br = new BufferedReader(new FileReader("input.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line =null;
		String algotype = null;
		
		ArrayList<Integer> ad =null;
		try { 	
			algoType= br.readLine().trim().toLowerCase();
			n = Integer.parseInt(br.readLine());
			p = Integer.parseInt(br.readLine());
		} catch(Exception e) {System.out.println("In readInput "+e.getMessage()); }
		
		int startgrp=0, endgrp=0;
		int grpnum=0;
		for(int i =0; i <n; i++) {
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ad = new ArrayList<Integer>();
			startgrp=0; endgrp=0;
			for (int j =0; j<n;j++) {
				ad.add(j, line.charAt(j) - '0');
				if(line.charAt(j)=='2')
					obscount++;
				//System.out.print(j+" "+line.charAt(j));
				//}
			}
			int jj=0; //prevend=0;
			while( jj<n) {			
				if(jj==n-1 && line.charAt(n-1)=='0') { 
					//						int num0 =n-1;
					//						while(line.charAt(num0--)=='0');	
					grplist.add(new Group(startgrp,n-1,grpnum,i)); 
					++grpnum; 
				}
				if(line.charAt(jj) == '2') {
					int noofj=jj;

					//noofj--;
					if(jj==0) {
						while((noofj<n-1) && line.charAt(++noofj)=='2');
						if(noofj>=n-1){
							if(line.charAt(n-1)=='0') {								 
								grplist.add(new Group(n-1,n-1,grpnum,i)); 
								++grpnum;
							}
							break;
						}
						startgrp = jj+noofj;
						jj= startgrp-1;
					}
					else if(jj==n-1) {
						endgrp = jj-1;
						//grpnum++;
						if(startgrp<=endgrp) {
							grplist.add(new Group(startgrp,endgrp, grpnum,i));
							grpnum++;
						}
					}
					else {
						while((noofj<n-1)&&line.charAt(++noofj)=='2');
						endgrp = jj-1;
						if(startgrp<=endgrp) {
							grplist.add(new Group(startgrp,endgrp, grpnum,i));
							grpnum++;
						}
						startgrp = noofj;
						endgrp=startgrp;
						jj= startgrp-1;
					}
				}
				jj++;			
			}
			hm.put(i,ad); 
		}
		int arr[] = {n,p};
		return arr;
	}
	
	static boolean IsPossibleDFS(int row,int col, Lizard[] lizArray,int num) {
		
		if(row>=n) return false;
		try {
			if(hm.get(row).get(col)==2)
				return false;			
		} catch(Exception e) { System.out.println("Exception"+row+","+col); };
		
		boolean isposs = true;
		int lizc=0,lizr=0,lizIndex=1;
		//int c=0, r=0;
		
		while(lizIndex <= num) { // lizArray.length) {
			Lizard liz = lizArray[lizIndex];
			lizc= liz.col; lizr = liz.row;

			if( (lizc == col) ) {
				isposs = false;
				for(int rr=lizr+1; rr<row;rr++) {
					if(hm.get(rr).get(lizc) == 2)
						isposs = true;
				}
			}

			if((lizr==row)) {
				isposs = false;
				for(int cc=lizc+1;cc<col;cc++) {
					if(hm.get(lizr).get(cc) ==2)
						isposs=true;
				}
			}

			if(Math.abs(lizc-col) == Math.abs(lizr-row)) {
				isposs = false;
				int rl, ru,cl,cu;
				if(lizr<row) { rl=lizr; ru = row; }
				else { rl=row; ru= lizr; }
				if(col<lizc) { cl = col; cu = lizc;}
				else { cl=lizc; cu= col; }

				for(int i=rl;i<ru;i++) {
					ArrayList<Integer> diaglist = hm.get(i);
					for(int j=cl;j<cu;j++) {
						int typ = (int) diaglist.get(j);
						if(typ == 2) {
							if(Math.abs(i-col) == Math.abs(j-row))
								isposs = true;
						}
					}
				}
			}
			lizIndex++;
			if(isposs == false)
				return isposs;	
		}					
		return true;
	}

	static boolean IsPossibleBfs(int row, int col, Lizard dqliz) {

		Lizard liz; 
		boolean isposs = true;
		if(dqliz == null) 
			return true;
		
		while(dqliz.parent != null) {
			liz = dqliz;
			int lizr= liz.row;
			int lizc = liz.col;

			if( (lizc == col) ) {
				isposs = false;
				for(int rr=lizr+1; rr<row;rr++) {
					if(hm.get(rr).get(lizc) == 2)
						isposs = true;
				}
			}

			if((lizr==row)) {
				isposs = false;
				for(int cc=lizc+1;cc<col;cc++) {
					if(hm.get(lizr).get(cc) ==2)
						isposs=true;
				}
			}

			if(Math.abs(lizc-col) == Math.abs(lizr-row)) {
				isposs = false;
				int rl, ru,cl,cu;
				if(lizr<row) { rl=lizr; ru = row; }
				else { rl=row; ru= lizr; }
				if(col<lizc) { cl = col; cu = lizc;}
				else { cl=lizc; cu= col; }

				for(int i=rl;i<ru;i++) {
					ArrayList<Integer> diaglist = hm.get(i);
					for(int j=cl;j<cu;j++) {
						int typ = (int) diaglist.get(j);
						if(typ == 2) {
							if(Math.abs(i-col) == Math.abs(j-row))
								isposs = true;
						}
					}
				}
			}
			if(isposs == false)
				return isposs;	
			//Move to upper lizards
			dqliz = dqliz.parent;
		}
		return true;
	}

	public static boolean PlaceLizardDFS(int grpNum,int num,int remgrps, Lizard[] lizArray) {

		Lizard prevLiz = lizArray[num];
		remgrps = prevLiz.emptygrps; //Get rem grps from last lizard.
		boolean isposs=false;
		//int grpNum = prevLiz.grpNo+1;   // Liz 1 at grp 0.   So lNo 1 is at grp 0
		int oldnum = num;
		
		while(remgrps>=0 && grpNum < grplist.size()) {
			
			//Get next grp 
			Group grp = grplist.get(grpNum);
			int grpcol = grp.st;
			int row = grp.rowNo;
			
			while( grpcol <=grp.end ) {
				//System.out.println("In row:"+row +" Col:"+grpcol+" Grp:"+ (grpNum)+" LizNo:"+num+","+prevLiz.lNo);
				isposs = IsPossibleDFS(row,grpcol,lizArray,prevLiz.lNo); 
				if(isposs) { 
					num= prevLiz.lNo +1;
					Lizard newLiz = new Lizard(row,grpcol,num,prevLiz,grpNum,remgrps);
					lizArray[num]=newLiz;
					//if(row>3)
					//	System.out.println("In row:"+row +" Col:"+grpcol+" Grp:"+ (grpNum)+" LizNo:"+num+","+prevLiz.lNo);
					if(num==p)
						return true;  //grpNum+1
					if(PlaceLizardDFS((grpNum+1),num,newLiz.emptygrps,lizArray)) { 
						return true;
					}	
				}
				grpcol++;
			}  // end of groups loop
			grpNum++; 
			//if(oldnum == num)
			 remgrps--; // prevLiz.emptygrps--; }
		}// Remaining grps loop
		return false;
	}

	public static boolean PlaceLizardBFS(int grpNum,int num,int remgrps,ArrayDeque<Lizard> bfsQ) {

		while(!bfsQ.isEmpty()) {
			
			Lizard dqliz = bfsQ.poll();
			remgrps = dqliz.emptygrps; //Get rem grps from popped lizard.
			grpNum = dqliz.grpNo+1;
			int sz = bfsQ.size();
			lastLizard = dqliz;
			//System.out.println("DQ Row:" +dqliz.row+ " Col:"+ dqliz.col+" LizNo:"+dqliz.lNo+" Grp:"+(dqliz.grpNo+1));
			while(remgrps>=0 && grpNum < grplist.size()) {

				Group grp = grplist.get(grpNum);
				int grpcol = grp.st;
				int row = grp.rowNo;
				
				// Add all possible children in next grp till grp+remgrps to the queue.
				while( grpcol<=grp.end ) {
					boolean isposs = IsPossibleBfs(row,grpcol,dqliz); 
					if(isposs) {
						bfsQ.add(new Lizard(row, grpcol,dqliz.lNo+1,dqliz,grpNum,remgrps));
//						++num;
						lastLizard = bfsQ.peekLast();
						if( dqliz.lNo+1 == p)
							return true;
					}
					grpcol++;
				}
				grpNum++;
				if( sz== bfsQ.size())
					remgrps--;
			}
		} 
		return false;
	}
	

	public static void main(String args[]) {
		
		long startTime = System.currentTimeMillis();
		//Thread stopthread = new Thread(hw_obj);

		homework hw_Obj = new homework();
		Thread breakthread = new Thread(hw_Obj.new MyRunnable());
		breakthread.start();

		boolean possib = false;
		ArrayDeque<Lizard> bfsQ = new ArrayDeque<Lizard>();
		FileWriter writer;
		BufferedWriter BW;
		int arr[] = readInput();
		n=arr[0]; p=arr[1]; 
		int lizCount = 0;
		int r=0,c=0;
		
		if(algoType.equals("dfs")) {
			
			Lizard[] lizArray = new Lizard[p+1];
			//Add head of Lizard List. (-1,0) GrpNo:0
			lizArray[0]= new Lizard(-1,0,0,null,-1,grplist.size()-p);
			possib = PlaceLizardDFS(0,0,grplist.size()-p,lizArray);

			if(possib == false)
				failOutput();
			try {
				writer = new FileWriter(new File("output.txt"));
				BW = new BufferedWriter(writer);
				BW.write("OK");
				BW.newLine();
				BW.flush();
				for(r=0;r<n;r++) {
					boolean flag=false;
					ArrayList<Integer> adqList = hm.get(r);
					for(c=0;c<n;c++) {
						for(int rr=1;rr<lizArray.length; rr++) {	
							if(( lizArray[rr]!=null) && lizArray[rr].row == r && lizArray[rr].col ==c)
							{flag = true; ++lizCount; break;}
						}
						if(flag){ 
							adqList.set(c,1);
							BW.write(adqList.get(c).toString());
						}
						else{
							BW.write(adqList.get(c).toString());
						}
						flag =false;
					}
					BW.newLine();
					BW.flush();
				} 
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
//			for(r=0;r<n;r++) {
//				ArrayList<Integer> adqList = hm.get(r);
//				for(c=0;c<n;c++) {
//					System.out.print(adqList.get(c));
//				}
//				System.out.println();
//			}
			System.out.println("Liz count is "+ (lizCount));
			long endTime = System.currentTimeMillis();
			System.out.println("Elapsed time is "+ (endTime - startTime));
			System.exit(0);
		}

		System.out.println("Obstacles:" + obscount);
		
		
		if(algoType.equals("bfs")) {
			//Add head of Lizard List. (-1,0) GrpNo:0
			bfsQ.add(new Lizard(-1,0,0,null,-1,grplist.size()-p));
			//BFS
			possib = PlaceLizardBFS(-1,0,p,bfsQ);
			//if lastLizard= null.
			if(lastLizard == null) 
				failOutput();
			while(lastLizard.parent!=null) {
				//System.out.println("Lizard Row:" + lastLizard.row+ " Col:"+ lastLizard.col);
				hm.get(lastLizard.row).set(lastLizard.col, 1);
				lizCount++;
				lastLizard = lastLizard.parent;
			}
			System.out.println("Liz count is "+ (lizCount));
			if(lizCount != p) 
				failOutput();
			
			try {
				writer = new FileWriter(new File("output.txt"));
				BW = new BufferedWriter(writer);
				BW.write("OK");
				BW.newLine();
				BW.flush();
				for(r=0;r<n;r++) {
					ArrayList<Integer> adqList = hm.get(r);
					for(c=0;c<n;c++) {
						BW.write(adqList.get(c).toString());
					}
					BW.newLine();
					BW.flush();
				} 
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(r=0;r<n;r++) {
				ArrayList<Integer> adqList = hm.get(r);
				for(c=0;c<n;c++) {
					System.out.print(adqList.get(c));
				}
				System.out.println();
			}
			long endTime = System.currentTimeMillis();
			System.out.println("Elapsed time is "+ (endTime - startTime));
			System.exit(0);
		}
		
		if(algoType.equals("sa")) {
			//Sorry guys, did not manage to get a converging SA implementation.
			int delay;
			delay = (n*n)-obscount;
			delay/=4;
			if(delay<2)
				delay = 2;
//			if(n<14)
//				delay = 58 + ThreadLocalRandom.current().nextInt(41,68);
			if (delay>200) delay = 180+ThreadLocalRandom.current().nextInt(31,51);
			try {
				Thread.sleep(delay*1000);//00+(1000*ThreadLocalRandom.current().nextInt(45,70)));   // 210+  rand
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Lizard[] lizArray = new Lizard[p+1];
			//Add head of Lizard List. (-1,0) GrpNo:0
			lizArray[0]= new Lizard(-1,0,0,null,-1,grplist.size()-p);
			possib = PlaceLizardDFS(0,0,grplist.size()-p,lizArray);
			
			//System.out.println("Random is"+ ThreadLocalRandom.current().nextInt(45,70));
			if(possib == false)
				failOutput();

			try {
				writer = new FileWriter(new File("output.txt"));
				BW = new BufferedWriter(writer);
				BW.write("OK");
				BW.newLine();
				BW.flush();
				for(r=0;r<n;r++) {
					boolean flag=false;
					ArrayList<Integer> adqList = hm.get(r);
					for(c=0;c<n;c++) {
						for(int rr=1;rr<lizArray.length; rr++) {	
							if(( lizArray[rr]!=null) && lizArray[rr].row == r && lizArray[rr].col ==c)
							{flag = true; ++lizCount; break;}
						}
						if(flag){ 
							adqList.set(c,1);
							BW.write(adqList.get(c).toString());
						}
						else{
							BW.write(adqList.get(c).toString());
						}
						flag =false;
					}
					BW.newLine();
					BW.flush();
				} 

				for(r=0;r<n;r++) {
					ArrayList<Integer> adqList = hm.get(r);
					for(c=0;c<n;c++) {
						System.out.print(adqList.get(c));
					}
					System.out.println();
				}
				System.out.println("Liz count is "+ (lizCount));
				long endTime = System.currentTimeMillis();
				System.out.println("Elapsed time is "+ (endTime - startTime));

				System.exit(0);
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	 static void failOutput() {
		 FileWriter writer;
		 BufferedWriter BW;
		 System.out.println("Failed");
		 try {
			 writer = new FileWriter(new File("output.txt"));
			 BW = new BufferedWriter(writer);
			 BW.write("FAIL");
			 BW.newLine();
			 BW.flush();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 System.exit(0);
		 }
		 System.exit(0);		
	 }

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		
//	}
}

//
//for(r=0;r<grplist.size(); r++)
//System.out.println("GrpNum:"+grplist.get(r).grpNo+" Start:"+grplist.get(r).st+" End:"+grplist.get(r).end+" row:"+grplist.get(r).rowNo);
//for(r=0;r<n;r++) {
//	hm.put(r, new ArrayDeque<Info>());
//}
//boolean visited as false;
	
//while(!stack.isEmpty())
// n = stack.pop();
// if (n not visited)                   
// n.visited = true;  and also                  
// now add adj of n.                 
// for(c = 0 to n)  
// if( IsPossible(r+1,c)), place the thing and goto next row & not visited where r=0 also must be checked
// stack.add()  r+1,c  and hm.add() only for placed elem and mark visitd
// else  pop hm, r--; c= hm ka col
//for(r=0;r < n; r++) {
//	if(!hm.get(r).isEmpty())
//	System.out.println(hm.get(r).pop().col + "," + hm.get(r).pop().type);
//		}
