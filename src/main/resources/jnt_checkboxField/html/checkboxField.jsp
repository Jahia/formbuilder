<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<c:set var="required" value=""/>
<c:if test="${jcr:hasChildrenOfType(currentNode, 'jnt:required')}">
    <c:set var="required" value="required"/>
</c:if>

<label class="left" for="${currentNode.name}">${currentNode.properties['jcr:title'].string}</label>
<div class="formMarginLeft">
<template:addResources type="inlinejavascript">
<script type='text/javascript'>
$(function() {
   $('input[name="${currentNode.name}box"]').change(function() {
       var values = $("input[name='${currentNode.name}box']:checked").map(function() {
           return this.value;
       }).get();
        $('#${currentNode.name}').val(values.join(" "));
    });
});
</script>
</template:addResources>
<input ${disabled} type="text" style="display:none" id="${currentNode.name}" name="${currentNode.name}"
   value="${sessionScope.formDatas[currentNode.name][0]}" readonly="readonly"/>

<c:forEach items="${jcr:getNodes(currentNode,'jnt:formListElement')}" var="option">
    <c:set var="isChecked" value="false"/>
    <c:forEach items="${fn:split(sessionScope.formDatas[currentNode.name][0], ' ')}" var="checked">
        <c:if test="${option.name eq checked}">
            <c:set var="isChecked" value="true"/>
        </c:if>
        </c:forEach>
    <label>
        <input ${disabled} type="checkbox" ${required} class="${required}" name="${currentNode.name}box" value="${option.name}" <c:if test="${isChecked eq 'true'}">checked="true"</c:if>
                           <c:if test="${required eq 'required'}">onclick='$("input:checkbox[name=${currentNode.name}box]:checked").size()==0?$("input:checkbox[name=${currentNode.name}box]").prop("required", true):$("input:checkbox[name=${currentNode.name}box]").removeAttr("required")'</c:if> />
        ${option.properties['jcr:title'].string}</label>
</c:forEach>
<c:if test="${renderContext.editMode}">
    <p><fmt:message key="label.listOfOptions"/> </p>
    <ol>
        <c:forEach items="${jcr:getNodes(currentNode,'jnt:formListElement')}" var="option">
            <li><template:module node="${option}" view="default" editable="true"/></li>
        </c:forEach>
    </ol>
    <div class="addvalidation">
        <span><fmt:message key="label.addListOption"/> </span>
        <template:module path="*" nodeTypes="jnt:formListElement"/>
    </div>

    <p><fmt:message key="label.listOfValidation"/> </p>
    <ol>
    <c:forEach items="${jcr:getNodes(currentNode,'jnt:formElementValidation')}" var="formElement" varStatus="status">
        <li><template:module node="${formElement}" view="edit"/></li>
    </c:forEach>
    </ol>
    <div class="addvalidation">
        <span><fmt:message key="label.addValidation"/> </span>
        <template:module path="*" nodeTypes="jnt:formElementValidation"/>
    </div>
</c:if>
</div>
