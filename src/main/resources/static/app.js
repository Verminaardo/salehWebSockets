const socketConn = new WebSocket('ws://62.113.119.225:8080/txtSocketHandler');

socketConn.onmessage = (e) => {
        showMessage(e.data);
        console.log(e);
}

socketConn.onopen = function() {
  alert("Connection established.");
};

socketConn.onclose = function(event) {
  if (event.wasClean) {
    alert('Connection closed clean');
  } else {
    alert('Disconnection');
  }
  alert('Code: ' + event.code + ' reason: ' + event.reason);
};
;

socketConn.onerror = function(error) {
  alert("error " + error.message);
};

function sendMessage() {
    socketConn.send($("#msg").val());
}

function showMessage(message) {
    $("#messagelist").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendMessage(); });
});