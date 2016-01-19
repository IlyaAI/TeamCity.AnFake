<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<div class="parameter">
  Path to packages.config (or .sln): <props:displayValue name="PackagesConfig"/>
</div>

<div class="parameter">
  Source Url(s): <props:displayValue name="SourceUrl"/>
</div>

<div class="parameter">
  Solution directory: <props:displayValue name="SolutionDirectory"/>
</div>

<div class="parameter">
  Other command line arguments: <props:displayValue name="Options"/>
</div>