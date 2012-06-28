<html>
<head>
  <title>Generierter Datenbank Report von Database-Analyzer</title>
</head>
<body>
  <#if CustInfo?? || CustAddress??>
  <h1>Kunde: </h1>
  </#if>
  <#if CustInfo??><p><b>${CustInfo}</b></p></#if>
  <#if CustAddress??><p>${CustAddress}</p></#if>

  <#if NotesUrl?? || ContactsUrl?? || RelViewUrl??>
  <h1>Inhaltsverzeichnis</h1>
  </#if>
  <#if NotesUrl??><a href="${NotesUrl}">Notizen</a><br></#if>
  <#if ContactsUrl??><a href="${ContactsUrl}">Kontaktdaten</a><br></#if>
  <#if RelViewUrl??>
  <a href="${RelViewUrl}">Grafische Repräsentation der Datenbank</a><br>
  <a href="${FdsViewUrl}">Grafische Repräsentation der FDs</a><br>
  <a href="${TextUrl}">Textuelle Beschreibung der Datenbank</a><br>
  </#if>
</body>
</html>