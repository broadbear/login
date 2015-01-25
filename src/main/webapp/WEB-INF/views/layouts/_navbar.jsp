 	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!-- Fixed navbar -->
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="${routes.usersPath}"><img src="/images/generic-logo-md.png"/></a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="${routes.usersPath}">Users</a></li>
            <li><a href="#">Help</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Account <span class="caret"></span></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="${routes.getUserPath(sessionHelper.currentUser.id)}">Profile</a></li>
                <li><a href="${routes.getEditUserPath(sessionHelper.currentUser.id)}">Settings</a></li>
                <li class="divider"></li>
                <li><a href="${routes.logoutPath}">Logout</a></li>
              </ul>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
				<c:if test='${sessionHelper.hasPermission("view-all-users")}'>
					<li><a href="${routes.usersPath}">Admin</a></li>
				</c:if>
			</ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>