<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard</title>
    <meta charset="UTF-8">
    <!-- <meta charset="UTF-8"> -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/vendor/jquery-toast/css/jquery.toast.min.css">

</head>
<body>

<button id="user_event_btn" type="button" class="btn btn-primary">Generate Event</button>
<button id="broadcast_btn" type="button" class="btn btn-primary">Broadcast</button>


<script src="${sessionScope.server_url}/assets/vendor/jquery/jquery-3.2.1.min.js"></script>
<script src="${sessionScope.server_url}/assets/vendor/jquery-toast/js/jquery.toast.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

<script src="${pageContext.request.contextPath}/customjs/common.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/customjs/dashboard/dashboard.js" type="text/javascript"></script>

</body>
</html>
