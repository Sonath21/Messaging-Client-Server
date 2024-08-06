import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Client {
    public static void main (String args[]) throws IOException { // arguments supply message and hostname of destination
        Socket socket = null;

        // int serverPort = 7896;
        socket = new Socket(args[0], Integer.parseInt(args[1]));// args[0] -> ip , args[1] -> port number
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        //String data = in.readUTF();
        out.writeUTF(Arrays.toString(args));
        String serverResp= in.readUTF();
        System.out.println(serverResp);
       /* if (args[2].equals("1")) {
            out.writeUTF(Arrays.toString(args));
            String serverResponse = in.readUTF();
            System.out.println(serverResponse);
        } else if (args[2].equals("2")) {
            out.writeUTF(Arrays.toString(args));
            String serverResponse = in.readUTF();
            System.out.println(serverResponse);
        } else if (args[2].equals("3")) {
            out.writeUTF(Arrays.toString(args));
            String serverResponse = in.readUTF();
            System.out.println(serverResponse);
        } else if (args[2].equals("4")) {
            out.writeUTF(Arrays.toString(args));
            String serverResponse = in.readUTF();
            System.out.println(serverResponse);
        } else if (args[2].equals("5")) {
            out.writeUTF(Arrays.toString(args));
            String serverResponse = in.readUTF();
            System.out.println(serverResponse);
        } else if (args[2].equals("6")) {
            out.writeUTF(Arrays.toString(args));
            String serverResponse = in.readUTF();
            System.out.println(serverResponse);
        }*/

        //System.out.println("Received: "+ data) ;
        socket.close();
    }
}