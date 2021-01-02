!function ($) {

    function handlePreloader() {
        if ($('.preloader').length) {
            $('.preloader').delay(500).fadeOut(500);
        }
    }

    /* When document is loading, do */
    $(window).on('load', function () {
        handlePreloader();
    });

    const EMAIL_REGEX = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    $('#username').focus();
    let status = undefined;
    let responseText = undefined;
    let loginsuccess = false;
    const VALIDATE_USER_DETAILS = (username, password) => {
        if (!username) {
            $("#username")[0].reportValidity();
            $("#username")[0].setCustomValidity("Please enter Emp/Ref Id.");
            return null;
        }

        if (!password) {
            $("#password")[0].reportValidity();
            $("#password")[0].setCustomValidity("Please enter password.");
            return null;
        }

        return {
            username: username,
            password: password
        };
    };

    const PROCESS_LOGIN = () => {
        $("#msg").css("display", "none");
        const username = $("#username").val();
        const password = $("#password").val();
        const data = VALIDATE_USER_DETAILS(username, password);
        if (data) {
            $(".preloader").show();
            $(`#login-form`)[0].submit();
        }else{
            return false;
        }
    };

    $(document).ajaxSend(() => {
        $('.preloader').show();
    });

    $(document).ajaxComplete((event, xhr, setting) => {
        $(".preloader").hide();
    });

    $("#process-login").click((e) => {
        PROCESS_LOGIN();
    });
    $(document).keypress(function (e) {
        if (e.which == 13) {
            PROCESS_LOGIN();
        }
    });

    $('#password + .fa').on('click', function () {
        $(this).toggleClass('fa-eye-slash').toggleClass('fa-eye');
        $('#password').togglePassword();
    });

    $("#user-module-select").change((e) => {
        const selectedModuleId = $(e.currentTarget).val();
        if (selectedModuleId) {
            location.href = encodeURI(contextPath + "/dashboard/" + selectedModuleId);
        }
    });

    $("#forgot_password_link").click((e) => {
        $("#emailid").val("");
        $("#msg>span").text("");
        $("#login_div").hide();
        $("#forgot_password_div").show();
    });

    $("#back_to_login").click((e) => {
        $("#login_div").show();
        $('#msg2>span').text("");
        $("#forgot_password_div").hide();
    });

    function PROCESS_RESET() {
        const email = $("#emailid").val();
        if (email && EMAIL_REGEX.test(email)) {
            $("#recover-pass-btn").attr("disabled", "disabled");
            $.ajax({
                method: "POST",
                url: encodeURI(`${contextPath}/request-password-reset`),
                data: {email: email},
                contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            })
                .done((data, textStatus, jqXHR) => {
                    let resp = data.responseText || jqXHR.responseText;
                    if (resp) {
                        resp = JSON.parse(resp);
                        if (resp.iserror == "N") {
                            $(".loginforms").empty().html(`
									<div class="pl-1 pr-1 pb-1">
										<h4 class="mb-0">Mail Sent!</h4>
										<p class="text-justify p-1">An email has been sent to ${email} with instructions on how to reset your password.</p>
									</div>`);
                        } else {
                            $("#recover-pass-btn").removeAttr("disabled");
                            $("#msg2").css("display", "block");
                            $("#msg2>span").text(resp.errorString);
                        }
                    } else {
                        $("#recover-pass-btn").removeAttr("disabled");
                        $("#msg2").css("display", "block");
                        $("#msg2.errors").text("Something went wrong on server-side");
                    }
                })
                .fail((data, textStatus, jqXHR) => {
                    $("#recover-pass-btn").removeAttr("disabled");
                    $("#msg").css("display", "block");
                    $("#msg>span").text(data.responseText || jqXHR.responseText);
                })
        } else {
            $("#emailid")[0].setCustomValidity("Enter a valid email address.");
            $("#emailid")[0].reportValidity();
        }
    }

    $("#recover-pass-btn").click((e) => {
        PROCESS_RESET();
    });

    $(document).keypress(function (e) {
        if (e.which == 13) {
            if ($("#login_div").is(':visible')) {
                PROCESS_LOGIN();
            }
            if ($("#forgot_password_div").is(':visible')) {
                PROCESS_RESET();
            }
        }
    });
}($);