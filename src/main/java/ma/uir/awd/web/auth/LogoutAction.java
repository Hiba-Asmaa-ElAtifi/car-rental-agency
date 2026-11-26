package ma.uir.awd.web.auth;

import java.util.Map;

import org.apache.struts2.action.SessionAware;
import org.apache.struts2.dispatcher.SessionMap;

import com.opensymphony.xwork2.ActionSupport;

/** GET /logout : termine la session, puis redirige vers /login (comme le logout du Lab 9). */
public class LogoutAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }

    @Override
    public String execute() {
        if (session instanceof SessionMap) {
            ((SessionMap) session).invalidate();   // équivalent de session.invalidate() du Lab 9
        } else if (session != null) {
            session.clear();
        }
        return SUCCESS;
    }
}
