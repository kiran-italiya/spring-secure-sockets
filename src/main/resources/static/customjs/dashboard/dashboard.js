let stompService = new SocketService();
let userNotification = null;
let broadcastNotification = null;
stompService.connect(function () {
    userNotification = stompService.subscribe('/user/queue/notification', function (message) {
        console.log(message.body);
        if (message.body) {
            // handle message
            let response = JSON.parse(message.body);
            if (response.type === 'PUSH_NOTIFICATIONS') {
                let ids = [];
                let str = "";
                for (let notification of response.data) {
                    ids.push(notification.notificationId);
                    str += notification.payload + "<br>";
                }
                if (response.data.length > 0) {
                    bannerNotification("error", str, "Notifications");
                    // mark read
                    stompService.send('/ws/notification/post/mark_read', {}, JSON.stringify(ids.splice(0, 1)));
                }
            }
        }
    });
    broadcastNotification = stompService.subscribe('/topic/notification', function (message) {
        console.log(message.body);
    });
});

let userEventBtn = document.querySelector("#user_event_btn");
let broadcastBtn = document.querySelector("#broadcast_btn");

userEventBtn.addEventListener("click", (e) => {
    ajaxReq("GET", "/dashboard/event/user", {
        type : "toast",
        message : "hello"
        }, 2,
        () => {
            bannerNotification("info", "", "Success");
        },
        () => {
            bannerNotification("info", "", "error");
        });
});

broadcastBtn.addEventListener("click", (e) => {
    ajaxReq("GET", "/dashboard/event/broadcast", {
            type : "toast",
            message : "hello"
        }, 2,
        () => {
            bannerNotification("info", "", "Success");
        },
        () => {
            bannerNotification("info", "", "error");
        });
});