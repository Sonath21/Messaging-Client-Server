# Request-Reply Messaging App

# ΥΠΟΘΕΣΕΙΣ
-Ο χρήστης ανοίγει τον σέρβερ με την μορφή java Server.java port (οπου port πχ 5000)
-Δίνονται σωστα τα inputs 
-Γράφοντας στο terminal, αναφορά στα αρχεία συνοδευόμενα με .java (πχ java Server.java 5000, ή java Client.java localhost)


---------------------------------!. SERVER .!------------------------------------------


## Functions 

### createAccount() 
Δημιουργεί έναν νέο λογαριασμό με ένα όνομα χρήστη και δημιουργεί ένα μοναδικό διακριτικό token για αυτόν.

### showAccounts() 
Εμφανίζει μια λίστα λογαριασμών με τα αντίστοιχα ονόματα χρηστών. Η μέθοδος αναζητά το authToken μεταξύ των λογαριασμών και αν βρεθεί, τα αντίστοιχα ονόματα χρηστών γράφονται στο DataOutputStream.
Εάν το authToken δεν βρεθεί, η μέθοδος γράφει "Invalid Auth Token" στο DataOutputStream.

### showInbox() 
Εμφανίζει τα εισερχόμενα ενός λογαριασμού που αναγνωρίζεται από ένα συγκεκριμένο authToken.
Τα μηνύματα στα εισερχόμενα εμφανίζονται μαζί με το messageID και το όνομα χρήστη του αποστολέα.
Εάν ένα μήνυμα είναι αδιάβαστο, επισημαίνεται με αστερίσκο (*).

### readMessage() 
Διαβάζει το μήνυμα που καθορίζεται από το messageID για έναν χρήστη με το καθορισμένο authToken.

### deleteMessage() 
Διαγράφει ένα μήνυμα με το καθορισμένο αναγνωριστικό μηνύματος.

### sendMessage() 
Η μέθοδος sendMessage χρησιμοποιείται για την αποστολή ενός μηνύματος από έναν λογαριασμό σε έναν άλλο.Αυτή η μέθοδος χρησιμοποιεί το senderAuthToken για να επαληθεύσει τον λογαριασμό του αποστολέα, και αν ο λογαριασμός του αποστολέα βρεθεί, δημιουργεί ένα νέο μήνυμα με το όνομα του παραλήπτη, το σώμα του μηνύματος και ένα μοναδικό αναγνωριστικό μηνύματος.
Στη συνέχεια, η μέθοδος αναζητά το λογαριασμό παραλήπτη και προσθέτει το νέο μήνυμα στο πλαίσιο μηνυμάτων του παραλήπτη.
Εάν βρεθεί ο παραλήπτης, στέλνει "OK" στην έξοδο μέσω του DataOutputStream. Εάν ο παραλήπτης δεν βρεθεί, στέλνει στην έξοδο το μήνυμα "User does not exist".
Εάν το senderAuthToken είναι άκυρο, στέλνει στην έξοδο το μήνυμα "Invalid Auth Token".

### main() 
Εκτελούνται απλα κομμάτια που έχουν να κανουν με το την επικοινωνία με τον client, το πως θα στείλει ή θα λάβει το request του client, άνοιγμα-κλείσιμο thread.


-----------------------------------!. CLIENT .!--------------------------------------

Δημιουργία socket με arguments: ip, port number. Στέλνει request και λαμβάνει απάντηση απο τον σέρβερ μέσω του terminal
