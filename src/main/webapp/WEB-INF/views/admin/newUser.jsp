<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.newUser"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<p><s:a action="users" namespace="/admin">&larr; <s:text name="title.users"/></s:a></p>
<h1><s:text name="title.newUser"/></h1>

<div id="errors">
    <s:fielderror/>
</div>

<s:form id="new-user-form" action="createUser" namespace="/admin" method="post">
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
    <label><s:text name="label.role"/>
        <s:select name="role" list="roles" listValue="getText('role.' + name())"/>
    </label>
    <s:submit key="button.create"/>
</s:form>
</body>
</html>
