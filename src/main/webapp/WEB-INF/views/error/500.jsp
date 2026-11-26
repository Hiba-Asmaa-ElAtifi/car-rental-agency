<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Page d'erreur 500. Affichée par le résultat global "forbidden" (403) ou par <error-page> de web.xml.
     Aucun détail technique (trace, exception) n'est montré à l'utilisateur. --%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - Erreur du serveur</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="error-page">
    <h1>500 &middot; Erreur du serveur</h1>
    <p>Une erreur inattendue s'est produite. Réessayez plus tard ; si elle persiste, prévenez l'administrateur.</p>
    <p><a href="${pageContext.request.contextPath}/home">Retour à l'accueil</a></p>
</main>
</body>
</html>
