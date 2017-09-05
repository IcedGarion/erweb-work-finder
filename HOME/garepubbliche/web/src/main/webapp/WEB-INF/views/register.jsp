<html>

<h:head>
	<script name="jquery/jquery.js" library="primefaces"></script>
	<title>Register Utente</title>
</h:head>

<body>

	<h1>Registra nuovo utente</h1>

	<h:form>
		<p:growl id="messages"></p:growl>
		<p:panelGrid columns="2">
			<p:outputLabel value="Username:"></p:outputLabel>
			<p:inputText value="#{registerUtente.utente.username}"></p:inputText>
			<p:outputLabel value="Email:"></p:outputLabel>
			<p:inputText value="#{registerUtente.utente.email}"></p:inputText>
			<p:outputLabel value="Password:"></p:outputLabel>
			<p:inputText value="#{registerUtente.utente.password}"></p:inputText>
		</p:panelGrid>
		<p:commandButton value="Register" action="#{registerUtente.register}"
			update="messages"></p:commandButton>
	</h:form>

	<h2>
		<a href="listaBandi">Lista Bandi</a>
	</h2>

</body>
</html>