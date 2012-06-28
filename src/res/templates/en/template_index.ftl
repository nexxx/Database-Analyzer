<html>
<head>
  <title>Generated Database Report by Database-Analyzer</title>
</head>
<body>
  <#if CustInfo?? || CustAddress??>
  <h1>Customer: </h1>
  </#if>
  <#if CustInfo??><p><b>${CustInfo}</b></p></#if>
  <#if CustAddress??><p>${CustAddress}</p></#if>

  <#if NotesUrl?? || ContactsUrl?? || RelViewUrl??>
  <h1>List of contents</h1>
  </#if>
  <#if NotesUrl??><a href="${NotesUrl}">Notes</a><br></#if>
  <#if ContactsUrl??><a href="${ContactsUrl}">Contact Data</a><br></#if>
  <#if RelViewUrl??>
  <a href="${RelViewUrl}">Graphical Relation View</a><br>
  <a href="${FdsViewUrl}">Graphical FDs View</a><br>
  <a href="${TextUrl}">Textual Database Description</a><br>
  </#if>
</body>
</html>