<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bean" uri="http://jakarta.apache.org/struts/tags-bean" %>
<%@ taglib prefix="wdk" tagdir="/WEB-INF/tags/wdk" %>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>


<%@ attribute name="stages"
              required="false"
              description="Total number of stages in the current wizard"
%>
<%@ attribute name="stage"
              required="false"
              type="org.gusdb.wdk.model.wizard.Stage"
              description="The current stage"
%>
<%@ attribute name="stageNumber"
              required="true"
              description="The current stage Number"
%>

<c:set var="curr" value="${stageNumber}"/>
<c:set var="stageCount" value="${stages}"/>

<c:if test="${fn:length(stageCount) == 0}"><c:set var="stageCount" value="1"/></c:if>
<div id="process-crumbs">
	<c:forEach begin="1" end="${stageCount}" var="i">
		<c:set var="stepImage" value="step_bw_${i}.png"/>
		<c:set var="dispClass" value=""/>
		<c:if test="${i == curr}">
			<c:set var="stepImage" value="step${i}.png"/>
			<c:set var="dispClass" value="active"/>
		</c:if>
		<div id="pStep_${i}" class="${dispClass}">
			<img class="step-image" src="<c:url value="wdk/images/${stepImage}"/>"/>
                        <c:choose>
				<c:when test="${stage != null}">
					<p>${stage.display}</p>
				</c:when>
				<c:otherwise>
					<p>Please select a Wizard</p>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${i > stageCount}">
			<img class="transition" id="${i}-${i+1}" src="<c:url value="wdk/images/sStepTrans.png"/>"/>
		</c:if>
	</c:forEach>
</div>
