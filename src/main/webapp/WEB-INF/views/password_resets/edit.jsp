<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
	<jsp:include page="../layouts/_head.jsp"/>
</head>

<body>

	<jsp:include page="../layouts/_nologinnavbar.jsp"/>

	<div class="container">

		<jsp:include page="../shared/_flash.jsp"/>

		<h1>Reset password</h1>
		
		<div class="row">
			<div class="col-sm-6 col-sm-offset-3">
		
				<form role="form" action="${routes.getPasswordResetPath(model.resetToken)}" method="post">
					<jsp:include page="../shared/_errors.jsp"/>
					
					<input type="hidden" name="email" value="${model.email}">
		
			        <div class="form-group">
						<label for="inputPassword" class="sr-only">Password</label>
						<input type="password" id="inputPassword" name="password" class="form-control" 
								placeholder="Password" required> 
					</div>
		
			        <div class="form-group">
						<label for="inputConfirmation" class="sr-only">Confirmation</label>
						<input type="password" id="inputPasswordConfirmation" name="passwordConfirmation" class="form-control" 
								placeholder="Confirmation" required>
					</div>
							
					<button class="btn btn-lg btn-primary btn-block" type="submit">Update my password</button>
				</form>

			</div>
		</div>

	</div>
	<!-- /container -->


	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>
</body>
</html>
