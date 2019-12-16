$(function () {
    socketConn = null;
let USER_NOW = "";
let USER_TO = "";


function Socket() {
    socketConn = new WebSocket('ws://192.168.43.16:8090/txtSocketHandler');

    socketConn.onmessage = (e) => {
        showMessage(e.data);
        console.log(e);
    }

    socketConn.onopen = function() {
        alert("Connection established.");
        sendLodin();
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
}

function sendLodin() {
    socketConn.send($("#log").val());
}

function sendMessage() {
    let messageObject = {
        from: $("#log").val(),
        to: USER_TO,
        message: $("#msg").val()
    };
    socketConn.send(JSON.stringify(messageObject));
}

function showMessage(message) {
    $("#messagelist").append("<div>" + message + "</div>");
}


    let sendButton = document.getElementById('send');
    sendButton.disabled=true;

    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() {
        debugger
        sendMessage();
    });

//нажатие ок
    $('#ok').on('click', function () {
        USER_NOW = $('#log').val();
       let url = "http://localhost:8090/user/" + USER_NOW + "/getAll"

        let result = getLogin(url)
        console.log(result)
        let sel = document.getElementById('selector');
        result.then(obj => {
            for (let i = 0; i < obj.length; i++) {
                let s = obj[i].login;
                sel[i] = new Option(s, i)
            }
        })
        sendButton.disabled = false;
    })

    //список
    $('#selector').change(function () {
        let sel = document.getElementById('selector');
        USER_TO = sel.options[sel.selectedIndex].text;
        console.log(USER_TO);
        console.log(USER_NOW);
        let url = "http://localhost:8090/message/"+USER_NOW+"/"+USER_TO+"/getAll";
       let result = sendLogins(url);
         console.log(result)
        $('messagelist').html('');
         result.then(el=>{
             el.forEach(element =>{
                 showMessage(element.postingDateTime + " - " + element.fromUser.login + ": " + element.text);
             })

         })
        Socket();
    })
});

function getLogin(url) {
   return fetch(url,{
       headers: {
           'Content-Type': 'application/json',
           // 'Content-Type': 'application/x-www-form-urlencoded',
       }
   }).then(response => response.json());
}

function sendLogins(url) {
    return fetch(url,{
        headers: {
            'Content-Type': 'application/json',
            // 'Content-Type': 'application/x-www-form-urlencoded',
        }
    }).then(response => response.json());
}


