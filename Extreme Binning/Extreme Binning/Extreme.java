/**
 * @MihirKumar
 * This is a function used to print a array list of the given class type
 * to a file with a given path
 * @version 1.0
 * @winterProject
 * All rights reserved
 * copyright 2017
 */
import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
/*
* before starting ensure that the file that are meant for deduplication are in the a folder Home in the Local Disk
* ensure that the E drive has the 2 folders FileDatabaseLoc and ChunkLoc.
* if these folders are not available, create the folders in the local drive and change the addresses of the home and the above givenn
* 2 folders in the following lines 123,124,125
*/

public class Extreme
{
    public static HashMap<BigInteger, String> DiskLoader(String FILENAME)
    {
          BufferedReader br = null;
          FileReader fr = null;
          HashMap<BigInteger, String> mp= new HashMap<>();
          try{
              fr = new FileReader(FILENAME);
	      br = new BufferedReader(fr);
              String sCurrentLine;
	while ((sCurrentLine = br.readLine()) != null){
          mp.put(new BigInteger(sCurrentLine), br.readLine());
        }
          }catch(Exception e){

          }
          return mp;

    }

        public static int searchByID(ArrayList<Primary> list, BigInteger id){

            for(int i=0;i<list.size();i++){
                if(id.compareTo(list.get(i).fileHash)==0) return i;
            }
            return -1;
        }
	private static String Hash(String str)
	{
		return MD5.toHexString(MD5.computeMD5(str.getBytes()));
	}
	private static ArrayList<String> make_chunk(File file) throws FileNotFoundException
	{

		ArrayList<String > arr=new ArrayList<String>();

		int D=300;
		int r=240;
		final int d=256;
		int n=14;
		int prime=148933;
		int t = 0; // hash value for text

		int h = 1;
		for (int i = 0; i < n; i++)
		{
			h = (h*d)%prime;
		}

		Scanner sc = new Scanner(file);
		String str="";
		while(sc.hasNextLine())
		{
			str+=sc.nextLine();
		}

		int l=str.length();
		int i,j=0;

		int count=0;

		while(count<l)
		{
			t=0;

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
				if (t%D==r||j==l)
				{
					arr.add(str.substring(count, j));
					count=j;
					break;
				}
			}
		}
		//System.out.println("avgchunksize "+ (l/arr.size()));
		sc.close();
		return arr;
	}

	public static void main(String args[])
	{
		String s="D:\\Home";
    final String FileDatabaseLocation= "E:\\FileDatabaseLoc";
    final String ChunkDatabaseLocation="E:\\ChunkLoc";
		Path main=Paths.get(s);
    HashMap<String, Integer> chunkQuantityMap= new HashMap<>();
		int num=0;
		ArrayList<Primary> dataStorage=new ArrayList<>();

		try(DirectoryStream<Path> stream = Files.newDirectoryStream(main))
		{
			for(Path path:stream)
			{
                            HashMap<BigInteger, String> mp = new HashMap<>();
                            /*
                             * this hash map is used to create the uniqueness in the chunks of the files
                             * and for this i will insert the hash value as key and the string value as the
                             * value in this map.
                             */
				Primary f=new Primary();
				ArrayList<String> chunks=make_chunk(path.toFile());

				Scanner sc = new Scanner(path.toFile());
				String strc="";
				while(sc.hasNextLine())
				{
					strc+=sc.nextLine();
				}
				f.fileHash=new BigInteger(Hash(strc),16);
				sc.close();
				f.repId=new BigInteger(Hash(chunks.get(0)),16);
				f.Pointer=new ArrayList<>();
				bin b;
				for(String str:chunks)
				{
					num++;
					BigInteger x=new BigInteger(Hash(str),16);
                                        if(mp.get(x)==null) mp.put(x, str);
					if(f.repId.compareTo(x)>0)
						f.repId=x;
					b= new bin(x, str.length(), str);
					f.Pointer.add(b);
				}
                               String mainFileName= String.valueOf(f.repId)+".txt";
                               int index=searchByID(dataStorage, f.repId);
                                        /*
                                        * note that the following lines tell the location of the minimum chunk present in the table. index==-1
                                        * means not present, create the new bin(write a new file in chunk data base as well as in the filemetaserver database)
                                        *  otherwise means that the minimum chunk(representative chunk id is present in the) table
                                        * stored in the memory. Now my target is to check the fileHash. If the fileHash is also same then it
                                        * means that the complete file is same and you don't need to create a deduplication and also you dont need to attach
                                        * this primary object in the table.
                                        * else if the file is not same, then you need to load the table stored in the hard disk,
                                        * and then construct a hash map with the data, then finally update the chunk ;-;
                                        */
                               if(index==-1){
                               DatabaseWriter.FileDatabaseWriter(f.Pointer, FileDatabaseLocation, mainFileName);
                               f.FileMetaServerAddress=FileDatabaseLocation+"\\"+mainFileName;
                               ChunkWriter.Chunkprinter(mp, ChunkDatabaseLocation, mainFileName, chunkQuantityMap);
                               f.ChunkDataServerAddress=ChunkDatabaseLocation+"\\"+mainFileName;
                               mp=null;
                               dataStorage.add(f);
                               }else{
                                   if(dataStorage.get(index).fileHash.compareTo(f.fileHash)==0){
                                      /*
                                       * this means that complete file is present in the database already. so just delete the local variables and continue the loop.
                                       */
																			// System.out.println("aya hia");
                                       f=null;
                                       continue;
                                   }else{
                                       // the address where the complete chunks are stored in the hard disk can be obtained by chunkDataServerAddress of index
                                       String ChunkDataServerAddress=dataStorage.get(index).ChunkDataServerAddress;
                                       //now we need to read the file at this index. for this purpose we will do the following
                                       HashMap<BigInteger, String> RetrievalMap=DiskLoader(ChunkDataServerAddress);
                                       Iterator it= mp.entrySet().iterator();
                                       while(it.hasNext()){
                                           Map.Entry pair= (Map.Entry)it.next();
                                           if(RetrievalMap.get(pair.getKey())==null) RetrievalMap.put((BigInteger)pair.getKey(), (String)pair.getValue());
                                       }
                                       DatabaseWriter.FileDatabaseWriter(f.Pointer, FileDatabaseLocation, mainFileName);
                                       ChunkWriter.Chunkprinter(RetrievalMap,ChunkDataServerAddress, chunkQuantityMap);
                                       f=null;
                                   }
                               }

                               /*
                                * now the step is to create a recovery meta data corresponding to each file
                                * and for this ill calll the writer function in the databasewriter and then ill write the obj f;
                                */


			}
      Iterator iterator = chunkQuantityMap.entrySet().iterator();
      Integer dedup=0;
      while(iterator.hasNext()){
        Map.Entry pair = (Map.Entry)iterator.next();
        dedup+=(Integer)pair.getValue();
      }
			System.out.println("chunks without dedup "+num);
      System.out.println("chunks with dedup "+dedup);
		}
		catch (Exception x)
		{

			System.err.println(x.getStackTrace());
		}


	}

}
