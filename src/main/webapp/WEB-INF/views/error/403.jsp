<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Page d'erreur 403. Affichée par le résultat global "forbidden" (403) ou par <error-page> de web.xml.
     Aucun détail technique (trace, exception) n'est montré à l'utilisateur. --%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - Accès refusé</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="error-page">
    <h1>403 &middot; Accès refusé</h1>
    <p>Vous n'avez pas le droit d'ouvrir cette page avec votre rôle.</p>
    <p><a href="${pageContext.request.contextPath}/home">Retour à l'accueil</a></p>
</main>
</body>
</html>
