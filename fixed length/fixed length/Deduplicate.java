import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Random;
import java.nio.file.*;
public class Deduplicate
{
	private static byte[] readByteBlock(InputStream in, int offset, int noBytes) throws IOException 
	{
		byte[] result = new byte[noBytes];
		in.read(result, offset, noBytes);
		return result;
	}
	
	public static String[] ChunkIt(File fileName) throws Exception
	{
		
		int chunkSize=192;
		InputStream in = new FileInputStream(fileName);
		long noOfBlocks = (long) Math.ceil((double)fileName.length() / chunkSize);
		String[] result = new String[(int)noOfBlocks];
		int offset = 0;
		for(int i = 0; i < result.length; i++) {
			result[i] = new String(readByteBlock(in, offset, chunkSize));
		}
		return result;
	}
	public static String Hash(String s)
	{
		return MD5.toHexString(MD5.computeMD5(s.getBytes()));
	}
	
	public static void main(String args[])
	{
		try 
		{
			HashMap<BigInteger,String> hash=new HashMap<BigInteger, String>();
			UndirectedGraph<String> graph=new UndirectedGraph<>();
			Path dir = Paths.get("D:\\Home");
			int numFiles=0;
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) 
			{
				int k=0;
				for (Path file: stream) 
				{
					String[] arr=ChunkIt(file.toFile());
					graph.addVertex(file.toString());
					for(int j=0;j<arr.length;j++)
					{ 
						k++;
						BigInteger x=new BigInteger(Hash(arr[j]),16);
						hash.put(x,arr[j]);
						graph.addVertex(x.toString(16));
						graph.addEdge(file.toString(), x.toString(16), 0);
					}					
				}
				int z=hash.size();
				HashSet<Integer> abc=new HashSet<>();
				System.out.println("initial num of chunk "+k);
				System.out.println("total chunks "+z);
				System.out.println("num of chunk saved "+(k-z));
				System.out.println("percent compression "+(((float)k-z)*100/k)+"%");
				//graph.printGraph();
			}
			catch (IOException | DirectoryIteratorException x) 
			{
				System.err.println(x);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}