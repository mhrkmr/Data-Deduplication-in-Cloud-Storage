import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
/**
 * @MihirKumar
 * This is a function used to print a array list of the given class type
 * to a file with a given path
 * @version 1.0
 * @winterProject
 * All rights reserved
 * copyright 2017
 */
public class ChunkWriter {
    /*
     * This method writes the arraylist to maintain a meta server data base.
     * for using this check the arguments and apply
     * P.S. Please implement the constructor in the bin class
     */
    public static void Chunkprinter(HashMap<BigInteger, String> mp, String fileAddress, HashMap<String, Integer> counter){
       try{
         int count=0;
        Writer output=null;
        File file= new File(fileAddress);
        output = new BufferedWriter(new FileWriter(file));
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            count++;
            Map.Entry pair = (Map.Entry)it.next();
            output.write(pair.getKey()+"\n");
            output.write(pair.getValue()+"\n");
            it.remove(); // avoids a ConcurrentModificationException
         }
         if(counter.get(fileAddress)==null) counter.put(fileAddress, count);
         else{
            if(count!=counter.get(fileAddress)){
              counter.remove(fileAddress);
              counter.put(fileAddress, count);
            }
         }
      output.close();
      //System.out.println("File has been written");

       }catch(IOException e){
           System.err.println("errroorrrrrrrrrrrrrrrrr");
       }

    }
    public static void Chunkprinter(HashMap<BigInteger, String> mp, String address, String mainFileName, HashMap<String, Integer> counter){
       try{
        Writer output=null;
        int count=0;
        String fileAddress=address+"\\"+mainFileName;
        File file= new File(fileAddress);
        output = new BufferedWriter(new FileWriter(file));
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            count++;
            Map.Entry pair = (Map.Entry)it.next();
            output.write(pair.getKey()+"\n");
            output.write(pair.getValue()+"\n");
            it.remove(); // avoids a ConcurrentModificationException
         }
         if(counter.get(fileAddress)==null) counter.put(fileAddress, count);
         else{
            if(count!=counter.get(fileAddress)){
              counter.remove(fileAddress);
              counter.put(fileAddress, count);
            }
         }
      output.close();
      //System.out.println("File has been written");

       }catch(IOException e){
           System.err.println("errroorrrrrrrrrrrrrrrrr");
       }
    }
}
