package ma.uir.awd.web.admin;

import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.BusinessException;
import ma.uir.awd.metier.IUserMetier;
import ma.uir.awd.metier.UserMetierImpl;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * POST /admin/changeRole (userId, newRole) : change le rôle, puis redirige vers
 * /admin/users?role=${newRole}&result=roleChanged (ou result=self si l'admin se vise lui-même).
 */
public class ChangeRoleAction extends BaseAction {

    private final IUserMetier userMetier = new UserMetierImpl(new UserDaoImpl());

    private Long userId;
    private Role newRole;
    private String result;

    @Override
    public void validate() {
        if (checkRole(Role.ADMINISTRATOR) != null) {
            return;   // pas admin : pas de validation, execute() renverra "login" ou "forbidden"
        }
        if (userId == null || newRole == null) {
            addActionError(getText("error.user.notFound"));
        }
    }

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        try {
            userMetier.changeRole(userId, newRole, getCurrentUser().getId());
            result = "roleChanged";
        } catch (BusinessException e) {
            result = "error.user.self".equals(e.getMessage()) ? "self" : "notFound";
        }
        return SUCCESS;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getNewRole() {
        return newRole;
    }

    public void setNewRole(Role newRole) {
        this.newRole = newRole;
    }

    public String getResult() {
        return result;
    }
}
