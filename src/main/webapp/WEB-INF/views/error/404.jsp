<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Page d'erreur 404. Affichée par le résultat global "forbidden" (403) ou par <error-page> de web.xml.
     Aucun détail technique (trace, exception) n'est montré à l'utilisateur. --%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - Page introuvable</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="error-page">
    <h1>404 &middot; Page introuvable</h1>
    <p>Cette page n'existe pas ou l'élément demandé a été supprimé.</p>
    <p><a href="${pageContext.request.contextPath}/home">Retour à l'accueil</a></p>
</main>
</body>
</html>
