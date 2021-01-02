<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login</title>
    <meta charset="UTF-8">
    <!-- <meta charset="UTF-8"> -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!--===============================================================================================-->
    <link rel="icon" type="image/png" href="${sessionScope.server_url}/assets/images/icons/favicon.ico"/>
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css"
          href="${sessionScope.server_url}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css"
          href="${sessionScope.server_url}/assets/fonts/font-awesome-4.7.0/css/font-awesome.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css"
          href="${sessionScope.server_url}/assets/fonts/iconic/css/material-design-iconic-font.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="${sessionScope.server_url}/assets/vendor/animate/animate.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css"
          href="${sessionScope.server_url}/assets/vendor/css-hamburgers/hamburgers.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css"
          href="${sessionScope.server_url}/assets/vendor/animsition/css/animsition.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="${sessionScope.server_url}/assets/vendor/select2/select2.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css"
          href="${sessionScope.server_url}/assets/vendor/daterangepicker/daterangepicker.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="${sessionScope.server_url}/assets/css/util.css">
    <link rel="stylesheet" type="text/css" href="${sessionScope.server_url}/assets/css/main.css">
    <!--===============================================================================================-->
</head>
<body>

<div class="limiter">
    <div class="container-login100">
        <div class="wrap-login100 p-t-85 p-b-20">
            <div class="login100-form validate-form">
					<span class="login100-form-title p-b-70">
						Welcome
					</span>
                <span class="login100-form-avatar">
						<img src="${sessionScope.server_url}/assets/images/avatar-01.jpg" alt="AVATAR">
					</span>
                <form action="${pageContext.request.contextPath}/process-login" autocomplete="off" id="login-form" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                    <div class="wrap-input100 validate-input m-t-85 m-b-35" data-validate="Enter username">
                        <input class="input100" type="text" id="username" name="username">
                        <span class="focus-input100" data-placeholder="Username"></span>
                    </div>

                    <div class="wrap-input100 validate-input m-b-50" data-validate="Enter password">
                        <input class="input100" type="password" id="password" name="password">
                        <span class="focus-input100" data-placeholder="Password"></span>
                    </div>
                </form>

                <div class="container-login100-form-btn">
                    <a class="login100-form-btn" id="process-login" href="#">
                        Login
                    </a>
                </div>

                <div class="container-login100-form-btn" id="msg">
                    <span class="errors">${errormsg}</span>
                </div>

                <ul class="login-more p-t-190">
                    <li class="m-b-8">
							<span class="txt1">
								Forgot
							</span>

                        <a href="#" class="txt2">
                            Username / Password?
                        </a>
                    </li>

                    <li>
							<span class="txt1">
								Don’t have an account?
							</span>

                        <a href="#" class="txt2">
                            Sign up
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>


<div id="dropDownSelect1"></div>

<script>
    const contextPath = "${pageContext.request.contextPath}";
    const resourcePath = "${sessionScope.server_url}";
</script>

<!--===============================================================================================-->
<script src="${sessionScope.server_url}/assets/vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
<script src="${sessionScope.server_url}/assets/vendor/animsition/js/animsition.min.js"></script>
<!--===============================================================================================-->
<script src="${sessionScope.server_url}/assets/vendor/bootstrap/js/popper.js"></script>
<script src="${sessionScope.server_url}/assets/vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
<script src="${sessionScope.server_url}/assets/vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
<script src="${sessionScope.server_url}/assets/vendor/daterangepicker/moment.min.js"></script>
<script src="${sessionScope.server_url}/assets/vendor/daterangepicker/daterangepicker.js"></script>
<!--===============================================================================================-->
<script src="${sessionScope.server_url}/assets/vendor/countdowntime/countdowntime.js"></script>
<!--===============================================================================================-->
<script src="${pageContext.request.contextPath}/customjs/login/process-login.js" type="text/javascript"></script>

</body>
</html>