package ma.uir.awd.web.admin;

import java.util.List;

import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.IUserMetier;
import ma.uir.awd.metier.UserMetierImpl;
import ma.uir.awd.model.Role;
import ma.uir.awd.model.User;
import ma.uir.awd.web.BaseAction;

/**
 * GET /admin/users?role=AGENT : liste des utilisateurs d'un rôle (AGENT par défaut ; il y a plus de 1 400 clients).
 * Affiche aussi le résultat de la dernière action (result=created, roleChanged, ...), lu après une redirection,
 * comme ok / cancelled au Lab 8.
 */
public class UsersAction extends BaseAction {

    private final IUserMetier userMetier = new UserMetierImpl(new UserDaoImpl());

    private Role role = Role.AGENT;   // "XYZ" : Struts signale lui-même l'erreur de conversion
    private String result;
    private List<User> users;

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        users = userMetier.findByRole(role);
        return SUCCESS;
    }

    /** Clé du message à afficher, ou null. Seules ces valeurs sont acceptées (pas de clé venant de l'URL). */
    public String getResultKey() {
        if (result == null) {
            return null;
        }
        switch (result) {
            case "created":       return "msg.user.created";
            case "roleChanged":   return "msg.user.roleChanged";
            case "deleted":       return "msg.user.deleted";
            case "deleteRefused": return "msg.user.deleteRefused";
            case "self":          return "error.user.self";
            case "notFound":      return "error.user.notFound";
            default:              return null;
        }
    }

    /** Résultat positif (vert) ou refus (rouge) : la JSP choisit la classe CSS sur cette valeur. */
    public boolean isResultOk() {
        return "created".equals(result) || "roleChanged".equals(result) || "deleted".equals(result);
    }

    public Role[] getRoles() {
        return Role.values();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<User> getUsers() {
        return users;
    }
}
