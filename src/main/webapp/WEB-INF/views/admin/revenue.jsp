<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.revenue"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<p><s:a action="home" namespace="/">&larr; <s:text name="link.back"/></s:a></p>
<h1><s:text name="title.revenue"/></h1>

<div id="errors">
    <s:actionerror/>
    <s:fielderror/>
</div>

<%-- Formulaire en GET : il ne modifie rien. Struts remet les valeurs saisies. --%>
<s:form id="revenue-form" action="revenue" namespace="/admin" method="get">
    <label><s:text name="label.revenue.year"/>
        <s:textfield name="year" type="number"/>
    </label>
    <label><s:text name="label.revenue.from"/>
        <s:textfield name="from" type="date"/>
    </label>
    <label><s:text name="label.revenue.to"/>
        <s:textfield name="to" type="date"/>
    </label>
    <s:submit key="button.filter"/>
</s:form>

<s:if test="byMonth != null">
    <h2><s:text name="title.revenue.byMonth"><s:param value="year"/></s:text></h2>
    <table id="revenue-month">
        <thead>
        <tr>
            <th><s:text name="label.revenue.month"/></th>
            <th><s:text name="label.revenue.amount"/></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <%-- key / value : entrées de la Map calculée par le métier (12 mois, à 0 s'il n'y a pas de facture) --%>
        <s:iterator value="byMonth">
            <tr>
                <td><s:text name="%{'month.' + key}"/></td>
                <td class="amount"><s:text name="format.money"><s:param value="value"/></s:text></td>
                <td><span class="bar" style="width: <s:property value='monthBarPercent(key)'/>%"></span></td>
            </tr>
        </s:iterator>
        </tbody>
        <tfoot>
        <tr>
            <th><s:text name="label.revenue.total"/></th>
            <th class="amount"><s:text name="format.money"><s:param value="yearTotal"/></s:text></th>
            <th></th>
        </tr>
        </tfoot>
    </table>
</s:if>

<s:if test="byCategory != null">
    <h2><s:text name="title.revenue.byCategory"><s:param value="from"/><s:param value="to"/></s:text></h2>
    <s:if test="byCategory.isEmpty()">
        <p><s:text name="msg.revenue.none"/></p>
    </s:if>
    <s:else>
        <table id="revenue-category">
            <thead>
            <tr>
                <th><s:text name="label.revenue.category"/></th>
                <th><s:text name="label.revenue.amount"/></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <s:iterator value="byCategory">
                <tr>
                    <td><s:property value="key"/></td>
                    <td class="amount"><s:text name="format.money"><s:param value="value"/></s:text></td>
                    <td><span class="bar" style="width: <s:property value='categoryBarPercent(key)'/>%"></span></td>
                </tr>
            </s:iterator>
            </tbody>
            <tfoot>
            <tr>
                <th><s:text name="label.revenue.total"/></th>
                <th class="amount"><s:text name="format.money"><s:param value="periodTotal"/></s:text></th>
                <th></th>
            </tr>
            </tfoot>
        </table>
    </s:else>
</s:if>
</body>
</html>
