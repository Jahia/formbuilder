<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="nodeId" value="${currentNode.identifier}"/>
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
           value="${not empty sessionScope.formError ? sessionScope.formDatas[currentNode.name][0] : ''}" readonly="readonly"/>
    <label class="left">${fn:escapeXml(currentNode.properties['jcr:title'].string)}</label>
    <span>
        <jsp:useBean id="now" class="java.util.Date" />
        <fmt:formatDate var="year" value="${now}" pattern="yyyy" />
        <select ${disabled} name="year">
                  <option><fmt:message key="label.year"/></option>
                  <c:forEach var="i" begin="0" end="${year - 1900}" step="1">
                      <option>${year - i}</option>
                  </c:forEach>
        </select>
        <select ${disabled} name="month">
            <option><fmt:message key="label.month"/></option>
                  <option>1</option>
                  <option>2</option>
                  <option>3</option>
                  <option>4</option>
                  <option>5</option>
                  <option>6</option>
                  <option>7</option>
                  <option>8</option>
                  <option>9</option>
                  <option>10</option>
                  <option>11</option>
                  <option>12</option>
              </select>
        <select ${disabled} name="day">
            <option><fmt:message key="label.day"/></option>
            <option>1</option>
            <option>2</option>
            <option>3</option>
            <option>4</option>
            <option>5</option>
            <option>6</option>
            <option>7</option>
            <option>8</option>
            <option>9</option>
            <option>10</option>
            <option>11</option>
            <option>12</option>
            <option>13</option>
            <option>14</option>
            <option>15</option>
            <option>16</option>
            <option>17</option>
            <option>18</option>
            <option>19</option>
            <option>20</option>
            <option>21</option>
            <option>22</option>
            <option>23</option>
            <option>24</option>
            <option>25</option>
            <option>26</option>
            <option>27</option>
            <option>28</option>
            <option>29</option>
            <option>30</option>
            <option>31</option>
        </select>
    </span>
</p>