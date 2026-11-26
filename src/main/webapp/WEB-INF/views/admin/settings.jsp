<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.settings"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<p><s:a action="home" namespace="/">&larr; <s:text name="link.back"/></s:a></p>
<h1><s:text name="title.settings"/></h1>

<s:if test="saved == 1">
    <p id="msg" class="ok"><s:text name="msg.settings.saved"/></p>
</s:if>

<div id="errors">
    <s:actionerror/>
    <s:fielderror/>
</div>

<s:form id="settings-form" action="saveSettings" namespace="/admin" method="post">
    <label><s:text name="label.vatPercent"/>
        <s:textfield name="vatPercent" type="number" step="0.01" min="0" max="100"/>
    </label>
    <label><s:text name="label.lateFeePercent"/>
        <s:textfield name="lateFeePercent" type="number" step="0.01" min="0" max="100"/>
    </label>
    <s:submit key="button.save"/>
</s:form>
</body>
</html>
