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

		<h1>Please sign in</h1>

		<div class="row">
			<div class="col-sm-6 col-sm-offset-3">
		  
				<form role="form" action="login" method="post">
					<jsp:include page="../shared/_errors.jsp"/>
	      
					<div class="form-group">
						<label for="inputEmail" class="sr-only">Email address</label>
				        <input type="email" id="inputEmail" name="email" class="form-control" placeholder="Email address" required autofocus>
					</div>
		        
		        	<div class="form-group">
						<label for="inputPassword" class="sr-only">Password</label>
						<input type="password" id="inputPassword" name="password" class="form-control" placeholder="Password" required>
		         		<a href="password_resets/new">(forgot password)</a>
		        	</div>
		        
		        	<div class="checkbox">
		          		<label>
		            		<input type="checkbox" value="1" name="rememberMe"> Remember me
		          		</label>
		        	</div>
		        	<button class="btn btn-lg btn-primary btn-block" type="submit">Log in</button>
	      		</form>
	     		<p>New user? <a href="signup">Sign up now!</a></p>
		  		  
		</div>
	  </div>


    </div> <!-- /container -->


    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
