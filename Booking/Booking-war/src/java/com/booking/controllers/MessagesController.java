package com.booking.controllers;

import com.booking.entities.Message;
import com.booking.entities.User;
import com.booking.facades.MessageFacade;
import com.booking.facades.UserFacade;
import com.booking.util.FacesUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Jesús Soriano
 */
@ManagedBean
@ViewScoped
public class MessagesController implements Serializable {

    @EJB
    private UserFacade userFacade;
    @EJB
    private MessageFacade messageFacade;
    
    private List<Message> allReceivedMessages;
    private List<Message> allSentMessages;
    private int allUnreadMessagesCount;
    private List<SelectItem> allUsersItems;
    private User receiver;
    private String subject;
    private String messageBody;
    private User logedUser;

    /**
     * Creates a new instance of MessagesController
     */
    public MessagesController() {
    }

    @PostConstruct
    public void postInitialize() {

        logedUser = FacesUtil.getCurrentUser();
        allUsersItems = new ArrayList<>();

        String replyId = FacesUtil.getParameter("reply");
        if (replyId != null) {
            User replyUser = userFacade.find(replyId);
            if (replyUser != null) {
                allUsersItems.add(new SelectItem(replyUser.getEmail(), replyUser.getFullName()));
            }

        } else {
            for (User user : userFacade.findAll()) {
                this.allUsersItems.add(new SelectItem(user.getEmail(), user.getFullName()));
            }
        }

        FacesUtil.removeSessionAttribute("messageSess");
    }

    public String sendMessage() {
        // TODO: comprobar que el mensaje contenga menos de 255 caracteres.
        messageFacade.sendMessage(subject, messageBody, receiver, logedUser);
        FacesUtil.addSuccessMessage("mailForm:msg", "Mensaje enviado.");
        return "mailbox.xhtml?faces-redirect=true";
    }

    public String cancelMessage() {
        return "mailbox.xhtml?faces-redirect=true";
    }

    public String viewMessage(Message message, boolean read) {
        message.setStatus(read);
        messageFacade.edit(message);
        FacesUtil.setSessionAttribute("messageSess", message);
        FacesUtil.getFlash().put("message", message);
        return "view-message.xhtml?faces-redirect=true";
    }

    public int getAllUnreadMessagesCount() {
        allUnreadMessagesCount = messageFacade.getUnreadMessages(logedUser).size();
        return allUnreadMessagesCount;
    }

    public void setAllUnreadMessagesCount(int allUnreadMessagesCount) {
        this.allUnreadMessagesCount = allUnreadMessagesCount;
    }

    public List<SelectItem> getAllUsersItems() {
        return Collections.unmodifiableList(allUsersItems);
    }

    public List<Message> getAllReceivedMessages() {
        allReceivedMessages = messageFacade.findAllReceivedMessages(logedUser);
        return Collections.unmodifiableList(allReceivedMessages);
    }

    public List<Message> getAllSentMessages() {
        allSentMessages = messageFacade.findAllSentMessages(logedUser);
        return Collections.unmodifiableList(allSentMessages);
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}