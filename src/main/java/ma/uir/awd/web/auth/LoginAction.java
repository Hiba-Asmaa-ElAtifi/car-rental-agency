package ma.uir.awd.web.auth;

import java.util.Map;

import org.apache.struts2.action.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.AuthenticationMetierImpl;
import ma.uir.awd.metier.IAuthenticationMetier;
import ma.uir.awd.model.User;
import ma.uir.awd.web.BaseAction;

/**
 * GET /login : affiche le formulaire (méthode input, non validée).
 * POST /doLogin (email, password) : vérifie avec le métier, range l'utilisateur en session, redirige vers /home.
 * Style du Lab 8 : champs + setters (Struts convertit), validate() + résultat input, textes dans package.properties.
 * Différence avec le Lab 8 imposée par l'énoncé : l'Action parle au métier, jamais au DAO.
 */
public class LoginAction extends ActionSupport implements SessionAware {

    private final IAuthenticationMetier authMetier = new AuthenticationMetierImpl(new UserDaoImpl());

    private Map<String, Object> session;
    private String email;
    private String password;
    private Integer registered;   // 1 après une inscription réussie (redirection depuis RegisterAction)

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }

    // input() vient de ActionSupport : il renvoie INPUT et n'est pas validé.

    @Override
    public void validate() {
        email = email == null ? "" : email.trim();
        if (email.isEmpty()) {
            addFieldError("email", getText("error.email.required"));
        } else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            addFieldError("email", getText("error.email.invalid"));
        }
        if (password == null || password.isEmpty()) {
            addFieldError("password", getText("error.password.required"));
        }
    }

    @Override
    public String execute() {
        User user = authMetier.authenticate(email, password);
        if (user == null) {
            // même message que l'e-mail ou le mot de passe soit faux (on ne révèle pas quels comptes existent)
            addActionError(getText("error.login.failed"));
            password = null;   // le mot de passe n'est jamais réaffiché
            return INPUT;
        }
        session.put(BaseAction.SESSION_USER, user);
        return SUCCESS;   // struts-hiba.xml : redirection vers /home (Post/Redirect/Get)
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /** Pas de getter pour password : la JSP ne le réaffiche jamais. */
    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }
}
