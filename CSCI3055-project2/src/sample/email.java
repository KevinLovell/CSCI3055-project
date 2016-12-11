package sample;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import java.util.Date;

public class email {
    private String subject;
    private Date date;
    private Address[] addressFrom;
    private Message message;

    public email(Date date, Address[] address, String subject, Message message) {
        this.setAddressFrom(address);
        this.setDate(date);
        this.setMessage(message);
        this.setSubject(subject);
    }

    public String getSubject() {
        return  subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Address[] getAddressFrom() { return addressFrom; }

    public void setAddressFrom(Address[] addressFrom) {
        this.addressFrom = addressFrom;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getMessageBody() {
        String body = "";
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                body = (String) content;
                return body;

            } else if (content instanceof Multipart) {
                Multipart mp = (Multipart) content;
                BodyPart bp = mp.getBodyPart(0);
                body = bp.getContent().toString();
                return body;
            }
        } catch (Exception mex) {
            mex.printStackTrace();
        }
        return body;
    }

    public String getAddressString() {
        String address = "";
        Address address1 = addressFrom[0];
        address = address1 == null ? null : ((InternetAddress) address1).getAddress();

        return address;
    }
}