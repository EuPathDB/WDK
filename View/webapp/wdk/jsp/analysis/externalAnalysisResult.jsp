<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions">
  <jsp:directive.page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"/>

  <!-- Generate the download URL of the data generated by this plugin -->
  <c:set var="scheme" value="${pageContext.request.scheme}"/>
  <c:set var="serverName" value="${pageContext.request.serverName}"/>
  <c:set var="serverPort" value="${pageContext.request.serverPort}"/>
  <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
  <c:set var="urlBase" value="${scheme}://${serverName}:${serverPort}${contextPath}"/>
  <c:url var="downloadUrl" value="${urlBase}/stepAnalysisResource.do?analysisId=${analysisId}&amp;path=${viewModel.downloadPath}"/>
  <c:url var="propertiesUrl" value="${urlBase}${initParam.wdkServiceEndpoint}/users/current/steps/${stepId}/analyses/${analysisId}/properties?accessToken=${accessToken}"/>

  <!-- Add query params to iframe URL to be passed to external analysis tool (c:url takes care of encoding) -->
  <c:url var="iframeUrl" context="/" value="${viewModel.iframeBaseUrl}">
    <c:param name="contextHash" value="${contextHash}"/>
    <c:param name="dataUrl" value="${downloadUrl}"/>
    <c:param name="propertiesUrl" value="${propertiesUrl}"/>
  </c:url>

  <html>
    <body>
      <div style="text-align:center">
        <iframe style="border:0" src="${iframeUrl}" width="${viewModel.iframeWidth}" height="${viewModel.iframeHeight}"/>
      </div>
    </body>
  </html>

</jsp:root>