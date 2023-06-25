import java.util.*;
import java.io.*;

	
	public class Graph_M 
	{
		public class Vertex 
		{
			HashMap<String, Integer> nbrs = new HashMap<>();
		}

		static HashMap<String, Vertex> vtces;

		public Graph_M() 
		{
			vtces = new HashMap<>();
		}

		public int numVetex() 
		{
			return this.vtces.size();
		}

		public boolean containsVertex(String vname) 
		{
			return this.vtces.containsKey(vname);
		}

		public void addVertex(String vname) 
		{
			Vertex vtx = new Vertex();
			vtces.put(vname, vtx);
		}

		public void removeVertex(String vname) 
		{
			Vertex vtx = vtces.get(vname);
			ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

			for (String key : keys) 
			{
				Vertex nbrVtx = vtces.get(key);
				nbrVtx.nbrs.remove(vname);
			}

			vtces.remove(vname);
		}

		public int numEdges() 
		{
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());
			int count = 0;

			for (String key : keys) 
			{
				Vertex vtx = vtces.get(key);
				count = count + vtx.nbrs.size();
			}

			return count / 2;
		}

		public boolean containsEdge(String vname1, String vname2) 
		{
			Vertex vtx1 = vtces.get(vname1);
			Vertex vtx2 = vtces.get(vname2);
			
			if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
				return false;
			}

			return true;
		}

		public void addEdge(String vname1, String vname2, int value) 
		{
			Vertex vtx1 = vtces.get(vname1); 
			Vertex vtx2 = vtces.get(vname2); 

			if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
				return;
			}

			vtx1.nbrs.put(vname2, value);
			vtx2.nbrs.put(vname1, value);
		}

		public void removeEdge(String vname1, String vname2) 
		{
			Vertex vtx1 = vtces.get(vname1);
			Vertex vtx2 = vtces.get(vname2);
			
			
			if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
				return;
			}

			vtx1.nbrs.remove(vname2);
			vtx2.nbrs.remove(vname1);
		}

		public void display_Map() 
		{
			System.out.println("\t BD Metro Map");
			System.out.println("\t------------------");
			System.out.println("----------------------------------------------------\n");
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());

			for (String key : keys) 
			{
				String str = key + " =>\n";
				Vertex vtx = vtces.get(key);
				ArrayList<String> vtxnbrs = new ArrayList<>(vtx.nbrs.keySet());
				
				for (String nbr : vtxnbrs)
				{
					str = str + "\t" + nbr + "\t";
                    			if (nbr.length()<16)
                    			str = str + "\t";
                    			if (nbr.length()<8)
                    			str = str + "\t";
                    			str = str + vtx.nbrs.get(nbr) + "\n";
				}
				System.out.println(str);
			}
			System.out.println("\t------------------");
			System.out.println("---------------------------------------------------\n");

		}
		
		public void display_Stations() 
		{
			System.out.println("\n***********************************************************************\n");
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());
			int i=1;
			for(String key : keys) 
			{
				System.out.println(i + ". " + key);
				i++;
			}
			System.out.println("\n***********************************************************************\n");
		}
			
		
		public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) 
		{
			
			if (containsEdge(vname1, vname2)) {
				return true;
			}

			
			processed.put(vname1, true);

			Vertex vtx = vtces.get(vname1);
			ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

			
			for (String nbr : nbrs) 
			{

				if (!processed.containsKey(nbr))
					if (hasPath(nbr, vname2, processed))
						return true;
			}

			return false;
		}
		
		
		private class DijkstraPair implements Comparable<DijkstraPair> 
		{
			String vname;
			String psf;
			int cost;
			@Override
			public int compareTo(DijkstraPair o) 
			{
				return o.cost - this.cost;
			}
		}
		
		public int dijkstra(String src, String des, boolean nan) 
		{
			int val = 0;
			ArrayList<String> ans = new ArrayList<>();
			HashMap<String, DijkstraPair> map = new HashMap<>();

			Heap<DijkstraPair> heap = new Heap<>();

			for (String key : vtces.keySet()) 
			{
				DijkstraPair np = new DijkstraPair();
				np.vname = key;
				
				np.cost = Integer.MAX_VALUE;

				if (key.equals(src)) 
				{
					np.cost = 0;
					np.psf = key;
				}

				heap.add(np);
				map.put(key, np);
			}

			
			while (!heap.isEmpty()) 
			{
				DijkstraPair rp = heap.remove();
				
				if(rp.vname.equals(des))
				{
					val = rp.cost;
					break;
				}
				
				map.remove(rp.vname);

				ans.add(rp.vname);
				
				Vertex v = vtces.get(rp.vname);
				for (String nbr : v.nbrs.keySet()) 
				{
					if (map.containsKey(nbr)) 
					{
						int oc = map.get(nbr).cost;
						Vertex k = vtces.get(rp.vname);
						int nc;
						if(nan)
							nc = rp.cost + 120 + 40*k.nbrs.get(nbr);
						else
							nc = rp.cost + k.nbrs.get(nbr);

						if (nc < oc) 
						{
							DijkstraPair gp = map.get(nbr);
							gp.psf = rp.psf + nbr;
							gp.cost = nc;

							heap.updatePriority(gp);
						}
					}
				}
			}
			return val;
		}
		
		private class Pair 
		{
			String vname;
			String psf;
			int min_dis;
			int min_time;
		}
		
		public String Get_Minimum_Distance(String src, String dst) 
		{
			int min = Integer.MAX_VALUE;
			
			String ans = "";
			HashMap<String, Boolean> processed = new HashMap<>();
			LinkedList<Pair> stack = new LinkedList<>();

			
			Pair sp = new Pair();
			sp.vname = src;
			sp.psf = src + "  ";
			sp.min_dis = 0;
			sp.min_time = 0;
			
			
			stack.addFirst(sp);

			
			while (!stack.isEmpty()) 
			{
				
				Pair rp = stack.removeFirst();

				if (processed.containsKey(rp.vname)) 
				{
					continue;
				}

				
				processed.put(rp.vname, true);
				
				
				if (rp.vname.equals(dst)) 
				{
					int temp = rp.min_dis;
					if(temp<min) {
						ans = rp.psf;
						min = temp;
					}
					continue;
				}

				Vertex rpvtx = vtces.get(rp.vname);
				ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

				for(String nbr : nbrs) 
				{
					
					if (!processed.containsKey(nbr)) {

						
						Pair np = new Pair();
						np.vname = nbr;
						np.psf = rp.psf + nbr + "  ";
						np.min_dis = rp.min_dis + rpvtx.nbrs.get(nbr); 
						
						stack.addFirst(np);
					}
				}
			}
			ans = ans + Integer.toString(min);
			return ans;
		}
		
		
		public String Get_Minimum_Time(String src, String dst) 
		{
			int min = Integer.MAX_VALUE;
			String ans = "";
			HashMap<String, Boolean> processed = new HashMap<>();
			LinkedList<Pair> stack = new LinkedList<>();

			
			Pair sp = new Pair();
			sp.vname = src;
			sp.psf = src + "  ";
			sp.min_dis = 0;
			sp.min_time = 0;
			
			
			stack.addFirst(sp);
			while (!stack.isEmpty()) {
				Pair rp = stack.removeFirst();

				if (processed.containsKey(rp.vname)) 
				{
					continue;
				}
				processed.put(rp.vname, true);
				if (rp.vname.equals(dst)) 
				{
					int temp = rp.min_time;
					if(temp<min) {
						ans = rp.psf;
						min = temp;
					}
					continue;
				}

				Vertex rpvtx = vtces.get(rp.vname);
				ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

				for (String nbr : nbrs) 
				{
					if (!processed.containsKey(nbr)) {
						Pair np = new Pair();
						np.vname = nbr;
						np.psf = rp.psf + nbr + "  ";
						np.min_time = rp.min_time + 120 + 40*rpvtx.nbrs.get(nbr); 
						stack.addFirst(np);
					}
				}
			}
			Double minutes = Math.ceil((double)min / 60);
			ans = ans + Double.toString(minutes);
			return ans;
		}
		
		public ArrayList<String> get_Interchanges(String str)
		{
			ArrayList<String> arr = new ArrayList<>();
			String res[] = str.split("  ");
			arr.add(res[0]);
			int count = 0;
			for(int i=1;i<res.length-1;i++)
			{
				int index = res[i].indexOf('~');
				String s = res[i].substring(index+1);
				
				if(s.length()==2)
				{
					String prev = res[i-1].substring(res[i-1].indexOf('~')+1);
					String next = res[i+1].substring(res[i+1].indexOf('~')+1);
					
					if(prev.equals(next)) 
					{
						arr.add(res[i]);
					}
					else
					{
						arr.add(res[i]+" ==> "+res[i+1]);
						i++;
						count++;
					}
				}
				else
				{
					arr.add(res[i]);
				}
			}
			arr.add(Integer.toString(count));
			arr.add(res[res.length-1]);
			return arr;
		}
		
		public static void Create_Metro_Map(Graph_M g)
		{
			g.addVertex("Uttara North~UN");
			g.addVertex("Uttara Center~UC");
			g.addVertex("Uttara South~US");
			g.addVertex("Pallabi~P");
			g.addVertex("Mirpur11~M11");
			g.addVertex("Mirpur10~M10");
			g.addVertex("Kazipara~KP");
			g.addVertex("Shewrapara~SP");
			g.addVertex("Agargaon~AG");
			g.addVertex("Bijoy Sarani~BS");
			g.addVertex("Farmgate~FG");
			g.addVertex("Karwan Bazar~KB");
			g.addVertex("Shahbag~SH");
			g.addVertex("Dhaka University~TSC");
			g.addVertex("Motijheel~MJ");
			
			g.addEdge("Uttara Center~UC", "Uttara North~UN", 3);
			g.addEdge("Uttara Center~UC", "Uttara South~US", 4);
			g.addEdge("Uttara Center~UC", "Pallabi~P", 5);
			g.addEdge("Uttara North~UN", "Pallabi~P", 3);
			g.addEdge("Uttara South~US", "Pallabi~P", 3);
			g.addEdge("Uttara North~UN", "Mirpur11~M11", 8);
			g.addEdge("Uttara South~US", "Kazipara~KP", 5);
			g.addEdge("Mirpur11~M11", "Mirpur10~M10", 2);
			g.addEdge("Mirpur10~M10", "Kazipara~KP", 4);
			g.addEdge("Mirpur11~M11", "Bijoy Sarani~BS", 9);
			g.addEdge("Kazipara~KP", "Shewrapara~SP", 7);
			g.addEdge("Mirpur10~M10", "Agargaon~AG", 7);
			g.addEdge("Bijoy Sarani~BS", "Agargaon~AG", 3);
			g.addEdge("Shewrapara~SP", "Agargaon~AG", 7);
			g.addEdge("Bijoy Sarani~BS", "Farmgate~FG", 4);
			g.addEdge("Farmgate~FG", "Karwan Bazar~KB", 1);
			g.addEdge("Agargaon~AG", "Karwan Bazar~KB", 7);
			g.addEdge("Karwan Bazar~KB", "Shahbag~SH", 4);
			g.addEdge("Shahbag~SH", "Dhaka University~TSC", 4);
                        g.addEdge("Dhaka University~TSC", "Motijheel~MJ", 12);
		}
		
		public static String[] printCodelist()
		{
			System.out.println("List of station along with their codes:\n");
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());
			int i=1,j=0,m=1;
			StringTokenizer stname;
			String temp="";
			String codes[] = new String[keys.size()];
			char c;
			for(String key : keys) 
			{
				stname = new StringTokenizer(key);
				codes[i-1] = "";
				j=0;
				while (stname.hasMoreTokens())
				{
				        temp = stname.nextToken();
				        c = temp.charAt(0);
				        while (c>47 && c<58)
				        {
				                codes[i-1]+= c;
				                j++;
				                c = temp.charAt(j);
				        }
				        if ((c<48 || c>57) && c<123)
				                codes[i-1]+= c;
				}
				if (codes[i-1].length() < 2)
					codes[i-1]+= Character.toUpperCase(temp.charAt(1));
				            
				System.out.print(i + ". " + key + "\t");
				if (key.length()<(22-m))
                    			System.out.print("\t");
				if (key.length()<(14-m))
                    			System.out.print("\t");
                    		if (key.length()<(6-m))
                    			System.out.print("\t");
                    		System.out.println(codes[i-1]);
				i++;
				if (i == (int)Math.pow(10,m))
				        m++;
			}
			return codes;
		}
		
		public static void main(String[] args) throws IOException
		{
			Graph_M g = new Graph_M();
			Create_Metro_Map(g);
			
			System.out.println("\n\t\t\t****WELCOME TO THE BD METRO MAP*****");
			BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
			
			while(true)
			{
				System.out.println("\t\t\t\t~~LIST OF Menu~~\n\n");
				System.out.println("1. LIST ALL THE STATIONS OF METRO MAP");
				System.out.println("2. SHOW THE METRO MAP");
				System.out.println("3. GET SHORTEST DISTANCE FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
				System.out.println("4. GET SHORTEST TIME TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
				System.out.println("5. EXIT THE MENU");
				System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 5) : ");
				int choice = -1;
				try {
					choice = Integer.parseInt(inp.readLine());
				} catch(Exception e) {
					
				}
				System.out.print("\n***********************************************************\n");
				if(choice == 7)
				{
					System.exit(0);
				}
				switch(choice)
				{
				case 1:
					g.display_Stations();
					break;
			
				case 2:
					g.display_Map();
					break;
				
				case 3:
					ArrayList<String> keys = new ArrayList<>(vtces.keySet());
					String codes[] = printCodelist();
					System.out.println("\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
					System.out.println("ENTER YOUR CHOICE:");
				        int ch = Integer.parseInt(inp.readLine());
					int j;
						
					String st1 = "", st2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
					if (ch == 1)
					{
					    st1 = keys.get(Integer.parseInt(inp.readLine())-1);
					    st2 = keys.get(Integer.parseInt(inp.readLine())-1);
					}
					else if (ch == 2)
					{
					    String a,b;
					    a = (inp.readLine()).toUpperCase();
					    for (j=0;j<keys.size();j++)
					       if (a.equals(codes[j]))
					           break;
					    st1 = keys.get(j);
					    b = (inp.readLine()).toUpperCase();
					    for (j=0;j<keys.size();j++)
					       if (b.equals(codes[j]))
					           break;
					    st2 = keys.get(j);
					}
					else if (ch == 3)
					{
					    st1 = inp.readLine();
					    st2 = inp.readLine();
					}
					else
					{
					    System.out.println("Invalid choice");
					    System.exit(0);
					}
				
					HashMap<String, Boolean> processed = new HashMap<>();
					if(!g.containsVertex(st1) || !g.containsVertex(st2) || !g.hasPath(st1, st2, processed))
						System.out.println("THE INPUTS ARE INVALID");
					else
					System.out.println("SHORTEST DISTANCE FROM "+st1+" TO "+st2+" IS "+g.dijkstra(st1, st2, false)+"KM\n");
					break;
				
				case 4:
					System.out.print("ENTER THE SOURCE STATION: ");
					String sat1 = inp.readLine();
					System.out.print("ENTER THE DESTINATION STATION: ");
					String sat2 = inp.readLine();
				
					HashMap<String, Boolean> processed1= new HashMap<>();				
					System.out.println("SHORTEST TIME FROM ("+sat1+") TO ("+sat2+") IS "+g.dijkstra(sat1, sat2, true)/60+" MINUTES\n\n");
					break;
				
					
               	         default:  
                    	        System.out.println("Please enter a valid option! ");
                        	    System.out.println("The options you can choose are from 1 to 5. ");
                            
				}
			}
			
		}	
	}
