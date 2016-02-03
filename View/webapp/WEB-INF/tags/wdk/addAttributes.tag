<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>

<jsp:useBean id="idgen" class="org.gusdb.wdk.model.jspwrap.NumberUtilBean" scope="application" />

<%@ attribute name="wdkAnswer"
              required="true"
              type="org.gusdb.wdk.model.jspwrap.AnswerValueBean"
              description="the AnswerValueBean for this attribute list"
%>

<%@ attribute name="commandUrl"
              required="true"
              type="java.lang.String"
%>
<c:set var="questionName" value="${wdkStep.question.fullName}" />
<c:set var="recordClassName" value="${wdkAnswer.recordClass.fullName}" />
<!--  questionName: ${questionName} -->
<!-- recordClassName: ${recordClassName} -->
<span class="ontology-checkbox-tree-setup"
      data-question-name="${questionName}"
      data-record-class-name="${recordClassName}"
      data-controller="wdk.attributeCheckboxTree.setupCheckboxTree"></span>
<input type="button" onclick="wdk.resultsPage.openAttributeList(this);" class="addAttributesButton" value="Add Columns" />

<div class="attributesList formPopup" title="Select Columns">

  <div class="attributesFormWrapper">
    <form name="addAttributes" action="${commandUrl}">
      <input type="hidden" name="command" value="update"/>
    <div class="formButtonPanel">
      <input type="submit" value="Update Columns"/>
    </div>
    <div id="newAttributeCheckboxTree"></div>
   
    <div class="formButtonPanel">
      <input type="submit" value="Update Columns"/>
    </div>
  </form>
  </div>

</div>  <%--   class="attributesList formPopup"  --%>
