<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.login"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<main class="auth-box">
    <h1><s:text name="title.login"/></h1>

    <%-- Message après une inscription réussie (propriété de l'Action, pas ${param...}) --%>
    <s:if test="registered == 1">
        <p id="msg" class="ok"><s:text name="msg.registered"/></p>
    </s:if>

    <%-- Erreur globale (identifiants faux) et erreurs de champ (validate) --%>
    <div id="errors">
        <s:actionerror/>
        <s:fielderror/>
    </div>

    <s:form id="login-form" action="doLogin" namespace="/" method="post">
        <label><s:text name="label.email"/>
            <s:textfield name="email" type="email"/>
        </label>
        <label><s:text name="label.password"/>
            <s:password name="password"/>
        </label>
        <s:submit key="button.login"/>
    </s:form>

    <p><s:a action="register" namespace="/"><s:text name="link.register"/></s:a></p>
</main>
</body>
</html>
