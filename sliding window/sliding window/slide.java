import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/*
 * content based hashing implementation
 * using a sliding window approach
 * encryption tool used is MD5
 * winter project 2017
 * Data Deduplication
 * */

public class slide {

	public static void main(String args[]){

		//HashMap<BigInteger,String> hm=new HashMap<BigInteger,String>();
		String s="D:\\Home";   // directory where the files are stored
		Path main=Paths.get(s);
			int num=0,j=0;
			HashMap<BigInteger,String> hm=new HashMap<BigInteger,String>();
			UndirectedGraph<String> graph=new UndirectedGraph<>();
			try(DirectoryStream<Path> stream = Files.newDirectoryStream(main)){

					long time= System.currentTimeMillis();
							for(Path path:stream)
							{
								graph.addVertex(path.toString());                    // addition in the graph
								ArrayList<String> chunks=make_chunk(path.toFile());
								num+=chunks.size();
								j++;
								for(String str:chunks)
								{
									BigInteger x=new BigInteger(Hash(str),16);
									graph.addEdge(path.toString(), String.valueOf(x), 0);
									hm.put(x, str);
								}

							}
							System.out.println("total chunks "+hm.size());
							System.out.println("chunks without dedup "+num);
							System.out.println("compression= "+( ((float)num-hm.size())*100/(num)));
							System.out.println("Time: "+(System.currentTimeMillis()-time)+"millis");


			}
			catch (IOException | DirectoryIteratorException x)
			{
				//oops!! directory not found
				System.err.println(x);
			}
			System.out.println();



	}

	private static String Hash(String str) {

		return MD5.toHexString(MD5.computeMD5(str.getBytes()));
	}

	private static ArrayList<String> make_chunk(File file) throws FileNotFoundException {
		ArrayList<String > arr=new ArrayList<String>();
		//InputStream in = new FileInputStream(fileName);

		//String S=new String(readByteBlock(in, 0, minChunkSize));
		//@SuppressWarnings("unused")
		//Random rand= new Random();
		int D=250;
		int r=235;
		int n=12;
        final int d=256;
		Scanner sc = new Scanner(file);
        String str="";
        while(sc.hasNextLine())
        {
        	str+=sc.nextLine();
        }
        int prime=148933;
        int l=str.length();
		int i,j=0;
        int t = 0; // hash value for text
        int h = 1;
        for (i = 0; i < n; i++){
            h = (h*d)%prime;
        }
        // Calculate the hash value of text and first
        // window of text
        int count=0;
        while(count<l)
        {
        	t=0;
        	//read a window;
        	for (i = count; i < n+count; i++)
            {
        		if(i>=l)
        			break;
                t = (d*t + str.charAt(i))%prime;
            }

        	if(t%D==r||i>=l)
        	{
        		arr.add(str.substring(count, i));
        		count=i;
        		continue;
        	}
        	j=i;
        	while(true)
        	{
        		t = (d*t + str.charAt(j) - (d*h*str.charAt(j-n)))%prime;
        		if(t<0)
        			t=t+prime;
        		j++;
        		//move window
        		if (t%D==r||j==l)
        		{
        			arr.add(str.substring(count, j));
        			count=j;
        			break;
        		}
        	}
        	//System.out.println("yo");
        }
    //    System.out.println("avgchunksize "+ (l/arr.size()));

        sc.close();
		return arr;
	}

	private static int Pow(int d, int i) {
		int p=1;
		for(int j=0;j<i;j++)
		{
			p=p*d;
		}
		return p;
	}


}
