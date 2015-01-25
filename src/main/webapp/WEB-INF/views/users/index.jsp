<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

	<head>
		<jsp:include page="../layouts/_head.jsp"/>
	</head>

  <body>

	<jsp:include page="../layouts/_navbar.jsp"/>

    <div class="container">

		<jsp:include page="../shared/_flash.jsp"/>

		<h1>All Users</h1>

		<ul class="users">
			<c:forEach items="${model}" var="user">
				<li>
					<a href="${routes.getUserPath(user.id)}">${user.name}</a>
					<c:if test='${sessionHelper.hasPermission("update-user")}'>
						| <a href="${routes.getEditUserPath(user.id)}">Edit</a>
					</c:if>
					<c:if test='${sessionHelper.hasPermission("destroy-user")}'>
						| <span id="delete_span">Delete</span>
					</c:if>
				</li>
			</c:forEach>
		</ul>
		
		<a href="${routes.signupPath}">Create new</a>
				
    </div> <!-- /container -->

	<jsp:include page="../layouts/_foot.jsp"/>
	<script>
		$(function() {
			$('#delete_span').on('click', function() {
				sendDelete(123);
			});
		});
		
		function sendDelete(id) {
			alert('click!');
			$.ajax({
				url: '/rest/users/' + id,
				type: 'DELETE',
				success: function(result) {
					// TODO: 
				}
			});
		}
	</script>
	
  </body>
</html>
