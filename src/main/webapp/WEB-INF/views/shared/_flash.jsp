<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach items="${sessionHelper.flash.messages}" var="msg">
	<div class="alert alert-${msg.type}">${msg.text}</div>
</c:forEach>
      
