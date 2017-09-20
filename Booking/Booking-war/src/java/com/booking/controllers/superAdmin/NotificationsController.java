package com.booking.controllers.superAdmin;

import com.booking.entities.Notification;
import com.booking.entities.Organisation;
import com.booking.entities.User;
import com.booking.enums.NotificationType;
import com.booking.enums.Role;
import com.booking.facades.NotificationFacade;
import com.booking.facades.UserFacade;
import com.booking.util.DateService;
import com.booking.util.FacesUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

/**
 *
 * @author Jesús Soriano
 */
@ManagedBean
public class NotificationsController implements Serializable {

    @EJB
    private NotificationFacade notificationFacade;

    private List<Notification> notificationList;
    private Role userRole;
    private List<SelectItem> notificationTypes;
    private NotificationType selectedNotificationType;
    private User loggedUser;

    @PostConstruct
    public void init() {
        loggedUser = FacesUtil.getCurrentUser();
        userRole = loggedUser.getUserRole().getRole();

        notificationTypes = new ArrayList<>();
        notificationTypes.add(new SelectItem(null, "All"));

        if (userRole == Role.ADMIN || userRole == Role.SUPER_ADMIN) {
            for (NotificationType at : NotificationType.getAllNotificationTypes()) {
                notificationTypes.add(new SelectItem(at.name(), at.name()));
            }
        } else {
            for (NotificationType at : NotificationType.getNotificationTypesForClient()) {
                notificationTypes.add(new SelectItem(at.name(), at.name()));
            }
        }
        
        search();
    }

    public void search() {
        if (selectedNotificationType != null) {
            notificationList = notificationFacade.findAllNotificationsOfTypeOfUser(loggedUser, selectedNotificationType);
        } else {
            notificationList = notificationFacade.findAllNotificationsOfUser(loggedUser);
        }
    }
    
    public boolean isAdminNotification (NotificationType notificationType) {
        return (notificationType == NotificationType.REGISTRO_USUARIO) || (notificationType == NotificationType.BAJA_USUARIO)  || (notificationType == NotificationType.RESERVA_SUSPENDIDA);
    }

    public boolean loggedUserIsAdmin() {
        return userRole == Role.ADMIN || userRole == Role.SUPER_ADMIN;
    }
    
    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public Role getUserRole() {
        return userRole;
    }

    public List<SelectItem> getNotificationTypes() {
        return notificationTypes;
    }

    public NotificationType getSelectedNotificationType() {
        return selectedNotificationType;
    }

    public void setSelectedNotificationType(NotificationType selectedNotificationType) {
        this.selectedNotificationType = selectedNotificationType;
    }
}
