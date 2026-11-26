package ma.uir.awd.web.admin;

import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IUserMetier;
import ma.uir.awd.metier.UserMetierImpl;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * GET /admin/newUser : formulaire (méthode input). POST /admin/createUser : l'admin crée un compte
 * (en général un AGENT), puis redirige vers /admin/users?role=...&result=created (Post/Redirect/Get).
 * Même découpage que AddClassAction au Lab 8.
 */
public class CreateUserAction extends BaseAction {

    private final IUserMetier userMetier = new UserMetierImpl(new UserDaoImpl());

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role = Role.AGENT;

    /** GET : seul l'admin voit le formulaire. */
    @Override
    public String input() {
        String denied = checkRole(Role.ADMINISTRATOR);
        return denied != null ? denied : INPUT;
    }

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
        if (password == null || password.length() < 8) {
            addFieldError("password", getText("error.password.short"));
        }
        if (role == null) {
            addFieldError("role", getText("error.role.required"));
        }
    }

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        try {
            userMetier.create(fullName, email, password, phone, role);
        } catch (BusinessException e) {
            addFieldError("email", getText(e.getMessage()));
            return INPUT;
        }
        return SUCCESS;   // redirection vers /admin/users?role=${role}&result=created
    }

    public Role[] getRoles() {
        return Role.values();
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
