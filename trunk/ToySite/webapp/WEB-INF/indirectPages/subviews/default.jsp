<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="misc" uri="http://www.gusdb.org/taglibs/wdk-misc-0.1" %>

<p>wibble

<misc:table>

  <misc:tr>
    <c:forEach var="row" items="${rivl.columnNames}">
      <c:forEach var="column" items="${row}">
	    <th><b>${rivl.displayName[column]}</b></th>
	  </c:forEach>
    </c:forEach>
  </misc:tr>

  <c:forEach var="row" items="${rivl}">
    <misc:tr>
      <c:forEach var="columnName" items="${rivl.columnNames}">
	    <td><misc:multiType value="${row[columnName]}" /></td>
	  </c:forEach>
    </misc:tr>
  </c:forEach>
</misc:table>

