<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.register"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<main class="auth-box">
    <h1><s:text name="title.register"/></h1>

    <div id="errors">
        <s:fielderror/>
    </div>

    <%-- Quand le formulaire revient avec des erreurs, Struts remet ce qui a été tapé (sauf les mots de passe) --%>
    <s:form id="register-form" action="doRegister" namespace="/" method="post">
        <label><s:text name="label.fullName"/>
            <s:textfield name="fullName"/>
        </label>
        <label><s:text name="label.email"/>
            <s:textfield name="email" type="email"/>
        </label>
        <label><s:text name="label.phone"/>
            <s:textfield name="phone" type="tel"/>
        </label>
        <label><s:text name="label.password"/>
            <s:password name="password"/>
        </label>
        <label><s:text name="label.confirmPassword"/>
            <s:password name="confirmPassword"/>
        </label>
        <s:submit key="button.register"/>
    </s:form>

    <p><s:a action="login" namespace="/"><s:text name="link.login"/></s:a></p>
</main>
</body>
</html>
