// SockJS와 STOMP 클라이언트 초기화
var socket = new SockJS("/ws");
var stompClient = Stomp.over(socket);

stompClient.connect({}, onConnected);

function onConnected() {
    console.log("Connected socket");


    stompClient.subscribe("/user/public", assignUser);

    stompClient.subscribe("/user/receiveMessage", receiveMessageHandler);
    stompClient.subscribe("/user/queue/changeName", changeNameHandler);
    stompClient.subscribe("/user/roomList", roomListHandler);
    stompClient.subscribe("/user/joinedRoom", joinedRoomHandler);
    stompClient.subscribe("/user/drawTable", drawTableHandler);
    stompClient.subscribe("/user/testCli", testCliHandler);
    stompClient.subscribe("/user/appendMe", appendMeHandler);
    stompClient.subscribe("/user/highlightMe", highlightMeHandler);
    stompClient.subscribe("/user/cssMe", cssMeHandler);
    stompClient.subscribe("/user/rolledDice", rolledDiceHandler);
    stompClient.subscribe("/user/diceUpdate", diceUpdateHandler);

    stompClient.send("/app/user.addUser", {});
}


function assignUser(message) {
    var data = JSON.parse(message.body);
    userId = data.userId;
    console.log("assigning user:" + userId);
    $("#name").val("user" + userId);
    var rooms = data.rooms;
    var visitors = data.visitors;
    $(".roomlist").empty();
    for (var i = 1; i <= rooms; i++) {
        var listItem = `<li style="color:${visitors[i - 1] == 2 ? "#C23535" : visitors[i - 1] == 1 ? "#0A07A6" : "#2C2312"};"><b>room ${i} (${visitors[i - 1]} / 2)</b>`;
        if (visitors[i - 1] < 2) {
            listItem += ` <button onclick="joinRoom(${i});">Join</button></li>`;
        }
        $(".roomlist").append(listItem);
    }
    cssMe("#errorstr", "display", "none");
}


function receiveMessageHandler(message) {
    var msg = JSON.parse(message.body).content;
    handleMessage(msg);
}

function changeNameHandler(message) {
    console.log("Received message: " + message);
    var name = JSON.parse(message.body).userId;
    $("#name").val(name);
}

function roomListHandler(message) {
    var data = JSON.parse(message.body);
    var rooms = data.rooms;
    var visitors = data.visitors;
    $(".roomlist").empty();
    for (var i = 1; i <= rooms; i++) {
        var listItem = `<li style="color:${visitors[i - 1] == 2 ? "#C23535" : visitors[i - 1] == 1 ? "#0A07A6" : "#2C2312"};"><b>room ${i} (${visitors[i - 1]} / 2)</b>`;
        if (visitors[i - 1] < 2) {
            listItem += ` <button onclick="joinRoom(${i});">Join</button></li>`;
        }
        $(".roomlist").append(listItem);
    }
    cssMe("#errorstr", "display", "none");
}

function joinedRoomHandler(message) {
    var roomNumber = JSON.parse(message.body).roomNumber;
    cssMe(".rollingbtn", "display", "block");
    cssMe(".hidden", "display", "block");
    $(".room_name").append(`Room ${roomNumber}`);
    $(".roomlist").remove();
}

function drawTableHandler(message) {
    var isStart = JSON.parse(message.body).isStart;
    if (isStart) $(".game_table").append(tableHTML);
    else $(".game_table").empty();
}

function testCliHandler(message) {
    var data = JSON.parse(message.body);
    var id = data.id;
    var turn = data.turn;
    var name = data.name;

    // 클라이언트 ID 확인 로직은 서버 측에서 처리, 클라이언트는 받은 메시지만 처리
    if (turn === 0) {
        appendMe("#p2name", name);
        appendMe(".btnplace1", '<button class="btn"></button>');
        testfunc(turn);
    } else if (turn === 1) {
        appendMe("#p1name", name);
        appendMe(".btnplace2", '<button class="btn"></button>');
        testfunc(turn);
    } else {
        $(".btn").attr("onclick", `testfunc(${turn}, $(this).parent());`);
        stompClient.send("/app/appendInRoom", {}, JSON.stringify({ selector: "#turn", content: `Turn ${parseInt(turn / 2)}/12` }));
        stompClient.send("/app/highlightInRoom", {}, JSON.stringify({ highlight: `.player${(turn % 2) + 1}`, unhighlight: `.player${((turn + 1) % 2) + 1}` }));
    }
    highlightMe(".btn", "");
    isMyTurn = 1;
    $(".btn").empty();
}

function appendMeHandler(message) {
    var data = JSON.parse(message.body);
    appendMe(data.target, data.content, data.option, data.target2, data.content2);
}

function highlightMeHandler(message) {
    var data = JSON.parse(message.body);
    highlightMe(data.target, data.basic);
}

function cssMeHandler(message) {
    var data = JSON.parse(message.body);
    cssMe(data.elem, data.attr, data.value);
}

function rolledDiceHandler(message) {
    var leaveDice = JSON.parse(message.body).leaveDice;
    $(window).scrollTop(0);
    appendMe(".rollingbtn", `Rolling (${leaveDice}/3)`);
}

function diceUpdateHandler(message) {
    var dices = JSON.parse(message.body).dices;
    rollingRandom(dices);
}











// 다이스 클릭 이벤트를 STOMP 메시지로 전송
$(".dices").click(function () {
    if (isMyTurn == 1) {
        var diceId = this.id;
        var keep = keepDice[parseInt(diceId.replace("dice", "")) - 1] == 1 ? 0 : 1;
        keepDice[parseInt(diceId.replace("dice", "")) - 1] = keep;

        var color = keep ? "darkorange" : "black";
        var fontSize = keep ? "100px" : "80px";

        // 선택한 다이스 상태 변경을 서버에 전송
        stompClient.send("/app/cssInRoom", {}, JSON.stringify({ selector: "#" + diceId, color: color, fontSize: fontSize }));
    }
});

// 'roll dice' 이벤트를 서버로 전송
function rollingDice() {
    stompClient.send("/app/rollDice", {}, JSON.stringify(keepDice));
}

// 채팅 메시지 전송
$("#chat").on("submit", function (e) {
    e.preventDefault();
    var name = $("#name").val().trim();
    var message = $("#message").val().trim();
    if (message) {
        stompClient.send("/app/sendMessage", {}, JSON.stringify({ name: name, message: message }));
        $("#message").val("");
        $("#message").focus();
    }
});

stompClient.send("/app/rooms", {}, JSON.stringify({ selector: "#" }));

// testfunc 및 관련 이벤트 전송 로직을 STOMP 메시지 전송으로 구현
function testfunc(turn, parent) {
    $(".btn").removeAttr("onclick");
    keepDice = [0, 0, 0, 0, 0];
    // 이벤트 데이터와 함께 서버로 메시지를 보내는 방식으로 변경
    stompClient.send("/app/cssInRoom", {}, JSON.stringify({ selector: ".dices", property: "color", value: "black" }));
    stompClient.send("/app/cssInRoom", {}, JSON.stringify({ selector: ".dices", property: "font-size", value: "80px" }));
    stompClient.send("/app/appendInRoom", {}, JSON.stringify({ selector: ".dices", htmlContent: "" }));
    stompClient.send("/app/clientToRoomClient", {}, {});
    if (parent !== undefined) {
        var score = parseInt($(parent).attr("index"));
        var id = $(parent).attr("id");
        stompClient.send("/app/pickScore", {}, JSON.stringify({ score: score, id: id }));
    }
    stompClient.send("/app/testServ", {}, JSON.stringify({ turn: ++turn }));
    isfirstRoll = 1;
}

function handleMessage(msg) {
    if (msg === "!@#$exit show!@#$") {
        $(".hidden").css("display", "block");
    } else if (msg === "!@#$exit hidden!@#$") {
        $(".hidden").css("display", "none");
    } else {
        $("#chatLog").append(msg + "\n");
        $("#chatLog").scrollTop($("#chatLog")[0].scrollHeight);
    }
}

function joinRoom(roomNumber) {
    stompClient.send("/app/joinRoom", {}, JSON.stringify({ roomNumber: roomNumber }));
}

function appendMe(target, content) {
    $(target).empty();
    $(target).append(content);
}

function highlightMe(target, basic) {
    $(basic).css("background-color", "#FEFAEE");
    $(target).css("background-color", "#FFDF60");
}

function cssMe(elem, attr, value) {
    $(elem).css(attr, value);
}

const rollingRandom = async function (dices) {
    for (var i = 1; i < 10; i++) {
        for (var j = 1; j <= 5; j++) {
            if ($(`#dice${j}`).css("color") == "rgb(0, 0, 0)") {
                $(`#dice${j}`).css("margin-top", `${Math.floor(Math.random() * 15) - 8}px`);
                $(`#dice${j}`).css("margin-left", `${Math.floor(Math.random() * 15) - 8}px`);
                appendMe(`#dice${j}`, dice_text[Math.floor(Math.random() * 6)]);
            }
        }
        await sleep(70);
    }
    for (var i = 1; i <= 5; i++) {
        appendMe(`#dice${i}`, dice_text[dices[i - 1] - 1]);
    }
};


var tableHTML = `
<style type="text/css">
.tg  {border-collapse:collapse;border-spacing:0;}
.tg td{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;
overflow:hidden;padding:10px 5px;word-break:normal;}
.tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;
font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}
.tg .tg-0lax{background-color:#FEFAEE;text-align:center;vertical-align:center;}
.tg .tg-1lax{background-color:#FEFAEE;text-align:left;vertical-align:center;}
.btn{height:25px;width:100%;}
.dice_text{font-size:20px;}
</style>
<table class="tg" style="undefined;table-layout: fixed; width: 252px">
<colgroup>
<col style="width: 130px">
<col style="width: 60px">
<col style="width: 60px">
</colgroup>
<thead>
<tr>
<th class="tg-1lax" id="turn">Turn 1/12</th>
<th class="tg-0lax player1" id="p1name"></th>
<th class="tg-0lax player2" id="p2name"></th>
</tr>
</thead>
<tbody>
<tr>
<td class="tg-1lax" id=""><span class="dice_text">⚀</span> Aces</td>
<td class="tg-0lax player1 btnplace1" index="1" id="1-1"></td>
<td class="tg-0lax player2 btnplace2" index="1" id="1-2"></td>
</tr>
<tr>
<td class="tg-1lax" id=""><span class="dice_text">⚁</span> Deuces</td>
<td class="tg-0lax player1 btnplace1" index="2" id="2-1"></td>
<td class="tg-0lax player2 btnplace2" index="2" id="2-2"></td>
</tr>
<tr>
<td class="tg-1lax" id=""><span class="dice_text">⚂</span> Threes</td>
<td class="tg-0lax player1 btnplace1" index="3" id="3-1"></td>
<td class="tg-0lax player2 btnplace2" index="3" id="3-2"></td>
</tr>
<tr>
<td class="tg-1lax" id=""><span class="dice_text">⚃</span> Fours</td>
<td class="tg-0lax player1 btnplace1" index="4" id="4-1"></td>
<td class="tg-0lax player2 btnplace2" index="4" id="4-2"></td>
</tr>
<tr>
<td class="tg-1lax" id=""><span class="dice_text">⚄</span> Fives</td>
<td class="tg-0lax player1 btnplace1" index="5" id="5-1"></td>
<td class="tg-0lax player2 btnplace2" index="5" id="5-2"></td>
</tr>
<tr>
<td class="tg-1lax" id=""><span class="dice_text">⚅</span> Sixes</td>
<td class="tg-0lax player1 btnplace1" index="6" id="6-1"></td>
<td class="tg-0lax player2 btnplace2" index="6" id="6-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">Bonus(+35)</td>
<td class="tg-0lax player1" index="7" id="bonus-1">0/63</td>
<td class="tg-0lax player2" index="7" id="bonus-2">0/63</td>
</tr>
<tr>
<td class="tg-1lax" id="">🃏 Choice</td>
<td class="tg-0lax player1 btnplace1" index="8" id="7-1"></td>
<td class="tg-0lax player2 btnplace2" index="8" id="7-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">🍀 4 of a kind</td>
<td class="tg-0lax player1 btnplace1" index="9" id="8-1"></td>
<td class="tg-0lax player2 btnplace2" index="9" id="8-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">🏠 Full House</td>
<td class="tg-0lax player1 btnplace1" index="10" id="9-1"></td>
<td class="tg-0lax player2 btnplace2" index="10" id="9-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">🀜 S. Straight</td>
<td class="tg-0lax player1 btnplace1" index="11" id="10-1"></td>
<td class="tg-0lax player2 btnplace2" index="11" id="10-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">🀝 L. Straight</td>
<td class="tg-0lax player1 btnplace1" index="12" id="11-1"></td>
<td class="tg-0lax player2 btnplace2" index="12" id="11-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">🎲 Yachoo</td>
<td class="tg-0lax player1 btnplace1" index="13" id="12-1"></td>
<td class="tg-0lax player2 btnplace2" index="13" id="12-2"></td>
</tr>
<tr>
<td class="tg-1lax" id="">Total</td>
<td class="tg-0lax player1" index="14" id="total-1">0</td>
<td class="tg-0lax player2" index="14" id="total-2">0</td>
</tr>
<tr>
<td class="tg-1lax" colspan="3" style="height:50px; text-align: center; font-size: 25px;">YACHOO</td>
</tr>
</tbody>
</table>
`;
