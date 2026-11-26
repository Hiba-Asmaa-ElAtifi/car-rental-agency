package ma.uir.awd.web.auth;

import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * GET /home : page d'accueil après la connexion, pour les trois rôles.
 * La JSP affiche les liens du rôle de l'utilisateur (currentUser.role).
 * Le contrôle de session vient de BaseAction (Salma) : sans utilisateur en session, résultat "login".
 */
public class HomeAction extends BaseAction {

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR, Role.AGENT, Role.CUSTOMER);
        if (denied != null) {
            return denied;
        }
        return SUCCESS;
    }
}
