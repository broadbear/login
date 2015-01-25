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

		<div class="row">
			<aside class="col-sm-4">
				<section class="user_info">
					<h1>
						${model.name}
					</h1>
				</section>
			</aside>
		</div>
			
    </div> <!-- /container -->

	<jsp:include page="../layouts/_foot.jsp"/>

  </body>
</html>
