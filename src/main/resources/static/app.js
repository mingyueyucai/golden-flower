var stompClient = null;
var roomNum = null;

function setConnected(connected) {
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/golden-flower-service');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/message', function (response) {
            showGreeting(JSON.parse(response.body).messageBody);
        });
    });
}

function sendCommand() {
    stompClient.send($("#path").val(), {}, $("#request").val());
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function enterRoom() {
    roomNum = $("#room-num").val();
    var seatNum = $("#seat-num").val();
    stompClient.send("/app/gf/enterRoom", {}, JSON.stringify({"roomNum": parseInt(roomNum), "seatNum": parseInt(seatNum)}));
}

function call() {
    var v = $("#call-value").val();
    stompClient.send("/app/gf/room/" + roomNum + "/action", {}, JSON.stringify({"actionType": 11, "detail": "" + v}))
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendCommand(); });
    $( "#call" ).click(function() { call(); });
    $("#enter-room").click(function() {enterRoom();});
    connect();
});
