<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr>
  <th><label for="Targets">Targets:</label></th>
  <td>
    <props:textProperty name="Targets" className="longField"/>
    <span class="smallNote">Target(s) separated by space</span>
    <span class="error" id="error_Targets"></span>
  </td>
</tr>

<tr>
  <th><label for="Properties">Properties:</label></th>
  <td>
    <props:textProperty name="Properties" className="longField"/>
    <span class="smallNote">Build properties in &lt;name>=&lt;value> form separated by space</span>
  </td>
</tr>

<tr>
  <th><label>Artifacts:</label></th>
  <td>
    If you wish to expose artifacts produced by AnFake add '.artifacts' into your 'Artifact Paths' under 'General Settings' of your build configuration.
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="Script">Build script path:</label></th>
  <td>
    <props:textProperty name="Script" className="longField"/>
    <bs:vcsTree fieldId="Script"/>
    <span class="smallNote">Relative path to the build script (build.fsx or build.csx)</span>
    <span class="error" id="error_Script"></span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="AnFakePath">AnFake working path:</label></th>
  <td>
    <props:textProperty name="AnFakePath" className="longField"/>
    <bs:vcsTree fieldId="AnFakePath"/>
    <span class="smallNote">Relative path to the anf.cmd</span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="Mono">Run with Mono:</label></th>
  <td>
    <props:checkboxProperty name="Mono"/>
  </td>
</tr>