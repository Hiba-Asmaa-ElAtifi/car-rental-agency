<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.users"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<p><s:a action="home" namespace="/">&larr; <s:text name="link.back"/></s:a></p>
<h1><s:text name="title.users"/></h1>

<%-- Résultat de la dernière action, après la redirection (comme ok / cancelled au Lab 8).
     La classe CSS est choisie sur une valeur calculée par l'Action (resultOk), pas dans la vue. --%>
<s:if test="resultKey != null">
    <p id="msg" class="<s:property value='resultOk ? "ok" : "ko"'/>"><s:text name="%{resultKey}"/></p>
</s:if>

<div id="errors">
    <s:actionerror/>
    <s:fielderror/>
</div>

<s:form id="filter-form" action="users" namespace="/admin" method="get">
    <label><s:text name="label.role"/>
        <s:select name="role" list="roles" listValue="getText('role.' + name())"/>
    </label>
    <s:submit key="button.filter"/>
</s:form>

<p><s:a action="newUser" namespace="/admin"><s:text name="link.newUser"/></s:a></p>

<s:if test="users.isEmpty()">
    <p><s:text name="msg.users.none"/></p>
</s:if>
<s:else>
    <table id="users">
        <thead>
        <tr>
            <th><s:text name="label.fullName"/></th>
            <th><s:text name="label.email"/></th>
            <th><s:text name="label.phone"/></th>
            <th><s:text name="label.createdAt"/></th>
            <th><s:text name="label.actions"/></th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="users">
            <tr data-id="<s:property value='id'/>">
                <td><s:property value="fullName"/></td>
                <td><s:property value="email"/></td>
                <td><s:property value="phone"/></td>
                <td><s:property value="createdAt.toLocalDate()"/></td>
                <td>
                    <%-- Toute modification passe par un POST, jamais par un lien GET --%>
                    <s:form action="changeRole" namespace="/admin" method="post" cssClass="inline">
                        <s:hidden name="userId" value="%{id}"/>
                        <s:select name="newRole" list="roles" value="%{role}" listValue="getText('role.' + name())"/>
                        <s:submit key="button.changeRole"/>
                    </s:form>
                    <s:form action="deleteUser" namespace="/admin" method="post" cssClass="inline">
                        <s:hidden name="userId" value="%{id}"/>
                        <s:hidden name="role" value="%{role}"/>
                        <s:submit key="button.delete"/>
                    </s:form>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</s:else>
</body>
</html>
