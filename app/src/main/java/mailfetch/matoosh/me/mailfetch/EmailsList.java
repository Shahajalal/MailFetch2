package mailfetch.matoosh.me.mailfetch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class EmailsList extends AppCompatActivity implements EmailMessage.OnFragmentInteractionListener {

    ArrayList<Fragment> emails = new ArrayList<>();
    String[] connectionInfo;
    public int currentPage;
    MailboxConnection connection;

    public static final int MSG_PER_PAGE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emails_list);

        connectionInfo = getIntent().getStringArrayExtra(MainActivity.CONNECTION_PASS);
        currentPage = 0;

        refresh();
    }

    public void refresh() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment frag :
                emails) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.commit();

        new RetrieveMailTask().execute(connectionInfo);
    }

    public void onRefreshClicked (View view) {
        refresh();
    }
    public void onNextClicked(View view) {
        currentPage++;
        refresh();
    }
    public void onPreviousClicked(View view) {
        if(currentPage == 0) return;
        currentPage--;
        refresh();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class RetrieveMailTask extends AsyncTask<String[], Void, Message[]> {

        String error;

        @Override
        protected Message[] doInBackground(String[]... strings) {
            String[] info = strings[0];
            String address = info[0];
            int port = Integer.parseInt(info[1]);
            String user = info[2];
            String pass = info[3];
            String protocol = info[4];

            //do connection
            connection = new MailboxConnection(address, port, user, pass, protocol);
            if(connection.connect()) {
                int startRange = connection.getMessageAmount() - MSG_PER_PAGE*(currentPage + 1);

                if(startRange < 0) startRange = 0;

                Message[] messages =  connection.getMessages(startRange, startRange + MSG_PER_PAGE);
                return messages;
            } else {
                error = "There was an error retrieving the mail!";
                return null;
            }
        }

        @Override
        protected void onPostExecute(Message[] messages) {
            if(error != null && error != "") {
                Toast.makeText(EmailsList.this, error, Toast.LENGTH_LONG).show();
                return;
            }

            for (int i = messages.length - 1; i >= 0; i--) {
                new PopulateListTask().execute(messages[i]);
            }
        }
    }
    class DisconnectTask extends AsyncTask<MailboxConnection, Void, Void> {

        @Override
        protected Void doInBackground(MailboxConnection... mailboxConnections) {
            for (MailboxConnection connection :
                    mailboxConnections) {
                connection.disconnect();
            }
            return null;
        }
    }
    class PopulateListTask extends AsyncTask<Message, Void, Void> {

        String sender;
        String recipient;
        String topic;
        String content;
        boolean seen;

        Exception error;

        @Override
        protected Void doInBackground(Message... messages) {
            Message msg = messages[0];
            try {
                sender = msg.getFrom()[0].toString();
                if(msg.getAllRecipients() != null && msg.getAllRecipients().length > 1) {
                    recipient = msg.getAllRecipients()[0].toString();
                } else {
                    recipient = "";
                }
                topic = msg.getSubject();
                content = getTextFromMessage(msg);
                //Toast.makeText(EmailsList.this,content,Toast.LENGTH_SHORT).show();
                Log.d("content", content);
                Log.d("sender", sender);
                seen = msg.isSet(Flags.Flag.SEEN);
            } catch (MessagingException e) {
                error = e;
            } catch (IOException e) {
                error = e;
            }

            return null;
        }

        private String getTextFromMessage(Message message) throws MessagingException, IOException {
            String result = "";
            if (message.isMimeType("text/plain")) {
                result = message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = getTextFromMimeMultipart(mimeMultipart);
            }
            return result;
        }

        private String getTextFromMimeMultipart(
                MimeMultipart mimeMultipart)  throws MessagingException, IOException{
            String result = "";
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break; // without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
                } else if (bodyPart.getContent() instanceof MimeMultipart){
                    result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            EmailMessage message = EmailMessage.newInstance(sender, recipient, topic, content, !seen);
            transaction.add(R.id.emails_list, message, topic);
            emails.add(message);
            transaction.commitAllowingStateLoss();

            if(emails.size() >= MSG_PER_PAGE) {
               new DisconnectTask().execute(connection);
            }
        }
    }
}


