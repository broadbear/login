
<!DOCTYPE html>
<html lang="en">

	<head>
		<jsp:include page="../layouts/_head.jsp"/>
	</head>

  <body>

	<jsp:include page="../layouts/_loginnavbar.jsp"/>

    <div class="container">
 
 		<jsp:include page="../shared/_flash.jsp"/>
 
        <h1>Forgot password</h1>
        
        <div class="row">
        	<div class="col-sm-6 col-sm-offset-3">

				<form role="form" action="${routes.getPasswordResetsPath()}" method="post">
					<jsp:include page="../shared/_errors.jsp"/>
		        
					<div class="form-group">
						<label for="inputEmail" class="sr-only">Email address</label>
						<input type="email" id="inputEmail" name="email" class="form-control" placeholder="Email address" required autofocus>
					</div>
			        
					<button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
				</form>

			</div>
		</div>
    </div> <!-- /container -->


    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
