<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Car Rental - <s:text name="title.ml"/></title>
    <link rel="stylesheet" href="<s:url value='/css/style.css'/>">
</head>
<body>
<p><s:a action="home" namespace="/">&larr; <s:text name="link.back"/></s:a></p>
<h1><s:text name="title.ml"/></h1>
<p><s:text name="ml.task"/></p>

<%-- Résultat du réentraînement, après la redirection (propriété de l'Action, pas ${param...}) --%>
<s:if test="retrained == 1">
    <p id="msg" class="ok"><s:text name="msg.ml.retrained"/></p>
</s:if>
<s:if test="retrained == 0">
    <p id="msg" class="ko"><s:text name="error.ml.notAvailable"/></p>
</s:if>

<s:if test="metrics == null">
    <p id="no-model"><s:text name="msg.ml.noModel"/></p>
</s:if>
<s:else>
    <p>
        <s:text name="label.ml.algorithm"/> : <s:property value="metrics.algorithm"/> &middot;
        <s:text name="label.ml.trainedAt"/> : <s:date name="metrics.trainedAt" format="dd/MM/yyyy HH:mm"/> &middot;
        <s:text name="label.ml.sizes"/> : <s:property value="metrics.trainingSize"/> / <s:property value="metrics.testSize"/>
    </p>

    <%-- Comparaison avec la baseline (exigence de l'énoncé) ; les valeurs sont affichées en pourcentages --%>
    <table id="metrics">
        <thead>
        <tr>
            <th><s:text name="label.ml.metric"/></th>
            <th><s:text name="label.ml.model"/></th>
            <th><s:text name="label.ml.baseline"/></th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>F1</td>
            <td><s:text name="format.percent"><s:param value="metrics.f1Score"/></s:text></td>
            <td><s:text name="format.percent"><s:param value="metrics.baselineF1"/></s:text></td>
        </tr>
        <tr>
            <td>Accuracy</td>
            <td><s:text name="format.percent"><s:param value="metrics.accuracy"/></s:text></td>
            <td><s:text name="format.percent"><s:param value="metrics.baselineAccuracy"/></s:text></td>
        </tr>
        <tr>
            <td>Precision</td>
            <td><s:text name="format.percent"><s:param value="metrics.precisionScore"/></s:text></td>
            <td>&mdash;</td>
        </tr>
        <tr>
            <td>Recall</td>
            <td><s:text name="format.percent"><s:param value="metrics.recall"/></s:text></td>
            <td>&mdash;</td>
        </tr>
        </tbody>
    </table>

    <%-- Signal visuel : la classe CSS est choisie sur une valeur calculée par l'Action (betterThanBaseline) --%>
    <s:if test="betterThanBaseline">
        <p class="ok"><s:text name="msg.ml.better"/></p>
    </s:if>
    <s:else>
        <p class="ko"><s:text name="msg.ml.notBetter"/></p>
    </s:else>
</s:else>

<%-- Réentraînement : un POST (jamais un lien GET), suivi d'une redirection --%>
<s:form id="retrain-form" action="retrain" namespace="/admin" method="post">
    <s:submit key="button.retrain"/>
</s:form>
</body>
</html>
