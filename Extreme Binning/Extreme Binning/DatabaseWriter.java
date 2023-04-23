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
public class DatabaseWriter {
    /*
     * This method writes the arraylist to maintain a meta server data base.
     * for using this check the arguments and apply
     * P.S. Please implement the constructor in the bin class
     */
    public static void FileDatabaseWriter(ArrayList<bin> chunkArrayList, String address, String mainFileName){
       try{
        Writer output=null;
        File file= new File(address+"\\"+mainFileName);
        output = new BufferedWriter(new FileWriter(file));
        Iterator<bin> it= chunkArrayList.iterator();
        while(it.hasNext())
        {
            bin obj= it.next();
            String toBePrinted= String.valueOf(obj.ChunkId);
            output.write(toBePrinted+"\n");
        }
      output.close();
      System.out.println("File has been written");

       }catch(IOException e){
           System.err.println("errroorrrrrrrrrrrrrrrrr");
       }
    }
}
