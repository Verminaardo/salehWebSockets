const socketConn = new WebSocket('ws://192.168.43.16:8090/txtSocketHandler');

socketConn.onmessage = (e) => {
        showMessage(e.data);
        console.log(e);
}

socketConn.onopen = function() {
  alert("Connection established.");
  var name = prompt("Login?")
    console.log(name)
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

function sendLodin() {
    socketConn.send($("#log").val());
}

function sendMessage() {
    socketConn.send($("#msg").val());
}

function showMessage(message) {
    $("#messagelist").append("<div>" + message + "</div>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendMessage(); });


});

//нажатие ок
$('#ok').on('click', function () {
    debugger
    url='';
    login = $('#log').val();
    console.log((login))
    // let result = getLogin(url, login)
})

function getLogin(url, login) {
   const response = fetch(url+"?login="+login );
   return response.json();

}