$(function() {
    const contactList = $('.chatPerson').html();
    console.log(contactList);
    $.ajax({
        type: "post",
        dataType: "json",
        url: "ChatServlet?method=mobile",
        success: function(data) {
            console.log(data);
            $.ajax({
                type: "post",
                dataType: "json",
                url: "ChatServlet?method=what",
                success: function(msg) {
                    console.log(msg);
                    dataList(msg, data, contactList);
                    // 渲染左上角数据
                    $('.myInformation>img').attr('src', data.userimg);
                    $('.myInformation>.userName').html(data.username);
                    chatClick(msg, data);
                    // 进入聊天界面默认与第一个用户聊天
                    $('.chatPerson').eq(0).trigger('click');

                    clickSend(msg, data);
                    //通过URL请求服务端（chat为项目名称）
                    url = "ws://192.168.199.104:8080/chatSocket?username=" + data.usermobile;
                    //进入聊天页面就是一个通信管道
                    console.log(url);

                    if ('WebSocket' in window) {
                        ws = new WebSocket(url);
                    } else if ('MozWebSocket' in window) {
                        ws = new MozWebSocket(url);
                        alert();
                    } else {
                        alert('WebSocket is not supported by this browser.');
                        return;
                    }
                    ws.onopen = function() {
                        // showMsg("webSocket通道建立成功！！！");
                        console.log("webSocket通道建立成功！！！");
                    };

                    //监听服务器发送过来的所有信息
                    ws.onmessage = function(event) {
                        $.ajax({
                            type: "post",
                            dataType: "json",
                            url: "ChatServlet?method=mobile",
                            success: function(data) {
                                console.log(data);
                                $.ajax({
                                    type: "post",
                                    dataType: "json",
                                    url: "ChatServlet?method=what",
                                    success: function(msg) {
                                        clickSend(msg, data);
                                        chatClick(msg, data);
                                        console.log(123);
                                        eval("var result=" + event.data);
                                        for (let index = $('.chatPerson').length; index < msg.goodsImg.length; index++) {
                                            $('.contacts').append("<div class='chatPerson'></div>");
                                            $('.contacts .chatPerson').eq(index).append(contactList);
                                            $('.contactType').eq(index).html('离线');
                                            $('.contacts .chatPerson').eq(index).children('img').attr('src', msg.userImg[index]);
                                            $('.contacts .chatPerson').eq(index).children('.userName').html(msg.userName[index]);
                                        }
                                        //当他人点击自己的商品时显示
                                        setInterval(function() {
                                            for (let index = 0; index < msg.userMobile.length; index++) {
                                                //console.log(msg.userMobile[index] + 'dengyu?' + data.usermobile);
                                                if (msg.userMobile[index] == data.usermobile) {
                                                    //console.log('have past');
                                                    $('.chatPerson').eq(index).css('display', 'block');
                                                    $('.chatPerson').eq(index).children('.userName').html(msg.myclick[index]);
                                                    msg.type[index] = '2';
                                                }
                                            }
                                        }, 1000);
                                        //如果后台发过来的alert不为空就显示出来
                                        //                if (result.alert != undefined) {
                                        //                    $(".messageShow").eq(0).append(result.alert + "<br/>");
                                        //                }
                                        //如果用户列表不为空就显示
                                        console.log(result.names);
                                        $('.contactType').html('离线');
                                        if (result.names != undefined) {
                                            //刷新用户列表之前清空一下列表，免得会重复，因为后台只是单纯的添加
                                            $(result.names).each(
                                                function() {
                                                    for (let index = 0; index < msg.goodsImg.length; index++) {
                                                        console.log(msg.userMobile[index] + "  " + this);
                                                        if (msg.type[index] == '1' && msg.userMobile[index] == this)
                                                            $('.contactType').eq(index).html('在线');
                                                        else if (msg.myclick[index] == this && msg.type[index] == '2')
                                                            $('.contactType').eq(index).html('在线');
                                                    }
                                                });
                                        }

                                        //将用户名字和当前时间以及发送的信息显示在页面上
                                        //                                        if (result.from != undefined) {
                                        if (result.sendMsg != undefined) {
                                            console.log(result.from);
                                            //                                            if (result.from != msg.myMobile) {
                                            for (let index = 0; index < msg.goodsImg.length; index++) {
                                                if ($('.chatPerson').eq(index).css('background-color') == 'rgb(102, 102, 102)') {
                                                    if (msg.userMobile[index] != data.usermobile)
                                                        setData(msg.userName[index], result.sendMsg, true, msg.goodsImg[index]);
                                                    else
                                                        setData(data.username, result.sendMsg, false, msg.userimg);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    });
});

function sentAjax() {
    setTimeout(function() {
        data = { "message": $('textarea').html(), "type": 1 };
        if (data["message"] != "") {
            $.ajax({
                type: "post",
                dataType: "json",
                url: "ChatServlet?method=chat",
                data: data,
                success: function(msg) {
                    for (let index = 0; index < msg.length; index++) {

                    }
                }
            });
        }
        sentAjax();
    }, 1000);
}

function setData(name, text, judge, img) {
    let message = $('<div></div>');
    message.addClass('message');
    let username = $('<p>' + name + '</p>');
    username.addClass('name');
    let userImg = $("<img src='" + img + "'>");
    let sendMsg = $("<p>" + text + "</p>");
    sendMsg.addClass('concreteMessage');
    if (judge)
        message.addClass('leftMessage');
    else
        message.addClass('rightMessage');
    message.append(username).append(userImg).append(sendMsg);
    $('.messageShow').append(message);
}

function send(mobile, type, data) {
    let to = mobile;
    // if (to == userName) {
    //     alert("你不能给自己发送消息啊");
    //     return;
    // }
    //根据勾选的人数确定是群聊还是单聊
    let value = $("textarea").eq(0).val();
    //alert("消息内容为"+value);
    let object = null;

    object = {
        tomobile: to,
        msg: value,
        type: type, //1 确定交易 2单聊 3 离线
        mobile: data.usermobile,
        username: data.username
    }


    //将object转成json字符串发送给服务端
    let json = JSON.stringify(object);
    //alert("str="+json);
    ws.send(json);
    //消息发送后将消息栏清空
    console.log('a');
    $("textarea").eq(0).val("");
}


// 关闭浏览器清空内容


function dataList(msg, data, contactList) {
    $('.contacts .chatPerson').remove();
    // 渲染用户列表
    for (let index = 0; index < msg.goodsImg.length; index++) {
        $('.contacts').append("<div class='chatPerson'></div>");
        $('.contacts .chatPerson').eq(index).append(contactList);
        $('.contactType').eq(index).html('离线');
        $('.contacts .chatPerson').eq(index).children('img').attr('src', msg.goodsImg[index]);
        $('.contacts .chatPerson').eq(index).children('.userName').html(msg.goodsName[index]);
        if (msg.myclick[index] != data.usermobile) {
            $('.chatPerson').eq(index).css('display', 'none');
        }
    }
}

// 点击对应用户
function chatClick(msg, data) {
    $('.chatPerson').each(function(index, element) {
        $('.chatPerson').eq(index).unbind();
        $('.chatPerson').eq(index).click(function() {
            // 点击确认交易
            $('.decideTransacte').unbind();
            $('.decideTransacte').click(function() {
                if (confirm('是否确认交易')) {
                    $.ajax({
                        type: "post",
                        dataType: "json",
                        data: { "id": msg.goodsID[index] },
                        url: "TransactionServlet?method=transaction",
                        success: function(msg) {
                            if (msg.judge === 'success')
                                alert('交易成功');
                            else
                                alert('交易失败');
                        }
                    });
                }
            });
            $('.chatPerson').eq(index).css('background-color', 'rgb(102,102,102)').siblings('.chatPerson').css('background-color', 'rgb(255,255,255)');
            $('.commodityInformation img').attr('src', msg.goodsImg[index]);
            $('.commodityInformation .commodityName').html(msg.goodsName[index]);
            $('.commodityInformation .commodityPrice').html(msg.goodsPrice[index]);
            $('.commodityInformation .commodityIntroduce').html(msg.goodsIntroduce[index]);
            $('.commodityInformation .commodityWays').html(msg.goodsWays[index]);
        });
    });
}

// 点击发送
function clickSend(msg, data) {
    $('.enterSend span').eq(0).unbind();
    $('.enterSend span').eq(0).click(function() {
        console.log(111);
        for (let index = 0; index < msg.goodsImg.length; index++) {
            console.log(1);
            if ($('.chatPerson').eq(index).css('background-color') == 'rgb(102, 102, 102)') {
                if ($('.chatPerson').eq(index).children('.contactType').html() == '在线')
                    send($('.chatPerson').eq(index).children('.username').html(), 2, data);
                else
                    send($('.chatPerson').eq(index).children('.username').html(), 3, data);
            }
            console.log(msg.userMobile[index] + 'dengyu?' + data.usermobile)
            if (msg.userMobile[index] == data.usermobile) {
                console.log('have past');
                $('.chatPerson').eq(index).css('display', 'block');
                $('.chatPerson').eq(index).children('.commodityName').html(msg.click[index]);
            }
        }
    });
}