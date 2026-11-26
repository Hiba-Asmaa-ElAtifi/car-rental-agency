<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.consultation"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<p><s:a action="home" namespace="/">&larr; <s:text name="link.back"/></s:a></p>
<h1><s:text name="title.consultation"/></h1>

<%-- Onglets : de simples liens GET (rien n'est modifié sur cette page) --%>
<nav id="tabs">
    <s:url var="tabRentals" action="rentals" namespace="/admin"><s:param name="view" value="'rentals'"/></s:url>
    <s:url var="tabReservations" action="rentals" namespace="/admin"><s:param name="view" value="'reservations'"/></s:url>
    <s:url var="tabInvoices" action="rentals" namespace="/admin"><s:param name="view" value="'invoices'"/></s:url>
    <a href="${tabRentals}" class="<s:property value='view == "rentals" ? "active" : ""'/>"><s:text name="tab.rentals"/></a>
    <a href="${tabReservations}" class="<s:property value='view == "reservations" ? "active" : ""'/>"><s:text name="tab.reservations"/></a>
    <a href="${tabInvoices}" class="<s:property value='view == "invoices" ? "active" : ""'/>"><s:text name="tab.invoices"/></a>
</nav>

<%-- ================= Locations ================= --%>
<s:if test="view == 'rentals'">
    <s:form action="rentals" namespace="/admin" method="get">
        <s:hidden name="view" value="rentals"/>
        <s:select name="rentalFilter"
                  list="#{'active':getText('filter.active'), 'inspection':getText('filter.inspection'), 'late':getText('filter.late'), 'all':getText('filter.all')}"/>
        <s:submit key="button.filter"/>
    </s:form>
    <p><s:text name="msg.total"><s:param value="total"/></s:text></p>
    <table id="rentals">
        <thead>
        <tr>
            <th>#</th><th><s:text name="col.customer"/></th><th><s:text name="col.vehicle"/></th>
            <th><s:text name="col.pickup"/></th><th><s:text name="col.plannedReturn"/></th>
            <th><s:text name="col.actualReturn"/></th><th><s:text name="col.agent"/></th><th><s:text name="col.status"/></th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="pageItems" var="r">
            <%-- Signal visuel : retard décidé par le métier (late), couleur du statut par sa valeur --%>
            <tr class="<s:property value='late(#r) ? "late" : ""'/>">
                <td><s:property value="#r.id"/></td>
                <td><s:property value="#r.reservation.customer.fullName"/></td>
                <td><s:property value="#r.reservation.vehicule.brand"/> <s:property value="#r.reservation.vehicule.model"/>
                    (<s:property value="#r.reservation.vehicule.licencePlate"/>)</td>
                <td><s:property value="#r.actualPickupDate"/></td>
                <td><s:property value="#r.plannedReturnDate"/></td>
                <td><s:property value="#r.actualReturnDate"/></td>
                <td><s:property value="#r.handledByAgent.fullName"/></td>
                <td><span class="status status-<s:property value='#r.status'/>"><s:text name="%{'rental.' + #r.status}"/></span></td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</s:if>

<%-- ================= Réservations ================= --%>
<s:elseif test="view == 'reservations'">
    <s:form action="rentals" namespace="/admin" method="get">
        <s:hidden name="view" value="reservations"/>
        <s:select name="reservationStatus" list="reservationStatuses" listValue="getText('reservation.' + name())"/>
        <s:submit key="button.filter"/>
    </s:form>
    <p><s:text name="msg.total"><s:param value="total"/></s:text></p>
    <table id="reservations">
        <thead>
        <tr>
            <th>#</th><th><s:text name="col.customer"/></th><th><s:text name="col.vehicle"/></th>
            <th><s:text name="col.pickup"/></th><th><s:text name="col.plannedReturn"/></th>
            <th><s:text name="col.createdAt"/></th><th><s:text name="col.status"/></th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="pageItems" var="res">
            <tr>
                <td><s:property value="#res.id"/></td>
                <td><s:property value="#res.customer.fullName"/></td>
                <td><s:property value="#res.vehicule.brand"/> <s:property value="#res.vehicule.model"/>
                    (<s:property value="#res.vehicule.licencePlate"/>)</td>
                <td><s:property value="#res.pickupDate"/></td>
                <td><s:property value="#res.plannedReturnDate"/></td>
                <td><s:property value="#res.createdAt.toLocalDate()"/></td>
                <td><span class="status status-<s:property value='#res.status'/>"><s:text name="%{'reservation.' + #res.status}"/></span></td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</s:elseif>

<%-- ================= Factures ================= --%>
<s:else>
    <p><s:text name="msg.total"><s:param value="total"/></s:text></p>
    <table id="invoices">
        <thead>
        <tr>
            <th><s:text name="col.invoiceNumber"/></th><th><s:text name="col.issueDate"/></th>
            <th><s:text name="col.customer"/></th><th><s:text name="col.days"/></th>
            <th><s:text name="col.lateDays"/></th><th><s:text name="col.total"/></th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="pageItems" var="inv">
            <tr class="<s:property value='#inv.lateDays > 0 ? "late" : ""'/>">
                <td><s:property value="#inv.invoiceNumber"/></td>
                <td><s:property value="#inv.issueDate"/></td>
                <td><s:property value="#inv.rental.reservation.customer.fullName"/></td>
                <td><s:property value="#inv.rentalDays"/></td>
                <td><s:property value="#inv.lateDays"/></td>
                <td class="amount"><s:text name="format.money"><s:param value="#inv.totalAmount"/></s:text></td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</s:else>

<%-- Pagination : 50 lignes par page --%>
<s:if test="pageCount > 1">
    <p id="pages">
        <s:text name="msg.page"><s:param value="page"/><s:param value="pageCount"/></s:text>
        <s:if test="page > 1">
            <s:a action="rentals" namespace="/admin"><s:param name="view" value="view"/><s:param name="rentalFilter" value="rentalFilter"/>
                <s:param name="reservationStatus" value="reservationStatus"/><s:param name="page" value="page - 1"/>&larr; <s:text name="link.previous"/></s:a>
        </s:if>
        <s:if test="page < pageCount">
            <s:a action="rentals" namespace="/admin"><s:param name="view" value="view"/><s:param name="rentalFilter" value="rentalFilter"/>
                <s:param name="reservationStatus" value="reservationStatus"/><s:param name="page" value="page + 1"/><s:text name="link.next"/> &rarr;</s:a>
        </s:if>
    </p>
</s:if>
</body>
</html>
