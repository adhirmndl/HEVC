package hevc_depack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class make_frame {
	public static String i2s(int i) {
		return Integer.toString(i);
	}
	public static byte[] copy(byte[] data,int pos) {
		byte r[] = new byte[data.length-pos];
		int l=0;int rc=0;
		for (byte i:data) {
			if(l++<pos) continue;
			r[rc++]=i;			
		}
		return r;
	}
	public static String c2s(char i) {
		return i+"";
	}
	public static void print(Object ...o) {
		for(Object i:o) {
			System.out.print(i);
		}
		System.out.println("");
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	
		
	int openfile = 0;
	while(openfile<1) {
		String loc = "/Users/Delta_Force/ivis/HEVC/h265_packet/";
		InputStream is;
		for(int i=0;i<7143;i++) {
			 is= new FileInputStream(loc+Integer.toString(i)+".bin");
			 long fileSize = new File(loc+Integer.toString(i)+".bin").length();
			 byte st[] = new byte[(int) fileSize];
			 is.read(st);
			 print("File Open: "+i2s(i)+"  >>--------->");
			 //String StartByte = "\x00\x00\x00\x01";
			 int v = (st[0]&192)>>6;
			Boolean p =  (st[0]&32)>0;
			Boolean x = (st[0]&16)>0;
			int cc =(st[0]&15);
			Boolean m = (st[1]&128)>0;
			int pt = (st[1]&127);
			int seq = (st[2]<<8|st[3])&0b1111111111111111;
			int ts =  (st[4]<<24|st[5]<<16|st[6]<<8|st[7])&0b11111111111111111111111111111111;
			int ssrc =  (st[8]<<24|st[9]<<16|st[10]<<8|st[11])&0b11111111111111111111111111111111;
			/*
			print("v = ",v);
			print("X = ",x);
			print("p = ",p);
			print("cc = ",cc);
			print("m = ",m);
			print("pt = ",pt);
			print("seq = ",seq);
			print ("TS = ",ts);
			print ("SSRC = ",ssrc);*/
			byte[] csrc = new byte[(byte) cc];
			int bp=96;int byp = 96/8;
			for(int i_ =0;i_<cc;i_++,byp++,bp+=8) {
				csrc[i_] =st[byp]; 
			}
			//process payload header
			Boolean F = st[byp]>>7>0;
			int payload_type = (st[byp]&0b01111110)>>1;
			int LayerId =((st[byp]&0b00000001)<<5)|(st[byp+1]&0b11111000);
			int tid = st[byp+1]&0b00000111;
			byp+=2;
			String pps = "RAHA8vA8kA==";
			/*
			 * +---------------+---------------+
             * |0|1|2|3|4|5|6|7|0|1|2|3|4|5|6|7|
             * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
             * |F|   Type    |  LayerId  | TID |
             * +-------------+-----------------+
			 */
			switch(payload_type) {
			case 32:
				/*continue;
				print ("type vps");
				print(byp," -> ",st.length);
				try (FileOutputStream fos = new FileOutputStream("/Users/Delta_Force/ivis/HEVC/dec265/frame0.bin",true)) {
				  		//byte fa[]=copy(st,byp+1);
					byte fa[]=copy(st,byp);
					   fos.write(fa);
					}*/
				continue;
			case 33:/*
					print ("type  sps");
					print(byp," -> ",st.length);
					if(st.length-byp<6) continue;
					try (FileOutputStream fos = new FileOutputStream("/Users/Delta_Force/ivis/HEVC/dec265/frame0.bin",true)) {
						//byte fa[]=copy(st,byp+1);
						byte fa[]=copy(st,byp);	
						   fos.write(fa);
						}*/
					continue;
			case 34:
				print ("Type  PPS");
				print(byp," -> ",st.length);
				if(st.length-byp<6) continue;
				try (FileOutputStream fos = new FileOutputStream("/Users/Delta_Force/ivis/HEVC/dec265/frame0.bin",true)) {
					byte fa[]=copy(st,byp+1);
					//byte fa[]=copy(st,byp);
					   fos.write(fa);
					}
				continue;
			case 48:
				// Aggregation Packet (AP)
				print("Found  Aggregation Packet (AP)");
			case 49:
				print("49 found");
				if (st.length-byp<3)continue;
				Boolean startBit = (st[byp]&0b10000000)>0;
				Boolean endBit = (st[byp]&0b01000000)>0;
				if(startBit) {
					print ("start bit found");
					int nal_unit_type =  (st[byp]&0b00111111);
					byte head[]=new byte[6];
					head[0] = 0b00000000;
					head[1] = 0b00000000;
					head[2] = 0b00000000;
					head[3] = 0b00000001;
					head[4] = (byte)((st[byp-2]&0b10000001)|((st[byp]&0b00111111)<<1));
					head[5] = st[byp-1];
					try (FileOutputStream fos = new FileOutputStream("/Users/Delta_Force/ivis/HEVC/dec265/frame0.bin",true)) {
						   fos.write(head);	
						   byte fa[]=copy(st,byp);	//byp, er bakira byp+1				   
						   fos.write(fa);
						}
		
				}
				else {
					try (FileOutputStream fos = new FileOutputStream("/Users/Delta_Force/ivis/HEVC/dec265/frame0.bin",true)) {
						  	
						byte fa[]=copy(st,byp);					   
						   fos.write(fa);
						}
					
				}
			
				
				
				
				/*
   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  | PayloadHdr (Type=49)          | FU header     | DONL (cond)   |
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-|
  | DONL (cond)   |												|
  |-+-+-+-+-+-+-+-+		     									|
  |							FU Payload							|
  |                               +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |                               :...OPTIONAL RTP padding        |
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
*/
			}
		}
		openfile+=1;

		}
	}

}
