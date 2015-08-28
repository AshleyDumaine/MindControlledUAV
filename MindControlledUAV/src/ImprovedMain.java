import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.nio.*;
import org.json.*;

class ImprovedMain {
	InetAddress inet_addr;
	DatagramSocket socket;
	int seq = 1; //Send AT command with sequence number 1 will reset the counter
	float speed = (float)0.8; //UAV movement speed
	boolean shift = false;
	FloatBuffer fb;
	IntBuffer ib;

	public ImprovedMain(String name, String args[]) throws Exception {
		super();
		String ip = "192.168.1.1"; //IP address for the UAV WiFi connection
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
		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"4000\""); //altitude max 4 meters?
		Thread t = new Thread(new Reset());
		t.start();
	}

	public static void main(String args[]) throws Exception {
		//run API_Main for the EPOC server socket
		Thread t = new Thread(new API_Main());
		t.start();
		ImprovedMain self = new ImprovedMain("ARDrone MAIN", args);
		double threshold = 0.5;
		String params;
		String JSONResponse;
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

		try {
			//connect to the EPOC server socket
			Socket clientSocket = new Socket("localhost", 4444);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

			//get user params and use that to control the drone
			System.out.println("Enter liftoff and land commands (separated by commas): ");
			params = inFromUser.readLine();
			System.out.println("Dare to use the X gyro? (y/n): ");
			String useXGyro = inFromUser.readLine();
			System.out.println("Dare to use the Y gyro? (y/n): ");
			String useYGyro = inFromUser.readLine();

			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			if ((useXGyro.equals("y")) || (useYGyro.equals("y"))) outToServer.writeBytes("Gyros, " + params + '\n');
			else outToServer.writeBytes(params + '\n'); 
			String[] tokens = params.split(", ");
			while ((JSONResponse = inFromServer.readLine()) == null) {
				System.out.println("waiting...");
			}
			while ((JSONResponse = inFromServer.readLine()) != null) {
				JSONObject obj = new JSONObject(JSONResponse);
				//System.out.println(obj); //debug
				if ((useXGyro.equals("y")) || (useYGyro.equals("y"))) {
					JSONArray gyros = obj.getJSONObject("EmoStateData").getJSONArray("Gyros");
					if (useXGyro.equals("y")) {
						//use the GyroX data
						double Xgyro_val = gyros.getJSONObject(0).getDouble("GyroX");
						System.out.println(Xgyro_val);
						if (Xgyro_val != 0) self.control("turn", Xgyro_val);
					}
					if (useYGyro.equals("y")) {
						//use the GyroY data
						double Ygyro_val = gyros.getJSONObject(1).getDouble("GyroY");
						System.out.println(Ygyro_val);
						if (Ygyro_val != 0) self.control("lift", Ygyro_val);
					}
				}
				for (String token : tokens) {
					//for expressiv and affectiv events, which are contained in JSONArrays
					if (API_Main.getAffectivMap().containsKey(token) || API_Main.getExpressivMap().containsKey(token)){
						JSONArray array = (API_Main.getAffectivMap().containsKey(token)) ? 
								obj.getJSONObject("EmoStateData").getJSONArray("Affectiv") : 
									obj.getJSONObject("EmoStateData").getJSONArray("Expressiv");
								for (int i = 0; i < array.length(); i++) {
									double param_val = array.getJSONObject(i).getDouble(token);
									if (param_val > threshold && token == tokens[0]) { //take off
										self.control("takeoff", 0);
									}
									else if (param_val > threshold && token == tokens[1]) { //land
										self.control("land", 0);
									}
								}
					}
					//for cognitiv events, which are contained in a JSONObject
					else if (API_Main.getCogntivMap().containsKey(token)) {
						String cog_action = obj.getJSONObject("EmoStateData").getString("Cognitiv");
						if (cog_action.equals(token)) {
							double param_val = obj.getJSONObject("EmoStateData").getDouble("Cognitiv");
							if (param_val > threshold && token == tokens[0]) { //take off
								self.control("takeoff", 0);
							}
							else if (param_val > threshold && token == tokens[1]) { //land
								self.control("land", 0);
							}
						}
					}
				}
			}
			//close all resources
			clientSocket.close();
			inFromUser.close();
			inFromServer.close();
			outToServer.close();
		}
		catch (SocketException e) {
			System.out.println("Could not start EPOC data server socket, aborting.");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public int intOfFloat(float f) {
		fb.put(0, f);
		return ib.get(0);
	}

	//Control AR.Drone via AT commands
	public void control(String command, double val) throws Exception {
		String at_cmd = "";
		String action = "";
		float cal_speed = 0.0f;
		if (command.equals("turn")) {
			action = (val < 0) ? "Rotate Left (yaw-)" : "Rotate Right (yaw+)";
			//lets try gyro magnitude to control speed, use 500 as the max and floor if any head jerks occur
			cal_speed = (float) ((Math.abs(val) > 500) ? 1.0 : (double) Math.round(val / 500.0 * 100) / 100); //will be negative for left
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(cal_speed);//-speed);
		}
		else if (command.equals("lift")) {
			action = (val < 0) ? "Go Down (gaz-)" : "Go Up (gaz+)";
			//lets try gyro magnitude to control speed, use 500 as the max and floor if any head jerks occur
			cal_speed = (float) ((Math.abs(val) > 500) ? 1.0 : (double) Math.round(val / 500.0 * 100) / 100); //will be negative for left
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(cal_speed) + ",0";//(double) Math.round((val / 500.0 * 100) / 100;
		}
		else if (command.equals("takeoff")) {
			action = "Takeoff";         
			at_cmd = "AT*REF=" + (seq++) + ",290718208";
		}
		else if (command.equals("land")) {
			action = "Landing";
			at_cmd = "AT*REF=" + (seq++) + ",290717696";
		}
		System.out.println("Speed: " + cal_speed);    	
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