if (locationExtract('id') == undefined) {
    alert('当前没有对应的聊天空间');
    window.close();
}
$(function() {

    startSocket();
    emojiHandle();
    $('.icon-emoji-happy').click(function(event) {
        event.stopPropagation();
        $('.emoji').show();
    });
    btnHandle();
    if (locationExtract('from') == undefined) {
        $('.sureTrade').remove();
    }
})

//按钮操作
function btnHandle() {
    $('.btn').each(function(index) {
        $(this).mousedown(function() {
            $(this).css('background', ' linear-gradient(rgb(255, 153, 0) 20%,  rgba(255, 153, 0, 0.2) 145%)');
        });
    });
    $('html').mouseup(function() {
        $('.btn').css('background', 'linear-gradient(rgba(255, 153, 0, 0.2) 15%, #CDC673)');
    });
}

function emojiHandle() {
    for (let index = 1; index <= 75; index++) {
        $('.emoji').append('<img src=images/emojis/' + index + '.gif>');
    }
    $('.emoji img').each(function(index) {
        $('.emoji img').eq(index).click(function(event) {
            event.stopPropagation();
            selectEmoji(index + 1);
        })
    });
    $('html').unbind().click(() => hideEmoji());
}


var isOpen = false; //标识是否打开连接

var ws = null; //WebSocket对象
//启动WebSocket
function startSocket() {
    //获取用户ID
    $.ajax({
        type: "post",
        dataType: "json",
        url: "ChatServlet?method=send",
        data: { "id": locationExtract('id'),
        		"from":locationExtract('from')},
        success: function(totalMsg) {
            $(".myInformation .userName").html(totalMsg.myname);
            $(".myInformation img").attr('src', totalMsg.myImg);
            //渲染商品内容
            $('.goodsInformation img').attr('src', totalMsg.goodImg).attr('alt', totalMsg.goodName);
            $('.goodsName>dd').html(totalMsg.goodName).attr('title', totalMsg.goodName);
            $('.goodsIntroduce>dd').html(totalMsg.goodIntroduce).attr('title', totalMsg.goodIntroduce);
            $('.goodsPrice>dd').html(totalMsg.goodPrice).attr('title', totalMsg.goodPrice);
            $('.getWays>dd').html(totalMsg.goodWays).attr('title', totalMsg.goodWays);
            if (totalMsg.goodBargin == 1)
                $('.bargin>dd').html('可讲价');
            else
                $('.bargin>dd').html('不可讲价');

            var user = totalMsg.myid;

            var wsurl = "ws://" + totalMsg.ip + ":8080/webchat/" + user + "??"; //声明一个变量存储服务器地址
            //根据浏览器类型创建对应的WebSocket实例
            if ('WebSocket' in window) {
                ws = new WebSocket(wsurl);
            } else if ('MozWebSocket' in window) {
                ws = new MozWebSocket(wsurl);
            } else {
                alert("您的浏览器不支持WebSocket");
            }

            //给WebSocket注册监听方法

            //建立连接时触发
            ws.onopen = function() {
                isOpen = true;
                sureTransaction(totalMsg);
            };

            //客户端接收服务端数据时触发
            ws.onmessage = function(e) {
                sureTransaction(totalMsg);
                var result = eval("(" + e.data + ")");
                var from = result.from;
                var time = result.time;
                var content = result.msg;
                var currentUser = user; //当前窗口的用户

                //如果有消息记录
                if (result.records) {
                    handleRecords(result.records[0], currentUser, totalMsg);
                } else { //处理消息
                	sureTransaction(totalMsg);
                    handleMessage(from, time, content, currentUser, totalMsg, result.img);
                }

                //显示在线用户列表
                users = result.userlist.toString().split("@");
                //判断是否在线
                $('.onloneType dd').html('离线');
                for (let index = 0; index < users.length; index++) {
                    if (users[index] == totalMsg.userid)
                        $('.onloneType dd').html('在线');
                }
                scrollDivToBottom();
            };

            //通信发生错误时触发
            ws.onerror = function() {
                $(".messageShow").append("<div class='clear'><p><font color='red'>【系统消息】服务器异常</font></p></div>");
            };

            //连接关闭时触发
            ws.onclose = function() {
                $(".messageShow").html("<div class='clear'><p><font color='red'>【系统消息】与服务器的连接已断开</font></p></div>");
                $("#btn").val("连接");
                $("#me").html("");
                $("#count").html("0");
            };
            //发送信息
            $('.enterSend').click(function() {
                if (locationExtract('from') == undefined)
                    sendMsg(user, totalMsg.userid, totalMsg);
                else
                    sendMsg(user, locationExtract('from'), totalMsg);
            });
            $('.edit').keyup(function(event) {
                if (event.keyCode == 13){
//                	$('.edit').html($('.edit').slice(0,$('.edit').length-1));
                    $('.enterSend').trigger('click');
                }
            });
        }
    });
}

function sureTransaction(totalMsg) {
    $('.sureTrade').unbind('click').click(function() {
        //卖家点击按钮发送请求确认交易
        if (confirm('是否确认交易')) {
            if (locationExtract('from') == undefined||locationExtract('from')==totalMsg.userid) {
                $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "TransactionServlet?method=transaction",
                    data: {
                        "id": locationExtract('id')
                    },
                    success: function(msg) {
                        if (msg.judge == 'success') {
                            alert('确认交易成功');
                            $('.sureTrade').unbind('click');
                            $('.edit').html('已点击确认交易，交易完成');
                            $('.enterSend').trigger('click');
                        }
                    }
                });
            } else {
                $('.edit').html("对方已选择交易该闲置/提供该寻求，请点击<span class=\"sureTrade btn\">确认交易</span>完成本次交易");
                $('.enterSend').trigger('click');
                sureTransaction(totalMsg);
            }
        }
    });
}

/** 处理消息 */
function handleMessage(from, time, content, currentUser, totalMsg, img) {
    if (from == currentUser) { //发送信息的是自己
        setData(totalMsg.myname, content, false, img, time);
    } else {
    	if(locationExtract('from') == undefined)
    		setData(from, content, true, img, time);
    	else
    		setData(totalMsg.othername, content, true, totalMsg.otherimg, time);
    }
    sureTransaction(totalMsg);
}

/** 处理聊天记录 */
function handleRecords(records, currentUser, totalMsg) {
    for (var i in records) {
        var sender = records[i].sender;
        var receiver = records[i].receiver;
        var message = records[i].message;
        var recordtime = records[i].time;
        if(sender==currentUser||sender==locationExtract('from')||sender==totalMsg.userid){
	        if (sender == currentUser)
	            sender = '我';
	        else
	            sender = "<a href='javascript:void(0)' onclick='selectUser(this);'>" + sender + "</a>";
	        if (sender == '我') {
	            setData(totalMsg.myname, message, false, totalMsg.myImg, recordtime);
	        } else {
	            if (locationExtract('from') == undefined)
	                setData(totalMsg.name, message, true, totalMsg.img, recordtime);
	            else
	                setData(totalMsg.othername, message, true, totalMsg.otherimg, recordtime);
	        }
        }
        // }
    }
    sureTransaction(totalMsg);
    if (records[0] != '')
        $(".messageShow").append("<div class='clear' style='text-align:center;margin:5px auto;'>-----以上是历史消息-----</div>");
//    if(locationExtract('from') == undefined)
//		var toId=totalMsg.userid;
//	else
//		var toId=locationExtract('from');
    $.ajax({
        type: "post",
        dataType: "json",
        url: "ChatServlet?method=offmessage",
        success: function(msg) {
            for (let index = 0; index < msg.offmessage.length; index++)
                setData(msg.from[index], msg.offmessage[index], true, msg.img, msg.time[index]);
        }
    });
}

//断开连接
function closeSocket() {
    if (isOpen) {
        ws.close();
        isOpen = !isOpen;
    }
}

//发送消息
function sendMsg(user, to, totalMsg) {
    var msg = $(".edit").html();

    if (!isOpen) {
        alert("请先连接服务器");
    } else if ($.trim(msg).length == 0) {
        alert("消息内容不能为空");
        $(".edit").html("");
    } else {
        var from = user;
        var face = $("#face").val();
        var data = { "from": from, "face": face, "to": to, "content": msg, "id": locationExtract("id"), "name": totalMsg.name };
        ws.send(JSON.stringify(data));
        //清空消息发送区
        $(".edit").html("");
    }
}

//按钮点击事件
$("#loginbtn").click(function() {
    if (!isOpen) {
        startSocket();
        $("#user").val("");
        $("#to").val("所有人");
        $(this).val('断开');
    } else if (isOpen) {
        closeSocket();
        $("#user").val("");
        $("#to").val("所有人");
        $(this).val('连接');
    }
});

/**使得滚动条始终在最底部，显示最新消息*/
function scrollDivToBottom() {
    $(".messageShow").scrollTop($(".messageShow")[0].scrollHeight);
}

/** 判断用户是否在线 */
function isOnline(user) {
    var flag = false;
    for (var i in users)
        if (users[i] == user) {
            flag = true;
            break;
        }
    return flag;
}

/** 选择表情 */
function selectEmoji(id) {
    $('.edit').append("<img src=\"images\/emojis\/" + id + ".gif\" />");
    $(".emoji").hide();
}

/** 隐藏表情区 */
function hideEmoji() {
    $('.emoji').hide();
}


//在消息框中插入信息
function setData(name, text, judge, img, time) {
    let message = $('<div></div>');
    message.addClass('message');
    if (judge)
        var username = $('<p>' + name + '<span class=\'time\'>' + time + '</span></p>');
    else
        var username = $('<p><span class=\'time\'>' + time + '</span>' + name + '</p>');
    username.addClass('name');
    let userImg = $("<img src='" + img + "'>");
    let sendMsg = $("<p class='concreteMessage'>" + text + "</p>");
    if (judge)
        message.addClass('leftMessage');
    else
        message.addClass('rightMessage');
    message.append(username).append(userImg).append(sendMsg);
    $('.messageShow').append(message);
}