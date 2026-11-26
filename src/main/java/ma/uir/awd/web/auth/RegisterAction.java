package ma.uir.awd.web.auth;

import com.opensymphony.xwork2.ActionSupport;

import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.AuthenticationMetierImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IAuthenticationMetier;

/**
 * GET /register : formulaire (méthode input). POST /doRegister : crée un compte CUSTOMER,
 * puis redirige vers /login?registered=1 (Post/Redirect/Get).
 * La validation du formulaire est ici (format) ; la règle « e-mail déjà pris » est dans le métier.
 */
public class RegisterAction extends ActionSupport {

    public static final int MIN_PASSWORD_LENGTH = 8;

    private final IAuthenticationMetier authMetier = new AuthenticationMetierImpl(new UserDaoImpl());

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;

    @Override
    public void validate() {
        fullName = fullName == null ? "" : fullName.trim();
        email = email == null ? "" : email.trim();
        phone = phone == null ? "" : phone.trim();

        if (fullName.isEmpty()) {
            addFieldError("fullName", getText("error.fullName.required"));
        }
        if (email.isEmpty()) {
            addFieldError("email", getText("error.email.required"));
        } else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            addFieldError("email", getText("error.email.invalid"));
        }
        if (!phone.matches("^\\+?[0-9 ]{9,20}$")) {
            addFieldError("phone", getText("error.phone.invalid"));
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            addFieldError("password", getText("error.password.short"));
        } else if (!password.equals(confirmPassword)) {
            addFieldError("confirmPassword", getText("error.password.mismatch"));
        }
    }

    @Override
    public String execute() {
        try {
            authMetier.register(fullName, email, password, phone);
        } catch (BusinessException e) {
            addFieldError("email", getText(e.getMessage()));   // error.email.taken
            return INPUT;
        }
        return SUCCESS;   // redirection vers /login?registered=1
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
