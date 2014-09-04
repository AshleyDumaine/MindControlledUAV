import java.net.*;
import java.util.*;
import java.nio.*;


class Reset implements Runnable {
	InetAddress inet_addr;
    DatagramSocket socket;
	FloatBuffer fb;
    IntBuffer ib;
	
	public Reset() throws Exception {
		String ip = "192.168.1.1";
		
		StringTokenizer st = new StringTokenizer(ip, ".");
		
		byte[] ip_bytes = new byte[4];
		if (st.countTokens() == 4){
			for (int i = 0; i < 4; i++){
				ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
			}
		}
		else {
			System.out.println("Incorrect IP address format: " + ip);
			System.exit(-1);
		}
		
		System.out.println("IP: " + ip);  	
		
        ByteBuffer bb = ByteBuffer.allocate(4);
        fb = bb.asFloatBuffer();
        ib = bb.asIntBuffer();
		
        inet_addr = InetAddress.getByAddress(ip_bytes);
		socket = new DatagramSocket();
		socket.setSoTimeout(3000);
	}
	
	public void send(String at_cmd) throws Exception {
    	byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5556);
		socket.send(packet); 	
    }
	
	public void run() {
		while(true){
			String at_cmd = "AT*COMWDG=1";
			try{
				send(at_cmd);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			try{
				Thread.sleep(300);
			}
			catch(InterruptedException iex){
				iex.printStackTrace();
			}
		}
	}
}