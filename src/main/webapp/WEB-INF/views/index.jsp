<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

	<head>
		<jsp:include page="layouts/_head.jsp"/>
	</head>

  <body>

	<jsp:include page="layouts/_loginnavbar.jsp"/>

    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">

		<jsp:include page="shared/_flash.jsp"/>

        <h1>Welcome to XYZ!</h1>
        <p>Jumbotron description.</p>
        <p><a class="btn btn-primary btn-lg" href="signup" role="button">Sign up now! &raquo;</a></p>
        <p></p>
        <p><a href="twitter_login"><img src="/images/sign-in-with-twitter-gray.png"/></a></p>
      </div>
    </div>

    <div class="container">
      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-4">
          <h2>All digital</h2>
          <p>Sub description.</p>
          <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
        </div>
        <div class="col-md-4">
          <h2>Hosted</h2>
          <p>Sub description.</p>
          <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
       </div>
        <div class="col-md-4">
          <h2>Secure</h2>
          <p>Sub description.</p>
          <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
        </div>
      </div>

      <hr>

      <footer>
        <p>&copy; XYZ 2014</p>
      </footer>
    </div> <!-- /container -->


	<jsp:include page="layouts/_foot.jsp"/>
  </body>
</html>
