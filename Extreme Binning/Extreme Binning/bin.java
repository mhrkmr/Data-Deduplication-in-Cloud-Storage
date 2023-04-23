import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
class bin{
    BigInteger ChunkId;
    int chunkSize;
    String chunk;
    bin(BigInteger ChunkId, int chunkSize, String str){
        this.ChunkId=ChunkId;
        this.chunkSize=chunkSize;
        chunk=str;
    }
}
