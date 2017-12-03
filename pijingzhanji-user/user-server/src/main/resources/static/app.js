var stompClient = null;


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/pijingzhanji');
    stompClient = Stomp.over(socket);
    var headers = {
        Authorization: $("#token").val()
    };
    stompClient.connect(headers, function (frame) {
        $("#sendDiv").removeAttr("hidden");
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (message) {
            showGreeting(message.body);
        });
    }, function (error) {
        $("#error").html(error.headers.message);
        $("#errorDiv").removeAttr("hidden");
    });
}

function disconnect() {
    $("#sendDiv").attr("hidden", true);
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, $("#name").val());
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
    $("#login").click(function () {
        var username = $("#username").val();
        var password = $("#password").val();
        var params = {
            username: username,
            password: password
        };

        $.ajax({
            type: "post",
            url: "authentication",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(params),
            success: function (message) {
                console.log(message);
                if (message && message.statusCode !== '200') {
                    $("#error").html(message.statusMessage);
                    $("#errorDiv").removeAttr("hidden");
                    $("#tokenDiv").attr("hidden", true);
                    return;
                }
                $("#errorDiv").attr("hidden", true);
                $("#token").val(message.responseContent.token);
                $("#tokenDiv").removeAttr("hidden");
            }
        });

    });


});

