
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.TooManyListenersException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;



public class GUI extends JFrame implements SerialPortEventListener{
	int depassement = 123;
	int min = 0;
	int max = 200;
	private SerialPort port;
	public GUI() throws PortInUseException, NoSuchPortException{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("COM2");
		this.port = (SerialPort) portIdentifier.open(this.getClass().getName(),2000);
		JPanel pan = new JPanel();
	    JFrame fenetre = new JFrame();
	    JLabel l = new JLabel("alerte:");
	    JTextField lmin = new JTextField("min: "+ min);
	    JTextField lmax = new JTextField("max: "+max);
	    this.setTitle("Ma première fenêtre Java");
	    this.setSize(400, 400);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	    JTextField txt = new JTextField("minimum");
	    lmax.setEditable(false);
	    JTextField txt2 = new JTextField("maximum");
	    lmin.setEditable(false);
	    this.getContentPane().setLayout(new GridLayout(10,10));
	    l.setBounds(50, 100, 20, 20);
	    
	    JButton button = new JButton("changer le minimum et maximum");
	    button.setBounds(100, 200, 50, 100);
	    button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String s = txt.getText();
				String m = txt2.getText();
				int mi = 0;
				int ma = 0;
				try {
					 mi = Integer.parseInt(s);
					 ma =  Integer.parseInt(m);
					 min = mi;
					 max = ma;
					 lmin.setText("min : "+min);
					 lmax.setText("max : "+max);
					 OutputStream out = port.getOutputStream();
					 BufferedWriter monBuffer = new BufferedWriter(new OutputStreamWriter(port.getOutputStream()));
					 out.write((byte) new Integer(min).byteValue());
					 out.flush();
					 System.out.println((byte) new Integer(min).byteValue());
	    			 out.write((char)max);
	    			 out.flush();
					 alerte(mi , ma);
				}catch(Error e1 ){
					System.out.println("veuillez indiquer un chiffre");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
	    	
	    });
	    
	    
	    this.getContentPane().add(l);
	    this.getContentPane().add(button);
	    this.getContentPane().add(txt);
	    this.getContentPane().add(txt2);
	    this.getContentPane().add(lmin);
	    this.getContentPane().add(lmax);
	    
	    this.setVisible(true);
	    
	    setup();
	    
	    
	}

public void setup() {
	try {
		this.port.addEventListener(this);
		this.port.notifyOnDataAvailable(true);
		this.port.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		
	}
	catch(TooManyListenersException e) {
		e.printStackTrace();
	}
	catch(UnsupportedCommOperationException e) {
		e.printStackTrace();
	}
}

public synchronized void serialEvent(SerialPortEvent event) {
	try {
		InputStream in = port.getInputStream();
		if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			byte[] buffer = new byte[8];
			int len = -1;
			String recu  = "";

			try {
				while ((len = in.read(buffer)) > 1) {
					String next=new String(buffer,0,len);
					recu += next.toString();
					System.out.println(next);
					
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				depassement = Integer.parseInt(recu);
				alerte(min,max);
			}catch(Error e1 ){
				System.out.println("veuillez indiquer un chiffre");
			}
			
		}
	}
	catch(IOException e) {
		e.printStackTrace();
	}
}
	
	
	
	public void alerte(int min,int max) {
		if(depassement>min && depassement<max) {
			Graphics g = this.getGraphics();
			g.setColor(Color.green);
			g.fillRect(80, 20 , 100, 50);
		}else {
			Graphics g = this.getGraphics();
			g.setColor(Color.red);
			g.fillRect(80, 20 , 100, 50);
		}
	} 
	public static void main(String[] args) throws PortInUseException, NoSuchPortException {
		GUI g = new GUI();
	}
	public void affichePort(String port) {
		
		JLabel p = new JLabel("port com utilisé : "+port);
		this.getContentPane().add(p);
	}

}
