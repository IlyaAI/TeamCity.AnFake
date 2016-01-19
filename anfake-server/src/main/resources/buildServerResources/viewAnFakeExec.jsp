<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<div class="parameter">
  Targets: <props:displayValue name="Targets"/>
</div>

<div class="parameter">
  Properties: <props:displayValue name="Properties"/>
</div>

<div class="parameter">
  Build script path: <props:displayValue name="Script"/>
</div>

<div class="parameter">
  Run with Mono: <props:displayCheckboxValue name="Mono"/>
</div>