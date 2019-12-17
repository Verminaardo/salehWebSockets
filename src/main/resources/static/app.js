$(function () {
   let socketConn = null;
   let USER_NOW = "";
   let USER_TO = "";


   function Socket() {
      socketConn && socketConn.close();
      socketConn = new WebSocket('ws://localhost:8080/txtSocketHandler');

      socketConn.onmessage = (e) => {
         showMessage(e.data);
         console.log(e);
      };

      socketConn.onopen = function () {
         //alert("Connection established.");
         sendLogin();
         sendButton.disabled = false;
      };

      socketConn.onclose = function (event) {
         if (event.wasClean) {
            //alert('Connection closed clean');
         } else {
            alert('Disconnection');
         }
         sendButton.disabled = true;
      };

      socketConn.onerror = function (error) {
         alert("error " + error.message);
         sendButton.disabled = true;
      };
   }

   function sendLogin() {
      debugger
      socketConn.send(JSON.stringify(
         {
            from: USER_NOW,
            to: USER_TO
         })
      );
   }

   function sendMessage() {
      let messageObject = {
         message: $("#msg").val()
      };
      socketConn.send(JSON.stringify(messageObject));
   }

   function showMessage(message) {
      $("#messagelist").append("<div>" + message + "</div>");
   }


   let sendButton = document.getElementById('send');
   sendButton.disabled = true;

   $("form").on('submit', function (e) {
      e.preventDefault();
   });
   $("#send").click(function () {
      sendMessage();
      $("#msg").val("")
   });

//нажатие ок
   $('#ok').on('click', function () {
      if (socketConn != null) {
         socketConn.close()
      }
      USER_NOW = $('#log').val();
      let url = "http://localhost:8080/user/" + USER_NOW + "/getAll";

      let result = login(url);
      let sel = document.getElementById('selector');
      result.then(obj => {
         sel[0] = new Option("-", 0);
         for (let i = 0; i < obj.length; i++) {
            let s = obj[i].login;
            sel[i + 1] = new Option(s, i + 1)
         }
      });
   });

   //список
   $('#selector').change(function () {
      let sel = document.getElementById('selector');
      USER_TO = sel.options[sel.selectedIndex].text;
      if (USER_TO !== "-") {
         console.log(USER_TO);
         console.log(USER_NOW);
         $("messagelist").innerHTML = "";
         Socket();
      }
   })
});

function login(url) {
   return fetch(url, {
      headers: {
         'Content-Type': 'application/json',
         // 'Content-Type': 'application/x-www-form-urlencoded',
      }
   }).then(response => response.json());
}

function getMessages(url) {
   return fetch(url, {
      headers: {
         'Content-Type': 'application/json',
         // 'Content-Type': 'application/x-www-form-urlencoded',
      }
   }).then(response => response.json());
}


