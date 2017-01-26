package com.booking.controllers;

import com.booking.entities.ActivityClass;
import com.booking.entities.Organisation;
import com.booking.entities.Service;
import com.booking.entities.User;
import com.booking.enums.AuditType;
import com.booking.enums.Role;
import com.booking.facades.AuditFacade;
import com.booking.facades.ClassFacade;
import com.booking.facades.BookingFacade;
import com.booking.facades.ServiceFacade;
import com.booking.util.Constants;
import com.booking.util.FacesUtil;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ClassesController implements Serializable {

    @EJB
    private ClassFacade classFacade;
    @EJB
    private BookingFacade bookingFacade;
    @EJB
    private ServiceFacade serviceFacade;
    @EJB
    private AuditFacade auditFacade;

    private List<ActivityClass> classes;
    private User loggedUser;
    private Organisation organisation;
    private Service currentService;
    private String serviceId;

    public ClassesController() {
    }

    @PostConstruct
    public void init() {
        loggedUser = FacesUtil.getCurrentUser();
        organisation = FacesUtil.getCurrentOrganisation();

        serviceId = FacesUtil.getParameter("service");
        if (serviceId != null) {
            currentService = serviceFacade.findServiceOfOrganisation(Integer.valueOf(serviceId), organisation);
            if (currentService != null) {
                classes = classFacade.findAllClassesOfService(currentService, organisation);
            } else {
                classes = classFacade.findAllClassesOfOrganisation(organisation);
            }
        } else {
            classes = classFacade.findAllClassesOfOrganisation(organisation);
        }
    }

    public String activateClass(ActivityClass activityClass) {
        classFacade.activateClass(activityClass);
        FacesUtil.addSuccessMessage("classesForm:msg", "El servicio ha sido activado correctamente.");

        try {
            // Audit class activation
            String ipAddress = FacesUtil.getRequest().getRemoteAddr();
            auditFacade.createAudit(AuditType.ACTIVAR_SERVICIO, loggedUser, ipAddress, activityClass.getId(), organisation);
        } catch (Exception e) {
            Logger.getLogger(ClassesController.class.getName()).log(Level.SEVERE, null, e);
        }

        String serviceParam = (serviceId != null) ? ("service=" + serviceId) : "";
        return "classes.xhtml" + Constants.FACES_REDIRECT + serviceParam;
    }

    public String deactivateClass(ActivityClass activityClass) {
        classFacade.deactivateClass(activityClass);
        FacesUtil.addSuccessMessage("classesForm:msg", "El servicio ha sido suspendido correctamente.");

        try {
            // Audit class suspention
            String ipAddress = FacesUtil.getRequest().getRemoteAddr();
            auditFacade.createAudit(AuditType.SUSPENDER_SERVICIO, loggedUser, ipAddress, activityClass.getId(), organisation);
        } catch (Exception e) {
            Logger.getLogger(ClassesController.class.getName()).log(Level.SEVERE, null, e);
        }

        String serviceParam = (serviceId != null) ? ("service=" + serviceId) : "";
        return "classes.xhtml" + Constants.FACES_REDIRECT + serviceParam;
    }

    public String bookClass(ActivityClass activityClass) {
        try {
            // Create the class user
            bookingFacade.createNewBooking(loggedUser, activityClass);
            // Add a booked place in the class
            classFacade.addClassBooking(activityClass);

            FacesUtil.addSuccessMessage("classesForm:msg", "La plaza ha sido reservada correctamente.");
        } catch (Exception e) {
            Logger.getLogger(ClassesController.class.getName()).log(Level.SEVERE, null, e);
            FacesUtil.addErrorMessage("classesForm:msg", "Lo sentimos, ha habido un problema al reservar la plaza.");
        }

        try {
            // Audit class suspention
            String ipAddress = FacesUtil.getRequest().getRemoteAddr();
            auditFacade.createAudit(AuditType.RESERVAR_CLASE, loggedUser, ipAddress, activityClass.getId(), organisation);
        } catch (Exception e) {
            Logger.getLogger(ClassesController.class.getName()).log(Level.SEVERE, null, e);
        }

        String serviceParam = (serviceId != null) ? ("service=" + serviceId) : "";
        return "classes.xhtml" + Constants.FACES_REDIRECT + serviceParam;
    }

    public String cancelClassBooking(ActivityClass activityClass) {
        try {
            // Create the class user
            if (bookingFacade.removeBooking(loggedUser, activityClass)) {
                // Add a booked place in the class
                classFacade.removeClassBooking(activityClass);
                FacesUtil.addSuccessMessage("classesForm:msg", "La reserva ha sido cancelada correctamente.");
            } else {
                FacesUtil.addErrorMessage("classesForm:msg", "Error, la reserva no existe.");
            }
        } catch (Exception e) {
            Logger.getLogger(ClassesController.class.getName()).log(Level.SEVERE, null, e);
            FacesUtil.addErrorMessage("classesForm:msg", "Lo sentimos, ha habido un problema al cancelar la reserva.");
        }

        try {
            // Audit booking cancelation
            String ipAddress = FacesUtil.getRequest().getRemoteAddr();
            auditFacade.createAudit(AuditType.CANCELAR_RESERVA, loggedUser, ipAddress, activityClass.getId(), organisation);
        } catch (Exception e) {
            Logger.getLogger(ClassesController.class.getName()).log(Level.SEVERE, null, e);
        }

        String serviceParam = (serviceId != null) ? ("service=" + serviceId) : "";
        return "classes.xhtml" + Constants.FACES_REDIRECT + serviceParam;
    }

    public boolean existsBooking(ActivityClass activityClass) {
        return bookingFacade.existsBooking(loggedUser, activityClass);
    }

    public List<ActivityClass> getClasses() {
        return classes;
    }

    public Role getUserRole() {
        return loggedUser.getUserRole().getRole();
    }

    public Service getCurrentService() {
        return currentService;
    }
}