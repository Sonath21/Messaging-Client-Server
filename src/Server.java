import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

   /* public static class Connection extends Thread {
        DataInputStream in;
        DataOutputStream out;
        Socket clientSocket;
        public Connection (Socket aClientSocket) {
            try { clientSocket = aClientSocket;
                in = new DataInputStream( clientSocket.getInputStream());
                out =new DataOutputStream( clientSocket.getOutputStream());
                this.start();
            } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
        }

        @Override
        public void run(){
            try { // an echo server
                String data = in.readUTF();
                out.writeUTF(data);
            } catch(EOFException e) {System.out.println("EOF:"+e.getMessage());
            } catch(IOException e) {System.out.println("IO:"+e.getMessage());}
            finally{ try {clientSocket.close();}catch (IOException e){/*close failed}}
        }
    }*/

    public static class Message {
        boolean isRead;
        String sender;
        String receiver;
        String body;
        int messageID;

        public Message(String sender,String receiver,String body, int messageID){
            this.sender = sender;
            this.receiver = receiver;
            this.body = body;
            this.isRead = false;
            this.messageID = messageID;

        }
    }

    public static class Account {


        String username;
        int authToken;
        List<Message> messageBox;

        public Account(String username, int authToken)
        {
            this.username = username;
            this.authToken=authToken;
            messageBox = new ArrayList<Message>();

        }
    }



    ArrayList<Account> accounts;
    int randAuthTokens;
    ArrayList<Integer> randmessageIDs;
    Account newAcc;

    public Server()
    {
        accounts =  new ArrayList<>();
        randAuthTokens =0;
        randmessageIDs = new ArrayList<>();
    }

    /**
     Εμφανίζει μια λίστα λογαριασμών με τα αντίστοιχα ονόματα χρηστών. Η μέθοδος αναζητά το authToken μεταξύ των λογαριασμών και αν βρεθεί, τα αντίστοιχα ονόματα χρηστών γράφονται στο DataOutputStream.
     Εάν το authToken δεν βρεθεί, η μέθοδος γράφει "Invalid Auth Token" στο DataOutputStream.
     @param authToken ένα token που χρησιμοποιείται για την αυθεντικοποίηση της αίτησης.
     @param out ένα αντικείμενο DataOutputStream για την εγγραφή του αποτελέσματος.
     */
    private void showAccounts(int authToken,DataOutputStream out) throws IOException {
        boolean found = false;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).authToken == authToken) {
                found = true;
                break;
            }
        }
        StringBuilder temp = new StringBuilder();
        if (found) {
            for (int i = 0; i < accounts.size(); i++) {
                //System.out.println(accounts.get(i).username);
                temp.append(i + 1).append(".").append(" ").append(accounts.get(i).username).append("\n");
                //out.writeUTF(i+1 + "." + " " + accounts.get(i).username);
            }
            out.writeUTF(temp.toString());

        }
        else
            out.writeUTF("Invalid Auth Token");
    }

    /**
     Δημιουργεί έναν νέο λογαριασμό με ένα όνομα χρήστη και δημιουργεί ένα μοναδικό διακριτικό token για αυτόν.
     @param data ένας πίνακας που περιέχει τις απαιτούμενες πληροφορίες για τη δημιουργία του λογαριασμού, συμπεριλαμβανομένου του ονόματος χρήστη.
     @param out ένα αντικείμενο DataOutputStream για την εγγραφή του αποτελέσματος.
     */

    private void createAccount(String[] data,DataOutputStream out) throws IOException {
        if(randAuthTokens==0) {
            randAuthTokens++;
            //System.out.println("prwth");
            newAcc = new Account(data[3], randAuthTokens);
            accounts.add(newAcc);
            //System.out.println(String.valueOf(newAcc.authToken));
            out.writeUTF(String.valueOf(newAcc.authToken));
        }else
        {
            boolean found = false;
            for(int i=0;i<accounts.size();i++)
            {
                if(data[3].equals(accounts.get(i).username)) {
                    //System.out.println("Sorry, the user already exists");
                    out.writeUTF("Sorry, the user already exists");
                    found=true;
                    break;
                }
            }
            if(!found) {
               // System.out.println("deyterh");
                randAuthTokens++;
                newAcc = new Account(data[3], randAuthTokens);
                accounts.add(newAcc);
               // System.out.println(String.valueOf(newAcc.authToken));
                out.writeUTF(String.valueOf(newAcc.authToken));

            }
        }
        /*

        if(authTokens.size()==0)
        {
            authTokens.add(0, 1000);
        }
        else
        {
            authTokens.add(randAuthTokens.size(), randAuthTokens.get(randAuthTokens.size()-1) + 1);
        }

        addedAccount = new Account(args[3],authTokens.get(randAuthTokens.size()-1));
        accounts.add(addedAccount);
        System.out.println(accounts.get(size)-1);
        */

    }

    /**
     Η μέθοδος sendMessage χρησιμοποιείται για την αποστολή ενός μηνύματος από έναν λογαριασμό σε έναν άλλο.Αυτή η μέθοδος χρησιμοποιεί το senderAuthToken για να επαληθεύσει τον λογαριασμό του αποστολέα, και αν ο λογαριασμός του αποστολέα βρεθεί, δημιουργεί ένα νέο μήνυμα με το όνομα του παραλήπτη, το σώμα του μηνύματος και ένα μοναδικό αναγνωριστικό μηνύματος.
     Στη συνέχεια, η μέθοδος αναζητά το λογαριασμό παραλήπτη και προσθέτει το νέο μήνυμα στο πλαίσιο μηνυμάτων του παραλήπτη.
     Εάν βρεθεί ο παραλήπτης, στέλνει "OK" στην έξοδο μέσω του DataOutputStream. Εάν ο παραλήπτης δεν βρεθεί, στέλνει στην έξοδο το μήνυμα "User does not exist".
     Εάν το senderAuthToken είναι άκυρο, στέλνει στην έξοδο το μήνυμα "Invalid Auth Token".
     @param senderAuthToken το διακριτικό ελέγχου ταυτότητας του αποστολέα του μηνύματος.
     @param recipient ο παραλήπτης του μηνύματος.
     @param MessageBody το σώμα του μηνύματος.
     @param out DataOutputStream για την εγγραφή του αποτελέσματος.

     */

    private void sendMessage(String senderAuthToken, String recipient, String MessageBody,DataOutputStream out) throws IOException {

        String senderUsername=null;
        int foundToken=0;
        for(int i=0;i<accounts.size();i++)
        {
            if(Integer.parseInt(senderAuthToken)==accounts.get(i).authToken)
            {
                foundToken=1;
                senderUsername = accounts.get(i).username;
                break;
            }
        }

        if(foundToken>0){

        if(randmessageIDs.size()==0)
            randmessageIDs.add(0, 1000);
        else
            randmessageIDs.add(randmessageIDs.size(), randmessageIDs.get(randmessageIDs.size()-1) + 1);


            Message newMessage = new Message(senderUsername,recipient,MessageBody, randmessageIDs.get(randmessageIDs.size()-1));//kateutheian ta args
            int found = 0;
            for(int i=0;i< accounts.size();i++)
            {
                if(accounts.get(i).username.equals(recipient))
                {
                    accounts.get(i).messageBox.add(newMessage);
                    out.writeUTF("OK");
                    found=1;
                    break;
                }
            }
            if(found==0) {
                out.writeUTF("User does not exist");
            }
        }
        else{
            out.writeUTF("Invalid Auth Token");
        }

    }

    /**
     Εμφανίζει τα εισερχόμενα ενός λογαριασμού που αναγνωρίζεται από ένα συγκεκριμένο authToken.
     Τα μηνύματα στα εισερχόμενα εμφανίζονται μαζί με το messageID και το όνομα χρήστη του αποστολέα.
     Εάν ένα μήνυμα είναι αδιάβαστο, επισημαίνεται με αστερίσκο (*).
     @param authToken Το διακριτικό ελέγχου ταυτότητας που προσδιορίζει το λογαριασμό του οποίου τα εισερχόμενα θα εμφανιστούν.
     @param out DataOutputStream για την εγγραφή του αποτελέσματος
     */
    private void showInbox(int authToken,DataOutputStream out) throws IOException {
        int found=0;
        StringBuilder temp = new StringBuilder();
        for(int i=0;i< accounts.size();i++)
        {
            if(accounts.get(i).authToken== authToken)
            {
                 found = 1;
                for(int j=0;j<accounts.get(i).messageBox.size();j++)
                {
                    if(accounts.get(i).messageBox.get(j).isRead)
                        temp.append(accounts.get(i).messageBox.get(j).messageID + "." + " from: " +accounts.get(i).messageBox.get(j).sender + "\n");
                        //out.writeUTF(accounts.get(i).messageBox.get(j).messageID + "." + " from: " +accounts.get(i).messageBox.get(j).sender );
                    else
                        temp.append(accounts.get(i).messageBox.get(j).messageID + "." + " from: " +accounts.get(i).messageBox.get(j).sender + "*" +"\n");
                        //out.writeUTF(accounts.get(i).messageBox.get(j).messageID + "." + " from: " +accounts.get(i).messageBox.get(j).sender + "*" );
                }

            }
        }
        if(found>0)
            out.writeUTF(temp.toString());
        else
            out.writeUTF("Invalid Auth Token");

    }

    /**
     Διαβάζει το μήνυμα που καθορίζεται από το messageID για έναν χρήστη με το καθορισμένο authToken.
     @param authToken διακριτικό ελέγχου ταυτότητας για έναν χρήστη
     @param messageID ID του προς ανάγνωση μηνύματος
     @param out DataOutputStream για την εγγραφή του αποτελέσματος
     */

    private void readMessage(int authToken,int messageID,DataOutputStream out) throws IOException {
        int foundAuth=0;
        int found=0;
        for(int i=0;i< accounts.size();i++)
        {
            if(accounts.get(i).authToken== authToken)
            {
                foundAuth=1;
                for(int j=0;j<accounts.get(i).messageBox.size();j++)
                {
                    if(accounts.get(i).messageBox.get(j).messageID == messageID)
                    {
                        out.writeUTF("(" + accounts.get(i).messageBox.get(j).sender + ")" + " " + accounts.get(i).messageBox.get(j).body);
                        accounts.get(i).messageBox.get(j).isRead = true;
                        found=1;
                        break;
                    }
                }

            }
            if(found>0)
                break;
        }
        if(foundAuth>0)
        {
            if(found==0)
                out.writeUTF("Message ID does not exist");
        }
        else
            out.writeUTF("Invalid Auth Token");
    }

    /**
     Διαγράφει ένα μήνυμα με το καθορισμένο αναγνωριστικό μηνύματος.
     @param authToken Το διακριτικό ελέγχου ταυτότητας του χρήστη.
     @param messageID Το αναγνωριστικό του προς διαγραφή μηνύματος.
     @param out Το αντικείμενο DataOutputStream για την εγγραφή του αποτελέσματος.
     @throws IOException Εάν προκύψει σφάλμα εισόδου/εξόδου κατά την εγγραφή στο DataOutputStream.
     */

    private  void deleteMessage(int authToken,int messageID, DataOutputStream out) throws IOException {
        int found=0;
        int foundMessage=0;
        for(int i=0;i< accounts.size();i++)
        {
            if(accounts.get(i).authToken==authToken)
            {
                found=1;
                for (int j = 0; j < accounts.get(i).messageBox.size(); j++)
                {
                    if (accounts.get(i).messageBox.get(j).messageID == messageID) {
                        out.writeUTF("OK");
                        accounts.get(i).messageBox.remove(accounts.get(i).messageBox.get(j));
                        foundMessage=1;
                    }
                }
            }
        }
        if(foundMessage>0)
            out.writeUTF("Message does not exist");
        if(found==0)
            out.writeUTF("Invalid Auth Token");
    }



    public static void main(String[] args) throws IOException {
        Server server = new Server();

            //int serverPort = 7896;

            ServerSocket listenSocket = new ServerSocket(Integer.parseInt(args[0]));

            while(true) {
                Socket clientSocket = listenSocket.accept();
                new Thread(()-> {

                    //Connection c = new Connection(clientSocket);
                    DataInputStream in;
                    DataOutputStream out;
                    try {
                        in = new DataInputStream(clientSocket.getInputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        out = new DataOutputStream(clientSocket.getOutputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    /*String temp;
                    try {
                        temp = in.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    temp = temp.substring(1,temp.length()-1);
                    //System.out.println(temp);

                    String []data = temp.split(", ");
                    //String[] data = temp.split(", ");
                    for(int i=0;i< data.length;i++)
                      System.out.println(data[i]);
*/
                    StringBuilder temp;
                    try {
                        temp = new StringBuilder(in.readUTF());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    temp.deleteCharAt(0);
                    temp.deleteCharAt(temp.length()-1);

                    String temp1=temp.toString();
                    String []data = temp1.split(", ");
                    //String[] data = temp.split(", ");
                   /* for(int i=0;i< data.length;i++)
                        System.out.println(data[i]);
                    */


                    if (data[2].equals("1")) {
                        try {
                            server.createAccount(data,out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (data[2].equals("2")) {
                        try {
                            server.showAccounts(Integer.parseInt(data[3]),out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (data[2].equals("3")) {
                        try {
                            server.sendMessage(data[3], data[4], data[5],out);//stelneis olo ton pinaka args
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (data[2].equals("4")) {
                        try {
                            server.showInbox(Integer.parseInt(data[3]),out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (data[2].equals("5")) {
                        try {
                            server.readMessage(Integer.parseInt(data[3]),Integer.parseInt(data[4]),out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if(data[2].equals("6")){
                        try {
                            server.deleteMessage(Integer.parseInt(data[3]),Integer.parseInt(data[4]),out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

    }
}