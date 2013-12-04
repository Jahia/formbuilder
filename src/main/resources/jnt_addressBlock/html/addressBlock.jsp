<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<template:addResources type="inlinejavascript">
<script type='text/javascript'>
$(function() {
   $('input[name="street"],input[name="street2"],input[name="city"],input[name="state"],input[name="zip"],input[name="country"]').change(function() {
        var x = $('input[name="street"]').val()+' '
                +$('input[name="street2"]').val()+' '
                +$('input[name="city"]').val()+' '
                +$('input[name="state"]').val()+' '
                +$('input[name="zip"]').val()+' '
                +$('input[name="country"]').val();
        $('#${currentNode.name}').val(x);
    });
});
</script>
</template:addResources>
<div>
<input ${disabled} type="text" id="${currentNode.name}" name="${currentNode.name}"
       value="${not empty sessionScope.formError ? sessionScope.formDatas[currentNode.name][0] : ''}" readonly="readonly"/>
<table cellpadding="4">
	<tr>
    	<td><label for="street"><fmt:message key="address.street"/></label></td>
        <td colspan="3"><input ${disabled} type="text" maxlength="50" size="40" name="street"></td>
	</tr>
	<tr>
		<td><label for="street2"><fmt:message key="address.street2"/></label></td>
        <td colspan="3"><input ${disabled} type="text" maxlength="50" size="40" name="street2"></td>
	</tr>
	<tr>
    	<td><label for="city"><fmt:message key="address.city"/></label></td>
        <td><input ${disabled} type="text" maxlength="40" size="18" name="city"></td>
        <td align="right"><label for="state"><fmt:message key="address.state"/></label></td>
        <td align="right"><input ${disabled} type="text" maxlength="15" size="6" name="state"></td>
	</tr>
	<tr>
    	<td><label for="zip"><fmt:message key="address.zip"/></label></td>
        <td><input ${disabled} type="text" maxlength="10" size="6" name="zip"></td>
        <td align="right"><label for="country"><fmt:message key="address.country"/></label></td>
        <td align="right"><input ${disabled} type="text" maxlength="40" size="6" name="country"></td>
	</tr>
</table>
</div>