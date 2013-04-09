import org.xbill.DNS.*;
import java.io.IOException;

    public class ReverseDnsTest {
       public static String reverseDns(String hostIp) throws IOException {
             Record opt = null;
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
       public static void main(String args[]) throws IOException {
             long now = System.currentTimeMillis();
             long after = System.currentTimeMillis();
       }
    }