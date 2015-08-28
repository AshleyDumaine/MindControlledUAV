import java.net.*;
import java.util.*;
import java.awt.*; 
import java.awt.event.*;
import java.nio.*;

//Main class for controlling the drone with the Emotiv EPOC and EmoKey
class Main extends Frame implements KeyListener {
	private static final long serialVersionUID = 1L;
	InetAddress inet_addr;
	DatagramSocket socket;
	int seq = 1; //Send AT command with sequence number 1 will reset the counter
	float speed = (float)0.8; //UAV movement speed
	boolean shift = false;
	FloatBuffer fb;
	IntBuffer ib;

	public Main(String name, String args[]) throws Exception {
		super(name);
		String ip = "192.168.1.1"; //IP address for the UAV WiFi connection AA
		if (args.length >= 1) {
			ip = args[0];
		}
		StringTokenizer st = new StringTokenizer(ip, ".");
		byte[] ip_bytes = new byte[4];
		if (st.countTokens() == 4) {
			for (int i = 0; i < 4; i++) {
				ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
			}	
		}
		else {
			System.out.println("Incorrect IP address format: " + ip);
			System.exit(-1);
		}
		System.out.println("IP: " + ip);
		System.out.println("Speed: " + speed);    	
		ByteBuffer bb = ByteBuffer.allocate(4);
		fb = bb.asFloatBuffer();
		ib = bb.asIntBuffer();
		inet_addr = InetAddress.getByAddress(ip_bytes);
		socket = new DatagramSocket();
		socket.setSoTimeout(3000);
		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"4000\""); //altitude max 2 meters
		if (args.length == 2) { //Command line mode
			send_at_cmd(args[1]);
			System.exit(0);
		}
		//Use keys from EmoKey to control the drone if different events are triggered
		addKeyListener(this); 
		setSize(320, 160);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Thread t = new Thread(new Reset());
		t.start();
	}

	public static void main(String args[]) throws Exception {
		new Main("ARDrone MAIN", args);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//changes key to its associated integer
		int keyCode = e.getKeyCode();
		System.out.println("Key: " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")");
		try {
			//Use specific key code as a command to the drone
			control(keyCode);
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	public int intOfFloat(float f) {
		fb.put(0, f);
		return ib.get(0);//CC
	}

	//Control AR.Drone via AT commands per key code
	public void control(int keyCode) throws Exception {
		String at_cmd = "";
		String action = "";
		switch (keyCode) {
		//Only max of 4 cognitive commands at a time recommended
		//Any changes to letter meaning/action here must occur in EmoKey mapping too
		case 'I':	//Up
			action = "Go Up (gaz+)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed) + ",0";
			break;
		case 'P':   //Forward
			action = "Go Forward (pitch+)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed) + ",0,0";
			break;
		case 'D':	//Down
			action = "Go Down (gaz-)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(-speed) + ",0";
			break;
		case 'U':   //Backward
			action = "Go Backward (pitch-)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed) + ",0,0";
			break;
		case 'T':	//turn left
			action = "Rotate Left (yaw-)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(-speed);
			break;
		case 'L':   //Left
			action = "Go Left (roll-)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed) + ",0,0,0";
			break;
		case 'C':	//turn right
			action = "Rotate Right (yaw+)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(speed);
			break;
		case 'R':   //Right
			action = "Go Right (roll+)";
			at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(speed) + ",0,0,0";
			break;
		case 'N':	//Hover
			//Not necessary but can be used to stabilize drone 
			action = "Hovering";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,0";
			break;
		case 'S':	//Takeoff
			action = "Takeoff";         
			at_cmd = "AT*REF=" + (seq++) + ",290718208";
			break;
		case 'A':	//Land
			action = "Landing";
			at_cmd = "AT*REF=" + (seq++) + ",290717696";
			break;
		case 'Z':	//reset 
			action = "Reset";
			at_cmd = "AT*REF=1,290717952";
			break;			
		default:
			break;
		}
		System.out.println("Speed: " + speed);    	
		System.out.println("Action: " + action);    	
		send_at_cmd(at_cmd);
	}
	
	public void send_at_cmd(String at_cmd) throws Exception {
		System.out.println("AT command: " + at_cmd);    	
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5556);
		socket.send(packet);
		//socket.receive(packet); //AR.Drone does not send back ack message (like "OK")
		//System.out.println(new String(packet.getData(),0,packet.getLength()));   	
	}
}