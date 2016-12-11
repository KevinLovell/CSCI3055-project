package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.mail.*;
import java.util.Properties;

public class ConnectionThread implements Runnable {
    private String username, password, client, host;
    private ObservableList<email> emails = FXCollections.observableArrayList();
    private Folder emailFolder;
    private Store store;

    public ConnectionThread(String username, String password, String client) {
        this.setUsername(username);
        this.setPassword(password);
        this.host = client;
    }

    boolean notInterrupted() {
        if(Thread.interrupted()) {
            try {
                emailFolder.close(false);
                store.close();
                return false;
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void run() {
        while(notInterrupted()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
                try {
                    emailFolder.close(false);
                    store.close();
                } catch(AuthenticationFailedException e) {

                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public ObservableList<email> getEmails() {
        return emails;
    }


    public void sendMail(String Address, String Subject, String Body) {
        System.out.println(Address + Subject + Body);

    }

    public String mailBody(int index) {
        String body = "";
        try {
            Object content = emails.get(index).getMessage().getContent();
            if (content instanceof String) {
                body = (String) content;
                //System.out.println("CONTENT:" + body);
                return body;

            } else if (content instanceof Multipart) {
                Multipart mp = (Multipart) content;
                BodyPart bp = mp.getBodyPart(0);
                //System.out.println("CONTENT:" + bp.getContent());
                body = bp.getContent().toString();
                return body;
            }
        } catch (Exception mex) {
            mex.printStackTrace();
        }
        return body;
    }

    public String getNumberOfMsg() {
        String label = "Inbox("+Integer.toString(emails.size()) + " messages)";
        return label;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ObservableList<String> getAllMail() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        ObservableList<String> messages2 = FXCollections.observableArrayList();

        try {
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new javax.mail.PasswordAuthentication(username, password);
                        }
                    });

            store = session.getStore();

            store.connect(host, username, password);

            emailFolder = store.getFolder("Inbox");
            System.out.println(store.getDefaultFolder().getURLName().toString());
            emailFolder.open(Folder.READ_ONLY);
            System.out.println(emailFolder.getParent().getURLName().toString());
            System.out.println(emailFolder.getURLName().toString());
            // System.out.println(emailFolder.getParent().);
            Folder[] f = store.getDefaultFolder().list();
            for(Folder fd:f)
                System.out.println(">> "+fd.getName());

            Message[] messages = emailFolder.getMessages();

            for (int i = 0, j = messages.length; i < j; i++) {
                Message message = messages[i];
                emails.add(new email(message.getSentDate(), message.getFrom(), message.getSubject(), message));
                messages2.add(String.valueOf(i+1) + ". " + message.getFrom()[0] + ": " + message.getSubject());
            }

        } catch(AuthenticationFailedException e) {
            System.out.println("wrong credentials");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages2;
    }
}