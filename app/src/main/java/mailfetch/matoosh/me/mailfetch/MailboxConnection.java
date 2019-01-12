package mailfetch.matoosh.me.mailfetch;

/**
 * Created by matir on 20.12.2018.
 */
import javax.mail.*;
import java.util.Properties;

public class MailboxConnection {

    private String address;
    private int port;
    private String user;
    private String pass;
    private String protocol;

    private boolean isSecure;

    public Session session;
    public Store store;
    public Folder inbox;

    public MailboxConnection connection;

    public MailboxConnection(String address, int port, String user, String pass, String protocol) {
        this.address = address;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.protocol = protocol.substring(0, 4).toLowerCase();
        if(protocol.length() > 4 && protocol.charAt(4) == 's') {
            this.isSecure = true;
        } else {
            this.isSecure = false;
        }
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public Session getSession() {
        return session;
    }

    public Store getStore() {
        return store;
    }

    public Folder getInbox() {
        return inbox;
    }

    /**
     * Connects using the given credenctials.
     * @return
     */
    public boolean connect() {
        System.out.println("Connecting...");

        if(!verify()) {
            System.out.println("Couldn't start a mail connection, invalid credentials!");
            return false;
        }

        try {
            handleConnection();
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't start a mail connection, error establishing connection!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean verify() {
        if(address == null || port == 0 || user == null || user == "" || pass == null || protocol == null || protocol == "") {
            return false;
        }
        return true;
    }

    /**
     * Disconnects using the given credentials.
     * @return
     */
    public boolean disconnect() {
        if(!isActive()) {
            System.out.println("Couldn't disconnect a mail connection, connection inactive!");
            return false;
        }

        try {
            handleDisconnection();
            return true;
        } catch (Exception e) {
            System.out.println("Couldn't disconnect a mail connection, error disconnecting!");
            e.printStackTrace();
            return false;
        }
    }

    public void handleConnection() throws Exception {
        //connecting to pop3 inbox.
        Properties properties = System.getProperties();
        properties.setProperty("mail." + protocol + ".ssl.enable", this.isSecure + "");
        properties.setProperty("mail." + protocol + ".starttls.enable", this.isSecure + "");
        session = Session.getDefaultInstance(properties);
        if(this.isSecure) {
            store = session.getStore(protocol + "s");
        } else {
            store = session.getStore(protocol);
        }
        store.connect(getAddress(), getPort(), getUser(), getPass());
        inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);
    }


    public void handleDisconnection() throws Exception {
        inbox.close(true);
        store.close();
    }

    public Message[] getMessages() {
        if(isActive()) {
            try {
                return inbox.getMessages();
            } catch (MessagingException e) {
                return new Message[0];
            }
        }
        return new Message[0];
    }

    public int getMessageAmount() {
        try {
            return inbox.getMessageCount();
        } catch (MessagingException e) {
            return -1;
        }
    }

    public Message[] getMessages(int startRange, int endRange) {
        //adjusting amount
        try {
            if(inbox.getMessageCount() < endRange) {
                endRange = inbox.getMessageCount();
            }
            if(inbox.getMessageCount() < startRange) {
                startRange = endRange - 10;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        Message[] messages = new Message[(endRange - startRange)];
        try {
            int j = 0;
            for (int i = startRange; i < endRange; i++) {
                messages[j] = inbox.getMessages()[i];
                j++;
            }
        } catch (MessagingException e) {
            return new Message[0];
        }
        return messages;
    }

    public boolean isActive() {
        if(store == null || session == null) return false;
        return store.isConnected();
    }
}
