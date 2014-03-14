<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="nodeId" value="${currentNode.identifier}"/>
<c:set var="previousDate" value="${sessionScope.formDatas[currentNode.name][0]}"/>
<c:if test="${not empty previousDate}">
    <c:set var="splittedDate" value="${fn:split(sessionScope.formDatas[currentNode.name][0], '-')}"/>
    <c:set var="previousYear" value="${splittedDate[0]}"/>
    <c:set var="previousMonth" value="${splittedDate[1]}"/>
    <c:set var="previousDay" value="${splittedDate[2]}"/>
</c:if>

<template:addResources>
<script type="text/javascript">
$(function() {
    $('#birthdate-${nodeId} select[name="year"],#birthdate-${nodeId} select[name="month"],#birthdate-${nodeId} select[name="day"]').change(function() {
        var x = $('#birthdate-${nodeId} select[name="year"]').val()+'-'+$('#birthdate-${nodeId} select[name="month"]').val()+'-'+$('#birthdate-${nodeId} select[name="day"]').val();
        $('#birthdate-value-${nodeId}').val(x);
    });
});
</script>
</template:addResources>

<p class="field" id="birthdate-${nodeId}">
    <input ${disabled} type="text" style="display:none" id="birthdate-value-${nodeId}" name="${currentNode.name}"
           value="${date}" readonly="readonly"/>
    <label class="left">${fn:escapeXml(currentNode.properties['jcr:title'].string)}</label>
    <span>
        <jsp:useBean id="now" class="java.util.Date" />
        <fmt:formatDate var="year" value="${now}" pattern="yyyy" />
        <select ${disabled} name="year">
                  <option><fmt:message key="label.year"/></option>
                  <c:forEach var="i" begin="0" end="${year - 1900}" step="1">
                      <option <c:if test="${not empty previousYear && previousYear eq year - i}">selected="selected"</c:if>>${year - i}</option>
                  </c:forEach>
        </select>
        <select ${disabled} name="month">
            <option><fmt:message key="label.month"/></option>
            <c:forEach var="i" begin="1" end="12" step="1">
                <option <c:if test="${not empty previousMonth && previousMonth eq i}">selected="selected"</c:if>>${i}</option>
            </c:forEach>
              </select>
        <select ${disabled} name="day">
            <option><fmt:message key="label.day"/></option>
            <c:forEach var="i" begin="1" end="31" step="1">
                <option <c:if test="${not empty previousDay && previousDay eq i}">selected="selected"</c:if>>${i}</option>
            </c:forEach>
        </select>
    </span>
</p>