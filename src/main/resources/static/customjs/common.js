const bannerNotification = function (toastrType, message, title) {
    switch (toastrType.toLowerCase()) {
        case "info":
            // toastr.info(message, title);
            // swal({ title: title, text: message, icon:
            // serverurlCommon+"/mudralogin/assets/alert_custom/alert_icon/t-warning.png",
            // buttons: { confirm: { className: "themeColor"}} });
            $.toast({
                heading: title,
                text: message,
                position: 'top-right',
                showHideTransition: 'plain',
                loaderBg: '#3b98b5',
                icon: 'info',
                hideAfter: 3000,
                stack: 1
            });
            break;
        case "success":
            // toastr.success(message, title);
            // swal({ title: title, text: message, icon:
            // serverurlCommon+"/mudralogin/assets/alert_custom/alert_icon/t-success.png",
            // buttons: { confirm: { className: "themeColor"}} });
            $.toast({
                heading: title,
                text: message,
                position: 'top-right',
                showHideTransition: 'plain',
                loaderBg: '#5ba035',
                icon: 'success',
                hideAfter: 3000,
                stack: 1
            });
            break;
        case "error":
            // toastr.error(message, title);
            // swal({ title: title, text: message, icon:
            // serverurlCommon+"/mudralogin/assets/alert_custom/alert_icon/t-remove.png",
            // buttons: { confirm: { className: "themeColor"}} });
            $.toast({
                heading: title,
                text: message,
                position: 'top-right',
                showHideTransition: 'plain',
                loaderBg: '#bf441d',
                icon: 'error',
                hideAfter: 3000,
                stack: 1
            });
            break;
        case "warning":
            // toastr.warning(message, title);
            // swal({ title: title, text: message, icon:
            // serverurlCommon+"/mudralogin/assets/alert_custom/alert_icon/t-warning.png",
            // buttons: { confirm: { className: "themeColor"}} });
            $.toast({
                heading: title,
                text: message,
                position: 'top-right',
                showHideTransition: 'plain',
                loaderBg: '#da8609',
                icon: 'warning',
                hideAfter: 3000,
                stack: 1
            });
            break;
    }
};

const ajaxReq = function (method, url, data, sendAs, successCallback, failureCallback) {
    let requestData = null;
    let cType = true;
    let pData = true;

    if (sendAs == 1) {
        requestData = JSON.stringify(data);
        cType = "application/json";
    } else {
        requestData = data;
        cType = "application/x-www-form-urlencoded; charset=UTF-8";
    }

    if (sendAs == 3) {
        cType = false;
        pData = false;
    }
    $.ajax({
        method: method,
        url: encodeURI(url),
        data: requestData,
        contentType: cType,
        processData: pData
    })
        .done(successCallback)
        .fail(failureCallback);
};


class SocketService {

    stompClient = null;

    constructor() {
        let socket = new SockJS('/secured/notifications');
        this.stompClient = Stomp.over(socket);
    }

    connect(perform) {
        this.stompClient.connect({}, function (frame) {
            console.log('Notification Socket Connected');
            if (typeof perform === 'function') {
                perform(frame);
            }
        });
    }

    subscribe(path, perform) {
        if (this.stompClient && this.stompClient.connected) {
            return this.stompClient.subscribe(path, function (message) {
                if (typeof perform === 'function') {
                    perform(message);
                }
            });
        }
    }

    send(path, headers, payload) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send(path, headers, payload);
        }
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
    }
}