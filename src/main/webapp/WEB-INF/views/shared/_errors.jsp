<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<c:if test="${model != null && model.errors.size() > 0}">
		<div id="error_explanation">
			<div class="alert alert-danger" role="alert">
				The form contains ${model.errors.size()} error(s).
			</div>
			<ul>
				<c:forEach items="${model.errors}" var="error">
					<li>${error.message}</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
