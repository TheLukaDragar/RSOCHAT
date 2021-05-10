// Java program to create a blank text
// field of definite number of columns.
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
class Client extends JFrame implements ActionListener {
    // JTextField
    static JTextField t;
    static JTextField port;
    static JTextField userInput;
    static JTextField username;
 
    // JFrame
    static JFrame f;
 
    // JButton
    static JButton b;
    static JButton d;

    static JButton send;

    static JButton start;
    static JButton stop;
 
    // label to display text
    static JLabel l;

    private KnockKnockClient knockClient;

    private Socket kkSocket;

    private static DataInputStream in;
    private static DataOutputStream out;

    private static boolean connected=false;
 
    // default constructor
    Client()
    {
    }
 
    // main class
    public static void main(String[] args)
    {
        // create a new frame to store text field and button
        f = new JFrame("Client");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // create a label to display text
        l = new JLabel("");
 
        // create a new button
        b = new JButton("Povezi");

        d = new JButton("Prekini");

        send = new JButton("Send");

        //start = new JButton("Start");

        //stop = new JButton("Stop");
 
        // create a object of the text class
        Client te = new Client();
 
        // addActionListener to button
        b.addActionListener(te);
        d.addActionListener(te);

        send.addActionListener(te);
 
        // create a object of JTextField with 16 columns
        t = new JTextField(16);
        port = new JTextField(8);

        username = new JTextField(8);
 
        // create a panel to add buttons and textfield
        JPanel p = new JPanel();

        JTextArea output = new JTextArea(10,20);
            PrintStream out = new PrintStream(new OutputStream() {
            @Override
                public void write(int b) throws IOException {
                    output.append(""+(char)(b & 0xFF));
                }
            });
            System.setOut(out);
            System.out.println("Vnesi hostname in port nato klikni Povezi");
 
        // add buttons and textfield to panel

        //p.add(start);
        //p.add(stop);

        username.setText("Tinea");

        userInput = new JTextField(16);
        
        p.add(t);
        p.add(port);
        p.add(b);
        p.add(d);
        p.add(l);
        p.add(output);
        p.add(username);

        p.add(userInput);
        p.add(send);

       

      
        // add panel to frame
        f.add(p);
 
        // set the size of frame
        f.setSize(300, 300);
 
        f.setVisible(true);

       

       

       
    }
 
    // if the button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();

        if (s.equals("Prekini") && connected) {
           
            try {
                kkSocket.close();
            
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            l.setText("povezava prekinjena");
            connected=false;

        }
        if (s.equals("Povezi") && !connected) {


           
            if(!t.getText().isEmpty() && !port.getText().isEmpty()){

                l.setText("povezujem" );
                System.out.println("Connecting to "+t.getText()+ ":"+ port.getText() );

            
                try {
                    kkSocket = new Socket(t.getText(), Integer.parseInt(port.getText()));

                
                   
                    connected=true;

                    in = new DataInputStream(kkSocket.getInputStream()); // create input stream for listening for incoming messages
                    out = new DataOutputStream(kkSocket.getOutputStream()); 
			        //out = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages

                    ChatClientMessageReceiver message_receiver = new ChatClientMessageReceiver(in); // create a separate thread for listening to messages from the chat server
			        message_receiver.start(); // run the new thread

                    
                    l.setText("povezano" );
                    



                } catch (NumberFormatException | IOException e1) {
                    l.setText("Napaka" );
                    System.out.println("Failed to connect to "+t.getText()+ ":"+ port.getText() );
                   
                }


                    

            }


        }
        if (s.equals("Send") && connected) {

            try {
			
			
               
                out.writeUTF(username.getText()+":"+userInput.getText()); // send the message to the chat server
                out.flush(); // ensure the message has been sent
                System.out.println("Sending: "+userInput.getText());
                userInput.setText("");
            } catch (IOException eee) {
                System.err.println("could not send message");
                eee.printStackTrace(System.err);
            }

           
        }
    }
}

class ChatClientMessageReceiver extends Thread {
	private DataInputStream in;

	public ChatClientMessageReceiver(DataInputStream in) {
		this.in = in;
	}

	public void run() {
    
		try {
			String message;
			while ((message = this.in.readUTF()) != null) { // read new message

                System.out.println("Recieved: "+ message);
			

			}
		}
		catch(EOFException e){
			System.err.println("Server closed connection");
			//System.exit(1);
            this.stop();

		}
		
		catch (Exception e) {
			System.err.println("[system] could not read message");
            this.stop();
			//e.printStackTrace(System.err);
			//System.exit(1);
		}
	}

}