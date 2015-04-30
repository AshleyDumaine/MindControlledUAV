/*import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

class NavData extends Thread { 
    DatagramSocket socket_nav;
    InetAddress inet_addr;
    ARDrone ardrone;

    public NavData(ARDrone ardrone, InetAddress inet_addr) throws Exception {
        this.ardrone = ardrone;
    this.inet_addr = inet_addr;

    socket_nav = new DatagramSocket(ARDrone.NAVDATA_PORT);
    socket_nav.setSoTimeout(3000);
    }

    public void run() {
        int cnt = 0;

    try {
        byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
        DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, ARDrone.NAVDATA_PORT);
        socket_nav.send(packet_snd);
            System.out.println("Sent trigger flag to UDP port " + ARDrone.NAVDATA_PORT);        

        ardrone.send_at_cmd("AT*CONFIG=" + ardrone.get_seq() + ",\"general:navdata_demo\",\"TRUE\"");

         byte[] buf_rcv = new byte[10240];
        DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);            

        while(true) {
        try {
            socket_nav.receive(packet_rcv);

            cnt++;
            if (cnt >= 5) {
                cnt = 0;
                System.out.println("NavData Received: " + packet_rcv.getLength() + " bytes"); 
                //System.out.println(ARDrone.byte2hex(buf_rcv, 0, packet_rcv.getLength()));
                System.out.println("Battery: " + ARDrone.get_int(buf_rcv, ARDrone.NAVDATA_BATTERY)
                        + "%, Altitude: " + ((float)ARDrone.get_int(buf_rcv, ARDrone.NAVDATA_ALTITUDE)/1000) + "m");
            }
        } catch(SocketTimeoutException ex3) {
                System.out.println("socket_nav.receive(): Timeout");
        } catch(Exception ex1) { 
            ex1.printStackTrace(); 
        }
        }
    } catch(Exception ex2) {
        ex2.printStackTrace(); 
    }
    }
}

class Video extends Thread { 
    DatagramSocket socket_video;
    InetAddress inet_addr;
    ARDrone ardrone;

    public Video(ARDrone ardrone, InetAddress inet_addr) throws Exception {
        this.ardrone = ardrone;
    this.inet_addr = inet_addr;

    socket_video = new DatagramSocket(ARDrone.VIDEO_PORT);
    socket_video.setSoTimeout(3000);
    }

    public void run() { 
    try {
        byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
        DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, ARDrone.VIDEO_PORT);
        socket_video.send(packet_snd);
            System.out.println("Sent trigger flag to UDP port " + ARDrone.VIDEO_PORT);        

        ardrone.send_at_cmd("AT*CONFIG=" + ardrone.get_seq() + ",\"general:video_enable\",\"TRUE\"");

         byte[] buf_rcv = new byte[64000];
        DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);           

        while(true) {
        try {
            socket_video.receive(packet_rcv);
            System.out.println("Video Received: " + packet_rcv.getLength() + " bytes"); 
            //System.out.println(ARDrone.byte2hex(buf_rcv, 0, packet_rcv.getLength()));  
        } catch(SocketTimeoutException ex3) {
                System.out.println("socket_video.receive(): Timeout");
                socket_video.send(packet_snd);
        } catch(Exception ex1) { 
            ex1.printStackTrace(); 
        }
        }
    } catch(Exception ex2) {
        ex2.printStackTrace(); 
    }
    }
}

class ARDrone extends Frame implements KeyListener {
    /**
	 * 
	 */
	/*private static final long serialVersionUID = 1L;
	static final int NAVDATA_PORT = 5554;
    static final int VIDEO_PORT   = 5555;
    static final int AT_PORT       = 5556;

    //NavData offset
    static final int NAVDATA_STATE    =  4;
    static final int NAVDATA_BATTERY  = 24;
    static final int NAVDATA_ALTITUDE = 40;

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}*/