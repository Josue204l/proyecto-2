package Interfaz.login;

import logic.Usuario;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelLogin {

    public static final String CURRENT_USER = "currentUser";

    private Usuario currentUser;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelLogin() {
        this.currentUser = null;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public Usuario getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Usuario currentUser) {
        Usuario oldUser = this.currentUser;
        this.currentUser = currentUser;
        propertyChangeSupport.firePropertyChange(CURRENT_USER, oldUser, currentUser);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}