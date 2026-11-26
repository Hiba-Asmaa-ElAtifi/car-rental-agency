package ma.uir.awd.web.admin;

import ma.uir.awd.dao.UserDaoImpl;
import ma.uir.awd.metier.IUserMetier;
import ma.uir.awd.metier.UserMetierImpl;
import ma.uir.awd.model.Role;
import ma.uir.awd.web.BaseAction;

/**
 * POST /admin/deleteUser (userId, role) : supprime un utilisateur sans historique, puis redirige vers
 * /admin/users?role=${role}&result=deleted ou deleteRefused (le DAO renvoie false, comme CoachDao.delete au Lab 7).
 */
public class DeleteUserAction extends BaseAction {

    private final IUserMetier userMetier = new UserMetierImpl(new UserDaoImpl());

    private Long userId;
    private Role role = Role.AGENT;   // pour revenir sur la même liste
    private String result;

    @Override
    public String execute() {
        String denied = checkRole(Role.ADMINISTRATOR);
        if (denied != null) {
            return denied;
        }
        boolean deleted = userId != null && userMetier.delete(userId, getCurrentUser().getId());
        result = deleted ? "deleted" : "deleteRefused";
        return SUCCESS;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getResult() {
        return result;
    }
}
