//判断是否为登录状态
$(function() {
    //打开websocket
    $.ajax({
        type: "post",
        dataType: "json",
        url: "JudgeLoginServlet?method=judge",
        success: function(msg) {
            if (msg.judge !== "") {
                socketOpen(msg.id, msg.ip);
                return false;
            }
        }
    });
    //
    $.ajax({
        type: "post",
        dataType: "json",
        url: "ChatServlet?method=getmessage",
        success: function(msg) {
            if (parseInt(msg.count) > 0) {
                $('.icon-chat-bubble-dots').addClass('newChat')
                    .attr('href', 'newChat.html?id=' + msg.goodid[msg.goodid.length - 1] + "&from=" + msg.from[msg.goodid.length - 1])
                    .click(function() {
                        $('.icon-chat-bubble-dots').removeClass('newChat');
                    });;
            }
        }
    });
    sentMobileCode();
    inputFocus();
    changeClick();
    $.ajax({
        type: "post",
        dataType: "json",
        url: "JudgeLoginServlet?method=judge",
        success: function(msg) {
            if (msg.judge != "") {
                $('.textInput').val('');
                $('.coverBg').css('display', 'none');
                $('.showContaniner>span').remove();
                $('.container').remove();
                $('.showContaniner').append("<a href='myGoods.html?nav=0'><img src='" + msg.img + "'></a>");
                $('.showInformation>div img').attr('src', msg.img);
                $('.showInformation .userName').html(msg.username);
                userimgHover();
                outOfLogin();
            } else {
                if (window.location.href.indexOf('myGoods.html') > 0) {
                    alert('您尚未登录');
                    $('.container').siblings().remove();
                    $('.container').eq(0).show();
                    $('.container form').eq(0).show();
                }
            }
        }
    });
});

// 聚焦搜索框框发生改变
function inputFocus() {
    for (let index = 0; index < $('.container form').length; index++) {
        $('.container form:eq(' + index + ') .textInput>input').each(function(i, element) {
            $('.container form:eq(' + index + ') .textInput>input').eq(i).focus(function() {
                if ($('.container form:eq(' + index + ') .textInput>input').eq(i).val() != '' && $('.container form:eq(' + index + ') .errorTip').eq(i).html() == '') {
                    $('.container form:eq(' + index + ') .textInput').eq(i).css('border', '2px solid rgb(102,204,102)');
                } else {
                    $('.container form:eq(' + index + ') .textInput').eq(i).css('border', '2px solid rgb(255,0,0)');
                }
            });
            $('.container form:eq(' + index + ') .textInput>input').eq(i).keyup(function() {
                if ($('.container form:eq(' + index + ') .textInput>input').eq(i).val() != '' && $('.container form:eq(' + index + ') .errorTip').eq(i).html() == '') {
                    $('.container form:eq(' + index + ') .textInput').eq(i).css('border', '2px solid rgb(102,204,102)');
                } else {
                    $('.container form:eq(' + index + ') .textInput').eq(i).css('border', '2px solid rgb(255,0,0)');
                }
            })
        })
    }
}

// 滑过头像显示资料框
function userimgHover() {
    $('.showContaniner>a').hover(function() {
        $('.showInformation').stop();
        $('.showInformation').slideDown();
    });
    $('.showContaniner').hover(function() {}, function() {
        $('.showInformation').stop();
        $('.showInformation').slideUp();
    });
}

// 点击弹出登录注册块
$('.showContaniner>span').each(function(index, element) {
    var $this = $(this);
    if (index > 1)
        index = index - 2;
    $this.click(function() {
        $('.coverBg').css('display', 'block');
        $('.container').css('display', 'block');
        $('.container form').eq(index).css('display', 'block');
        if (index === 0) {
            $.ajax({
                type: "post",
                url: "LoginServlet?method=remember",
                dataType: "json",
                success: function(msg) {
                    $('.login input').eq(0).val(msg.mobile);
                    $('.login input').eq(1).val(msg.password);
                }
            });
        }
    });
});

$('.loginButton').click(function() {
    $('.showContaniner span').eq(0).trigger('click');
});
// 验证输入

// 手机号码验证
$('.mobileText').each(function(index, element) {
    let $this = $(this);
    $this.keyup(function() {
        let mobileCheck = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        if (!$(this).val().match(mobileCheck))
            $('.mobileTip').eq(index).html('请输入正确的手机号码');
        else
            $('.mobileTip').eq(index).html('');
    });
});

// 手机号码查重
$('.registered .textInput').eq(0).children('input').blur(function() {
    if ($('.registered .textInput').eq(0).children('input').val().length == 11) {
        let data = {
            "mobile": $('.registered input').eq(0).val(),
            "Vcode": $('.registered input').eq(1).val()
        };
        $.ajax({
            type: "post",
            url: "RegisterServlet?method=repeat",
            dataType: "text",
            data: data,
            success: function(msg) {
                if (msg === "registered")
                    $('.registered .errorTip').eq(0).html('该账号已被注册');
            }
        });
    }
});

// 密码检查
$(".sureRegister [type='password']")
    .eq(0)
    .keyup(
        function() {
            let pureAlpha = /^(?:\d+|[a-zA-Z]+|[!@#$%^&*]+)$/,
                dulAlpah = /^(?![a-zA-z]+$)(?!\d+$)(?![!@#$%^&*]+$)[a-zA-Z\d!@#$%^&*]+$/,
                complexAlpha = /^(?![a-zA-z]+$)(?!\d+$)(?![!@#$%^&*]+$)(?![a-zA-z\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\d!@#$%^&*]+$)[a-zA-Z\d!@#$%^&*]+$/;
            if ($(this).val() === "")
                $('.sureRegister .errorTip:eq(1)').html('请输入密码');
            else if ($(this).val().match(pureAlpha))
                $('.sureRegister .errorTip:eq(1)').html('密码强度较弱');
            else if ($(this).val().match(dulAlpah))
                $('.sureRegister .errorTip:eq(1)').html('密码强度适中');
            else if ($(this).val().match(complexAlpha))
                $('.sureRegister .errorTip:eq(1)').html('密码强度较强');
            judgeSame();
        });
$(".sureRegister [type='password']").eq(1).keyup(function() {
    judgeSame();
});
// 关闭登陆注册界面
$('.icon-cancel').click(function() {
    $('form').css('display', 'none');
    $('.coverBg').css('display', 'none');
    $('.errorTip').html("");
    $("form [type='text']").val("");
    $("form [type='password']").val("");
});

$('.backRegister').click(function() {
    $('.imgCode').removeClass('rotateDisplay');
    $('.imgCode').addClass('rotateDisappear');
    setTimeout(function() {
        $('.imgCode').css('display', 'none');
        $('form').css('display', 'block');
        $('form').addClass('rotateDisplay');
        $('form').removeClass('rotateDisappear');
    }, 500);
});

function judgeBlank() {
    var texts = $(":text");
    for (var i = 0; i < texts.length; i++) {
        if (texts.value === "")
            return false;
    }
    return true;
}
turnForm();

// 切换登录注册界面
function turnForm() {
    $('.turn').each(function(index, element) {
        var $this = $(this);
        $this.click(function() {
            if (index === 1) {
                rotateTurn('form:eq(' + index + ')', 'form:eq(0)');
            } else {
                rotateTurn('form:eq(' + index + ')', 'form:eq(1)');
                $('.countDown:eq(0)').html(0);
                $('.mobileCode').html("");
                sentMobileCode();
                $('.mobileCode').append("<button class='getCode'>发送手机验证码</button>");
                sentMobileCode();
                changeClick();
            }
        });
    });
    $('.turnStu').click(function() {
        rotateTurn('.container form', '.studentLogin');
    });
    $('.turnLogin').click(function() {
        rotateTurn('.container form', '.login');
    });
    $('.turnRegistered').click(function() {
        rotateTurn('.container form', '.registered');
    })
}

// 判断是否能注册，可以则发送验证码
$('.btn input:eq(1)').click(
    function() {
        let data = {
            "mobile": $('.registered input').eq(0).val(),
            "Vcode": $('.registered input').eq(1).val()
        };
        if (!$('.registered .tip input')[0].checked)
            $('.registered .errorTip').eq(2).html('未同意网站协议');
        if ($('.registered .errorTip').eq(0).html() === "" && data["mobile"] != "" && $('.registered .tip input')[0].checked) {
            $.ajax({
                type: "post",
                url: "RegisterServlet?method=registered",
                dataType: "text",
                data: data,
                success: function(msg) {
                    if (msg === "registered") {
                        $('.registered .errorTip').eq(0).html('该账号已被注册');
                        $("#vcodeImg").trigger("click");
                    } else if (msg === "VcodeError") {
                        $('.registered .errorTip').eq(1).html('验证码错误');
                    } else {
                        $('form').fadeOut(300);
                        setTimeout(function() {
                            $('form:eq(2)').fadeIn(300);
                        }, 300);
                    }
                }
            });
        }
        return false;
    });

// 模块旋转转换
function rotateTurn(turnHidden, turnShow) {
    $(turnHidden).addClass('rotateDisappear');
    setTimeout(function() {
        $(turnHidden).css('display', 'none');
        $(turnShow).css('display', 'block');
        $(turnShow).addClass('rotateDisplay');
        $(turnHidden).removeClass('rotateDisappear');
        setTimeout(function() {
            $(turnShow).removeClass('rotateDisplay');
        }, 500);
    }, 500);
}



// 点击发送手机验证码
function sentMobileCode() {
    $('.getCode').eq(0).click(
        function() {
            var data = {
                "mobile": $('.registered input').eq(0).val()
            };
            $.ajax({
                type: "post",
                url: "RegisterServlet?method=sendcode",
                dataType: "text", // 返回数据类型
                data: data,
                success: function(msg) {
                    clearTimeout(codeLost);
                    $('.sureRegister .mobileCode .getCode').remove();
                    $('.sureRegister .mobileCode').append(
                        "验证码<span class='countDown'>60</span>s后可重新发送");
                    $('.countDown:eq(0)').html(60);
                    countTime('.countDown:eq(0)');
                    setTimeout(
                        function() {
                            $('.sureRegister .mobileCode').html("");
                            $('.sureRegister .mobileCode').append(
                                "<button class='getCode'>重新发送验证码</button>");
                            sentMobileCode();
                            changeClick();

                        }, 61000);
                    var codeLost = setTimeout(function() {
                        $.ajax({
                            type: "post",
                            url: "RegisterServlet?method=Timeout",
                            dataType: "text", // 5分钟结束后向后台发送请求
                            success: function(newdata) {}
                        });
                    }, 300000)
                },
                error: function() {
                    console.log("error");
                }
            });
            return false;
        });
}

// 倒计时
function countTime(node) {
    setTimeout(function() {
        $(node).html($(node).html() - 1);
        if ($(node).html() <= 0) {
            return true;
        } else
            countTime(node);
    }, 1000);
}

// 密码框显示与隐藏
$('.textInput .icon').each(function(index, element) {
    var $this = $(this);
    $this.click(function() {
        if ($this.attr('class') == 'icon icon-eye') {
            $this.addClass('icon-eye-off');
            $this.removeClass('icon-eye');
            $('.passwordText').eq(index).attr('type', 'text');
        } else if ($this.attr('class') == 'icon icon-eye-off') {
            $this.addClass('icon-eye');
            $this.removeClass('icon-eye-off');
            $('.passwordText').eq(index).attr('type', 'password');
        }
    });
});

// 验证码图片点击修改
$("#vcodeImg").click(function() {
    this.src = "CpachaServlet?method=loginCapcha&t=" + new Date().getTime();
});

// 密码账号匹配 手机登录
$('.container .btn').eq(0).click(function() {
    let checked = "F";
    if ($(".autoLogin input").eq(0).is(":checked"))
        checked = "T";
    let data = {
        "mobile": $('.container input').eq(0).val(),
        "password": $('.container input').eq(1).val(),
        "remember": checked
    };
    $.ajax({
        type: "post",
        url: "LoginServlet?method=login",
        dataType: "json", // 返回数据类型
        data: data,
        success: function(msg) {
            if (msg.judge === "success") {
                $('.container .btn').eq(0).unbind();
                $('.container .btn').eq(0).children('input').val(
                    '登录中...');
                setTimeout(function() {
                    location.replace(location.href);
                    $('.textInput').val('');
                    $('.coverBg').css('display', 'none');
                    $('.showContaniner>span').remove();
                    $('.container').remove();
                    $('.showContaniner').append("<a href='myGoods.html?nav=0'><img src='" + msg.img + "'></a>");
                    outOfLogin();
                }, 500);
            } else
                $('.container .passwordTip').eq(0).html('密码与账号不匹配');
        },
        error: function() {
            console.log("error");
            $('.container .passwordTip').eq(0).html('密码与账号不匹配');
        }
    });
    return false;
});

//学号登录
$('.studentLogin input').eq(0).keyup(function() {
    let studentId = "^[0-9]*[1-9][0-9]*$";
    if (!$('.studentLogin input').eq(0).val().match(studentId) || $('.studentLogin input').eq(0).val().length != 9)
        $('.studnetError').html('学号不正确');
    else
        $('.studnetError').html('');
});

$('.studentLogin .btn').eq(0).click(function() {
    let studentId = "^[0-9]*[1-9][0-9]*$";
    if ($('.studentLogin input').eq(0).val().match(studentId) && $('.studnetError').html() === "") {
        let id = encodeInp($('.studentLogin input').eq(0).val());
        let password = encodeInp($('.studentLogin input').eq(1).val());
        let data = {
            "id": $('.studentLogin input').eq(0).val(),
            "password": $('.studentLogin input').eq(1).val(),
            "code": id + "%%%" + password,
        }
        $.ajax({
            type: "post",
            dataType: "json",
            url: "LoginServlet?method=student",
            data: data,
            success: function(msg) {
                if (msg.judge === 'success') {
                    $('.studentLogin .btn input').val('登录中...');
                    setTimeout(function() {
                        location.replace(window.location);
                    }, 1000);
                } else {
                    $('.passwordTip').html('密码与账号不匹配');
                }
            }
        });
    }
})

// 判断两次输入的密码是否相同
function judgeSame() {
    if ($(".sureRegister input").eq(1).val() === $(".sureRegister input").eq(2).val()) {
        $('.sureRegister .errorTip:eq(2)').html('');
        return true;
    }
    $('.sureRegister .errorTip:eq(2)').html('两次输入的密码不同');
    return false;
}

// 按下注册发送手机验证码和密码
$(".sureRegister [type='submit']").click(function() {
    var data = {
        "mobile": $('.registered input').eq(0).val(),
        "mobileCode": $('.sureRegister input').eq(0).val(),
        "password": $('.sureRegister input').eq(1).val()
    }
    if (judgeSame() && $(".sureRegister input").eq(1).val() != "") {
        $.ajax({
            type: "post",
            url: "RegisterServlet?method=sendmessage",
            dataType: "text", // 返回数据类型
            data: data,
            success: function(msg) {
                if (msg === "success") {
                    alert('注册成功');
                    formEmpty();
                    $('.sureRegister').css('display', 'none');
                    $('.login').css('display', 'block');
                } else if (msg === "codeError") {
                    $('.sureRegister .errorTip').eq(0).html("验证码错误");
                }
            },
            error: function() {
                console.log("error");
            }
        });
    }
    return false;
});

// 清空表单输入
function formEmpty() {
    for (let index = 0; index < $('.container input').length; index++) {
        if ($('.container input').eq(index).attr('type') === 'text' ||
            $('.container input').eq(index).attr('type') === 'password')
            $('.container input').eq(index).val("");
    }
}

// 注销用户
function outOfLogin() {
    $('.outLogon').eq(0).click(function() {
        if (confirm('是否确认注销')) {
            $.ajax({
                type: "get",
                dataType: "text",
                url: "LoginServlet?method=loginout",
                success: function(msg) {
                    location.replace(location.href);
                }
            });
        }
    });
}

// 我的闲置点击判断是否登录
$('.head ul a').eq(7).click(function() {
    judgeLogin();
});

// 滑过头像显示下拉信息

// 跳转页面时判断是否登录
function judgeLogin() {
    $.ajax({
        type: "post",
        dataType: "json",
        url: "JudgeLoginServlet?method=judge",
        success: function(msg) {
            if (msg.judge === "") {
                alert('您尚未登录，请先登录');
                $(location).attr('href', window.location.href);
                return false;
            } else {
                socketOpen(msg.id, msg.ip);
            }
        }
    });
}

// 回车按下按钮
function enterClick(parent) {
    $(parent + " [type='text']").keyup(function(event) {
        if (event.keyCode === '13')
            $(parent + " [type='sumit']").trigger('click');
    });
}

for (let index = 0; index < $('.container form').length; index++) {
    $('.container form').each(function(index, element) {
        enterClick('.container form:eq(' + index + ')');
    });
}


function changeClick() {
    $('.changePassword .getCode').unbind();
    $('.changePassword .getCode').click(function() {
        if ($('.changePassword .mobileTip').html() === '' && $('.changePassword .mobileText').val() != '') {
            let data = {
                "mobile": $('.changePassword input').eq(0).val()
            };
            $.ajax({
                type: "post",
                url: "RegisterServlet?method=repeat",
                dataType: "text", // 返回数据类型
                data: data,
                success: function(msg) {
                    if ($('.changePassword .mobileTip').html() === '' && $('.changePassword .mobileText').val() != '') {
                        let data = {
                            "mobile": $('.changePassword input').eq(0).val()
                        };
                        if (msg === "registered") {
                            $.ajax({
                                type: "post",
                                url: "UserServlet?method=sendcode",
                                dataType: "text", // 返回数据类型
                                data: data,
                                success: function(msg) {
                                    $('.changePassword .mobileTip').html("");
                                    clearTimeout(codeLost);
                                    $('.changePassword .getCode').remove();
                                    $('.changePassword .mobileCode').append(
                                        "验证码<span class='countDown'>60</span>s后可重新发送");
                                    $('.countDown:eq(0)').html(60);
                                    countTime('.countDown:eq(0)');
                                    setTimeout(
                                        function() {
                                            $('.changePassword .mobileCode').html("");
                                            $('.changePassword .mobileCode').append(
                                                "<button class='getCode'>重新发送验证码</button>");
                                            changeClick();
                                        }, 61000);
                                    var codeLost = setTimeout(function() {
                                        $.ajax({
                                            type: "post",
                                            url: "userServlet?method=Timeout",
                                            dataType: "text", // 5分钟结束后向后台发送请求
                                            success: function(newdata) {}
                                        });
                                    }, 300000);
                                }
                            });
                        } else {
                            $('.changePassword .mobileTip').html('该手机号码仍未被注册');
                        }
                    }
                },
                error: function() {
                    console.log("error");
                }
            });
        }
        return false;
    })
}

//修改密码
$('.changePassword .btn').click(function() {
    let data = {
        'mobile': $('.changePassword .mobileText').val(),
        'code': $('.changePassword input').eq(1).val(),
        'password': $('.changePassword input').eq(2).val()
    }
    $.ajax({
        data: "post",
        dataType: "text",
        data: data,
        url: "UserServlet?method=change",
        success: function(msg) {
            if (msg === 'success') {
                alert('密码修改成功');
                $('form').fadeOut(300);
                setTimeout(function() {
                    $('form:eq(0)').fadeIn(300);
                }, 300);
            } else {
                alert('密码修改失败');
            }
        }
    });
    return false;
});

$('.forgetPassword').click(function() {
    $('form').fadeOut(300);
    setTimeout(function() {
        $('.changePassword').fadeIn(300);
    }, 300);
});

$('.gotoLogin').click(function() {
    $('form').fadeOut(300);
    setTimeout(function() {
        $('.login').fadeIn(300);
    }, 300);
});

function socketOpen(user,ip) {

    var wsurl = "ws://" + ip + ":8080/webchat/" + user; //声明一个变量存储服务器地址
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
    };

    //客户端接收服务端数据时触发
    ws.onmessage = function(e) {

        var result = eval("(" + e.data + ")");
        var from = result.from;
        var time = result.time;
        var content = result.msg;
        var currentUser = user; //当前窗口的用户
        //显示在线用户列表
        users = result.userlist.toString().split("@");
        // 打开收到信息的聊天界面
        if (from != '' && result.id != "") {
            $('.icon-chat-bubble-dots').addClass('newChat')
                .attr('href', 'newChat.html?id=' + result.id[result.id.length - 1] + "&from=" + result.from[result.id.length - 1])
                .click(function() {
                    $('.icon-chat-bubble-dots').removeClass('newChat');
                });
        }
        //        else{
        //        	$('.icon-chat-bubble-dots').removeClass('newChat');
        //        }
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
}