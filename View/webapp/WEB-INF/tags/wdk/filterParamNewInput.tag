<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>

<%--
Provides form input element for a given FilterParamParam.
--%>

<%@ attribute name="qp"
              type="org.gusdb.wdk.model.jspwrap.FilterParamNewBean"
              required="true"
              description="parameter name"
%>

<%@ attribute name="layout"
              required="false"
              description="parameter name"
%>

<c:set var="qP" value="${qp}"/>
<c:set var="pNam" value="${qP.name}"/>
<c:set var="dependedParams" value="${qP.dependedParamNames}"/>
<c:if test="${dependedParams != null}">
  <c:set var="dependedParam" value="${dependedParams}" />
</c:if>

  <%-- Used by LegacyParamController --%>
  <c:set var="questionParamValues">
    {
    <c:forEach items="${requestScope.wdkQuestion.params}" var="questionParam" varStatus="loop">
      "${questionParam.name}": "${fn:replace(questionParam.stableValue, '"', '\\"')}" <c:if test="${loop.last eq false}">,</c:if>
    </c:forEach>
    }
  </c:set>

  <c:set var="props">
    {
      "paramName": "${qP.name}",
      "questionName": "${requestScope.wdkQuestion.urlSegment}",
      "paramValues": ${questionParamValues}
    }
  </c:set>


<%-- FIXME change data-name to data-display-name --%>
<%-- display the param as an advanced filter param --%>
<div class="param filter-param"
    dependson="${dependedParam}"
    name="${pNam}"
    data-type="filter-param-new"
    data-controller="wdk.clientAdapter"
    data-name="LegacyParamController"
    data-props="${fn:escapeXml(props)}"

>
</div>
