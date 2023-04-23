/*
 * required imports
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
/*
 * ------------>DATA DEDUPLICATION, WINTER PROJECT 2017 UNDER PROFESSOR HARSHAN JAGDEESH<---------------
 */
/*
 *------------>content based hashing implementation<----------------
 * ----------->using a sliding window,
 * ----------->sliding gate and fixed chunk size
 * Encryption tool used is "MD5"
 * ||winter project 2017||
 * -------->Data Deduplication<-----------
 * */

/*
 * class content based chunking
 */
public class Content_Based {
   /*
    * main method
    */
	public static void main(String args[]){

	    /*
	     * Initializing the hahsmap which
	     * is going to store hashing value and chunk meta data
	     * This is done so as to remove similar chunks
	     * from the files
	     */
		HashMap<BigInteger,String> hashmap=new HashMap<BigInteger,String>();
		/*
		 * taking input from command line in the form of arguments
		 */
		String s;
		try{
		 s=args[0];
		}catch(Exception e){
			System.out.println("No arguement given so taking custom file as input");
		}
		/*
		 * or take custom file path
		 * Note:when giving custom path replace '\' with '\\'
		 * because '\' is an identifier which says the compiler
		 * to ignore everything after it and take it as a string
		 */
		s="D:\\Home";
		/*
		 * Just some UI favourable things
		 */
		System.out.println("Which method you would like to chose?\nFor variable gate chunking Enter 1 or for fixed chunking enter 2");
		Scanner sc=new Scanner(System.in);
		int choice=sc.nextInt();
		if(choice==1){
			 System.out.println("Going through variable chunking\n");
		}
		else{
			 System.out.println("Going through fixed chunking\n");
		}
		/*
		 * a utility function to measure
		 * the time taken in various algorithms
		 */
		long startTime=System.currentTimeMillis();
		/*
		 * getting all the files in the give directory
		 */
		Path main=Paths.get(s);
		/*
		 * trying to access the files
		 */
		 int numberOfCHunksConstructed=0;
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(main)){
			  /*
			   * if everything is okay
			   */
			  int num=0;
			  /*
			   * Initializing number of files in this given directory to be zero
			   * getting the files from their paths
			   */
			  for(Path path:stream){
				  /*
				   * temporary ArrayList to store the
				   * chunks of a file
				   */
				  ArrayList<String> chunks =new ArrayList<String>();
				  if(choice==1){
					  /*
					   * getting the total number of
					   * chunks using our chunking algorithm
					   */
					  /*
					   * going with variable size chunking
					   */
					  chunks=make_chunk(path.toFile());
				  }
				  if(choice==2){
					  /*
					   * getting the total number of
					   * chunks using our chunking algorithm
					   */
					  /*
					   * going with fixed path chunking with chunksize=48*4=192
					   */
					  chunks=fix_chunk(path.toFile(),1004);
				  }

				  /*
				   * incrementing the number of file counts
				   */
				  num++;
				  /*
				   * printing number of chunks for analysis
				   */
					numberOfCHunksConstructed+=chunks.size();
				  /*
				   * iterating over the current array of
				   * chunks
				   */
				  for(String str:chunks){
					  /*
					   * getting the decimal value from the hexadecimal value
					   * Using BigIntegr class because the hash value is
					   * 32 bits which is larger than usual Integer Limit
					   */
					  BigInteger x=new BigInteger(Hash(str),16);
					  /*
					   * putting this chunk and its hash
					   * in the HashMap
					   */
					  hashmap.put(x, str);
				  }
				  /*
				   * printing the net total stored chunks
				   */
				//  System.out.println("total chunks "+hashmap.size());
			  }
			  /*
			   * for convenience printing
			   * total number of chunks again
			   */
				System.out.println("total number of chunks constructed "+ numberOfCHunksConstructed);
			  System.out.println("total chunks stored finally are "+hashmap.size());
				System.out.println("the compression is "+100*((float)(numberOfCHunksConstructed-hashmap.size()))/numberOfCHunksConstructed);
				/*
			   * printing the total time up until now
			   */
			  long time=System.currentTimeMillis()-startTime;
			  System.out.println("total time in millis "+time);


		}
		/*
		 * catch the unhandled exception
		 */
		catch (IOException | DirectoryIteratorException x)
		{
			//oops!! directory not found
			System.err.println(x);
		}


	}
    /*
     * our string hash function
     * MD5 defined in another file
     */
	private static String Hash(String str) {
		/*
		 * returning the string value of hash
		 */
		return MD5.toHexString(MD5.computeMD5(str.getBytes()));
	}
    /*
     * ------>variable chunking algorithm<-----------
     * ------>sliding Gate algorithm<---------
     * Just an intuitive thought of us
     * the parameters are D and r
     * where D>r
     */
	/*
	 *??????????----what this algorithm does----??????????
	 * it starts with the first character calculate
	 * its hash(this value is an integer) using
	 * Rabin-Karp's algorithm
	 * checks whether this hash value modulo D
	 * gives r or not
	 */
	private static ArrayList<String> make_chunk(File file) throws FileNotFoundException {
		/*
		 * temporary array in RAM to store and return
		 * all the chunks
		 */
		ArrayList<String > arr=new ArrayList<String>();
		/*
		 * Initializing the value of D and r
		 * taken so as to maximize the efficiency
		 */
		int D=250;
		int r=235;
		/*
		 * printing these values
		 * just for convenience :)
		 */
		System.out.printf("Value of r is %d and that of D is %d \n",r,D);
		/*
		 * start of Rabin-Karp
		 */
        final int d=256;
        /*
         * reading the file
         */
        Scanner sc = new Scanner(file);
        String str="";
        while(sc.hasNextLine()){
        	str+=sc.nextLine();
        }
        /*
         * Initializing a large prime for modulo purpose
         * large primes tend to reduce collisions
         */
        int prime=148933;
        int l=str.length();
        int i;
        /*
         * t will store the hash value of the
         * text of a file
         */
        int t = 0;
        /*
         *  Calculate the hash value of text until it matches with desired result
         */
        /*
         * count is an important variable
         * its use will be shown soon in the upcoming code
         */
        int count=0;
        for (i = 0; i < l; i++)
        {
           /*
            * calculating the value of hash
            * this is similar to converting a text
            * in a decimal form with base 256
            */
            t = (d*t + str.charAt(i))%prime;
            /*
             * if hash%D is equal to r
             * then we are at the chunks boundary
             * We found our chunk
             */
            if(t%D==r){
            	/*
            	 * add this chunk to the chunk array
            	 */
            	arr.add(str.substring(count,i+1 ));
            	/*
            	 * make the hash value again to zero
            	 * because we are going to start the discovery
            	 * of a new chunk
            	 */
            	t=0;
            	/*
            	 * this count value helps us to add chunks to the array
            	 * other wise we would not know the chunk boundaries
            	 */
            	count=i+1;
            	continue;
            }
         }
		return arr;
	}
	/*
	 * ------->Fix chunking algorithm<----------
	 * All the chunks are of same size
	 */
	/*
	 * Note:here chunksize is not size
	 * its actually the number of character
	 * so in order to calculate size you have to multiply
	 * chunksize by 4 cause one string is 4 bytes
	 */
 private static ArrayList<String> fix_chunk(File file,int chunkSize) throws FileNotFoundException{
	 /*
	  * read the file
	  */
	Scanner sc= new Scanner(file);
	/*
	 * temporary array in RAM to store and return
	 * all the chunks
	 */
	ArrayList<String> arr= new ArrayList<String>();
	/*
     * reading the file
     */
	String s="";
	while(sc.hasNextLine()){
		s+=sc.nextLine();
	}
	/*
	 * adding the chunks to the array
	 */
	for(int i=0;i<s.length();i+=chunkSize){
		/*
		 * if we are not in the last chunks
		 */
		if(i+chunkSize<s.length()){
		arr.add(s.substring(i, i+chunkSize));
		}
		/*
		 * if its the last chunk
		 */
		else{
			arr.add(s.substring(i,s.length()));
		}
	}
	/*
	 * return this array
	 */
	return arr;

 }

}
