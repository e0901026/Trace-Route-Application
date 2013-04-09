package edu.vamk.netdatacapture;

import java.io.IOException;
import java.io.StringReader;

import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

public class CapUtil {

//	public static String ByteToBinary(byte[] bytes){
//		String retVal="";
//		
//		for ( byte z : bytes ) {
//            //if bit z of byte a is set, append a 1 to binaryCode. Otherwise, append a 0 to binaryCode
//            if ( ( z & ByteToCheck ) != 0 ) {
//                binaryCode += "1";
//            }
//            else {
//                binaryCode += "0";
//            }
//        } 
//		
//		return retVal;
//	}
//	
	
	
	public static String bytesToHex(byte[] bytes) {
		
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	public static String openFileToString(byte[] _bytes)
	{
	    String file_string = "";

	    for(int i = 0; i < _bytes.length; i++)
	    {
	        file_string += (char)_bytes[i];
	    }

	    return file_string;    
	}
	
	 public static String reverseDns(String hostIp) throws IOException {
         Resolver res = new ExtendedResolver();
         
         Name name = ReverseMap.fromAddress(hostIp);
         int type = Type.PTR;
         int dclass = DClass.IN;
         Record rec = Record.newRecord(name, type, dclass);
         Message query = Message.newQuery(rec);
         Message response = res.send(query);

         Record[] answers = response.getSectionArray(Section.ANSWER);
         if (answers.length == 0)
            return hostIp;
         else
            return answers[0].rdataToString();
   }

	
}
