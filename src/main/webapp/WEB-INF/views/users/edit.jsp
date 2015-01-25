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
		
		<h1>Update your profile</h1>
		<div class="row">
			<div class="col-sm-6 col-sm-offset-3">

				<form role="form" action="/rest/users/${model.id}" method="post">
					<jsp:include page="../shared/_errors.jsp"/>
					
					<div class="form-group">
						<label for="inputName" class="sr-only">Name</label> 
						<input type="text" id="inputName" name="name" class="form-control" 
								placeholder="Name" value="${model.name}" required> 
					</div>
					
					<div class="form-group">
						<label for="inputEmail" class="sr-only">Email address</label> 
						<input type="email" id="inputEmail" name="email" class="form-control" 
								placeholder="Email address" value="${model.email}" required> 
					</div>
							
					<div class="form-group">
						<label for="inputPassword" class="sr-only">Password</label> 
						<input type="password" id="inputPassword" name="password" class="form-control" 
								placeholder="Password"> 
					</div>
							
					<div class="form-group">
						<label for="inputConfirmation" class="sr-only">Confirmation</label> 
						<input type="password" id="inputPasswordConfirmation" name="passwordConfirmation" class="form-control" 
								placeholder="Confirmation">
					</div>
																		
					<c:if test="${sessionHelper.hasPermission('set-user-role')}">
						<div class="form-group">
							<label for="selectRole">Role</label>
							<select id="selectRole" name="roleId" class="form-control">
								<c:forEach var="role" items="${sessionHelper.allowedRoles}">
									<c:choose>
										<c:when test="${sessionHelper.currentUser.roleId == role.lesserId}">
											<option selected="selected" value="${role.lesserId}">${role.lesserId}</option>								
										</c:when>
										<c:otherwise>
											<option value="${role.lesserId}">${role.lesserId}</option>								
										</c:otherwise>
									</c:choose>
								</c:forEach>								
							</select>
						</div>
					</c:if>

					<c:if test="${sessionHelper.hasPermission('set-user-group')}">
						<div class="form-group">
							<label for="selectGroup">Group</label>
							<select id="selectGroup" name="groupId" class="form-control">
								<c:forEach var="group" items="${sessionHelper.allowedGroups}">
									<c:choose>
										<c:when test="${sessionHelper.currentUser.groupId == group.lesserId}">
											<option selected="selected" value="${group.lesserId}">${group.lesserId}</option>								
										</c:when>
										<c:otherwise>
											<option value="${group.lesserId}">${group.lesserId}</option>								
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</div>
					</c:if>
							
					<button class="btn btn-lg btn-primary btn-block" type="submit">Save changes</button>
				</form>
			
			</div>
		</div>

    </div> <!-- /container -->


	<jsp:include page="../layouts/_foot.jsp"/>
  </body>
</html>
