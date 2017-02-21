var stompClient = null;
var roomNum = null;

var tableTotal = {};
var blackList = [];
var currentPos = 1;
var table = {};

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
            parseResponse(JSON.parse(response.body));
        });
    });
}

function parseResponse(message) {
    if (message.type == 2000 || message.type == 2001) {
        currentPos = message.messageBody.currentPos;
        blackList = message.messageBody.blackList;
        table = message.messageBody.table;
        currentBet = message.messageBody.currentBet;
        renderTable();
    } else if (message.type == 5000) {
        tableTotal = message.messageBody.tableInfo;
        renderTable();
    } else if (message.type == 5001) {
        $("#text-log").html("");
        $("#card-in-hand").html("");
        $("#card-on-board").html("");
    } else if (message.type == 0 || message.type == 8000) {
        $("#text-log").append("<tr><td>" + message.messageBody.text + "</td></tr>");
    } else if (message.type == 1888) {
        $("#card-in-hand").html(message.messageBody.text);
    } else if (message.type == 1889) {
        $("#card-on-board").html($("#card-on-board").html() + message.messageBody.text);
    }
}

function renderTable() {
    $("#table").html("");
    for (i = 1; i <=8; i++) {
        if (table[i]) {
            className = "";
            if (currentPos == i) {
                className += " current";
            }
            currentBetValue = currentBet[table[i].userName] ? currentBet[table[i].userName] : 0;
            text = "No." + i + " " + table[i].userName + "($" + table[i].chips + "): " + "$" + currentBetValue;
            $("#table").append('<tr><td class="' + className + '">' + text + '</tr></td>');
        } else if (tableTotal[i]) {
            $("#table").append('<tr><td class="watching">No.' + i + ' ' + tableTotal[i].userName + '(watching)</tr></td>');
        } else {
            $("#table").append('<tr><td class="watching">No.' + i + ' (empty)');
        }
    }
}

function sendCommand() {
    stompClient.send($("#path").val(), {}, $("#request").val());
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

function fold() {
    stompClient.send("/app/gf/room/" + roomNum + "/action", {}, JSON.stringify({"actionType": 10, "detail": ""}))
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendCommand(); });
    $( "#call" ).click(function() { call(); });
    $("#fold").click(function() {fold();});
    $("#enter-room").click(function() {enterRoom();});
    connect();
});
