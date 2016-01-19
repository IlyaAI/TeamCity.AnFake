<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr>
  <th><label for="PackagesConfig">Path to packages.config (or .sln):</label></th>
  <td>
    <props:textProperty name="PackagesConfig" className="longField"/>
    <bs:vcsTree fieldId="PackagesConfig"/>
    <span class="smallNote">Relative path to the packages.config or .sln file</span>
    <span class="error" id="error_PackagesConfig"></span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="SourceUrl">Source Url(s):</label></th>
  <td>
    <props:textProperty name="SourceUrl" className="longField"/>
    <span class="smallNote">Packages source Url(s) separated by semicolon (if empty then nuget.org is used)</span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="SolutionDirectory">Solution directory:</label></th>
  <td>
    <props:textProperty name="SolutionDirectory" className="longField"/>
    <bs:vcsTree fieldId="SolutionDirectory"/>
    <span class="smallNote">Relative path to the directory containing .sln file file (if empty then checkout directory is used)</span>
  </td>
</tr>

<tr class="advancedSetting">
  <th><label for="Options">Other command line arguments:</label></th>
  <td>
    <props:multilineProperty name="Options" cols="58" linkTitle="Expand" rows="5"/>
  </td>
</tr>