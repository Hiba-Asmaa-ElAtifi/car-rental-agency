<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.home"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<header>
    <%-- s:property échappe le HTML : un nom saisi par un client ne peut pas injecter de script --%>
    <p><s:text name="msg.welcome"><s:param><s:property value="currentUser.fullName"/></s:param></s:text>
        &middot; <s:a action="logout" namespace="/"><s:text name="link.logout"/></s:a></p>
</header>

<main>
    <%-- Les liens dépendent du rôle. Cacher un lien n'est pas une autorisation :
         chaque Action vérifie elle-même le rôle (BaseAction.checkRole). --%>
    <s:if test="currentUser.role.name() == 'ADMINISTRATOR'">
        <h1><s:text name="home.admin.title"/></h1>
        <ul id="menu">
            <li><s:a action="users" namespace="/admin">Utilisateurs</s:a></li>
            <li><s:a action="settings" namespace="/admin">Paramètres (TVA, pénalités)</s:a></li>
            <li><s:a action="revenue" namespace="/admin">Revenus</s:a></li>
            <li><s:a action="rentals" namespace="/admin">Consultation globale</s:a></li>
            <li><s:a action="ml" namespace="/admin">Modèle ML</s:a></li>
            <li><s:a action="categories" namespace="/admin">Catégories</s:a></li>
        </ul>
    </s:if>

    <s:elseif test="currentUser.role.name() == 'AGENT'">
        <h1><s:text name="home.agent.title"/></h1>
        <ul id="menu">
            <li><s:a action="pendingReservations" namespace="/agent">Demandes en attente</s:a></li>
            <li><s:a action="pickups" namespace="/agent">Départs (pick-up)</s:a></li>
            <li><s:a action="returns" namespace="/agent">Retours et inspections</s:a></li>
            <li><s:a action="extensions" namespace="/agent">Prolongations</s:a></li>
            <li><s:a action="fleet" namespace="/agent">Flotte et maintenance</s:a></li>
            <li><s:a action="equipment" namespace="/agent">Équipements</s:a></li>
            <li><s:a action="history" namespace="/agent">Historique</s:a></li>
        </ul>
    </s:elseif>

    <s:else>
        <h1><s:text name="home.customer.title"/></h1>
        <ul id="menu">
            <li><s:a action="search" namespace="/customer">Rechercher un véhicule</s:a></li>
            <li><s:a action="myReservations" namespace="/customer">Mes réservations</s:a></li>
            <li><s:a action="myRentals" namespace="/customer">Mes locations et prolongations</s:a></li>
            <li><s:a action="myInvoices" namespace="/customer">Mes factures</s:a></li>
        </ul>
    </s:else>
</main>
</body>
</html>
