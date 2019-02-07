let table = tableContent();
let fileData = new FormData();
$(function() {
    //注销操作
    $('.outOfLogin').click(function() {
        if (confirm('是否确定注销')) {
            $.ajax({
                type: "post",
                dataType: "text",
                url: "LoginServlet?method=loginout",
                success: function(msg) {
                    if (msg == "success")
                        location.replace("managerLogin.html");
                }
            });
        }
    });
    imgShow();
    let nav = 0;
    $('.pageTurn').hide();
    $('header div li').eq(nav).attr('class', 'haveSelected').siblings('li').attr('class', 'notSelected');
    $('.tableBlock').eq(nav).show().siblings('.tableBlock').hide();
    buttonClick(nav);
    dataDisplay(nav);
    wholeHandle();
    pageTurn(nav);
    getdataChange(nav);
    //渲染用户名字
    $.ajax({
        type: "post",
        dataType: "text",
        url: "LoginServlet?method=getname",
        success: function(msg) {
            $('.managerName').html();
        }
    });
});


//按下按钮
function buttonClick(nav) {
    $('header div li').unbind();
    $('.notSelected').each(function(index, element) {
        $(this).mousedown(function() {
            $(this).css('background', ' linear-gradient(rgb(204, 204, 204) 20%,  rgb(255, 255, 255) 145%)');
        });
    });
    $('.haveSelected').mousedown(function() {
        $(this).css('background', ' linear-gradient(rgb(255, 153, 0) 20%,  rgba(255, 153, 0, 0.2) 145%)');
    });
    //使按钮弹回
    $('html').mouseup(function() {
        $('.haveSelected').css('background', 'linear-gradient(rgba(255, 153, 0, 0.2) 15%, rgb(255, 153, 0))');
        $('.notSelected').css('background', 'linear-gradient(rgb(255, 255, 255) 35%, rgb(102, 102, 102) 145%)');
    });
    // 更改导航标签
    $('header div li').each(function(index) {
        $(this).mouseup(function() {
            nav = index;
            $('header div li').eq(nav).attr('class', 'haveSelected').siblings('li').attr('class', 'notSelected');
            if (nav - 1 >= 0) {
                $('.managerTitle').html($('.managerType').eq(nav - 1).html());
                $('.pageTurn').show();
            } else {
                $('.managerTitle').html("首页");
                $('.pageTurn').hide();
            }
            $('.tableBlock').eq(nav).show().siblings('.tableBlock').hide();
            buttonClick(nav);
            $('.currentPage').html(1);
            dataDisplay(nav);
            console.log(nav);
        });
    });
    $('.managerType').each(function(index) {
        $(this).click(() => {
            $(this).unbind();
            $('header div li').eq(index + 1).trigger('mouseup');
        });
    });
}

// 数据渲染
function dataDisplay(nav) {
    let servlet = setServlet(nav);
    data = {
        "page": $('.currentPage').eq(0).html(),
        "rows": $('.rowsSelect option:checked').html()
    };
    if (nav != 0) {
        $.ajax({
            type: "post",
            dataType: "json",
            data: data,
            url: servlet + "?method=GetList",
            success: function(message) {
                if (parseInt(data.rows) > 10) {
                    $('.tableBlock').css('overflow-y', 'scroll');
                } else {
                    $('.tableBlock').css('overflow-y', 'visible');
                }
                $('table').children('tr').remove();
                //绘制空表格
                console.log(data.currnetPage);
                $('tbody').eq(nav - 1).children('tr').remove();
                for (let index = 0; index < data.rows; index++) {
                    $('tbody').eq(nav - 1).append(table[nav - 1]);
                }
                $('.changeBlock').hide();
                if (nav == 1) {
                    console.log(message);
                    for (let index = 0; index < message.rows.length; index++) {
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('input').eq(0).val(message.rows[index].id);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('input').eq(1).val(message.rows[index].name);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('input').eq(2).val(message.rows[index].mobile);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('input').eq(3).val(message.rows[index].QQ);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('input').eq(4).val(message.rows[index].wechat);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('input').eq(5).val(message.rows[index].address);
                    }
                    userChange(message);
                    userSave();
                } else if (nav == 2) {
                    for (let index = 0; index < message.rows.length; index++) {
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(0).html(message.rows[index].id);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(1).html(message.rows[index].name);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(2).html(message.rows[index].price);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(3).html(message.rows[index].ways);
                    }
                    goodsHandle();
                } else if (nav == 3) {
                    for (let index = 0; index < message.rows.length; index++) {
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(0).html(message.rows[index].id);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(1).html(message.rows[index].content);
                    }
                    announceEdit();
                } else if (nav == 4) {
                    for (let index = 0; index < message.rows.length; index++) {
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(0).html(message.rows[index].id);
                        $('tbody:eq(' + (nav - 1) + ') tr').eq(index).children('td').children('span').eq(1).html(message.rows[index].satisfation);
                    }
                    feedbackHandle();
                }
                $('.pageSelect *').remove();
                for (let index = 1; index <= message.total / data.rows + 1; index++) {
                    $('.pageSelect').append("<option>" + index + "</option>")
                }
                $('.pageSelect option:checked').html(data.currnetPage);
                $('.wholePage').html(Math.floor(message.total / data.rows + 1));
                $('.totalData').html(message.total);
                pageTurn(nav);
                dataDelete(nav);
                getdataChange(nav);
                turnHide(nav);
            }
        });
    }
}


// 所有表格操作
function wholeHandle() {
    userChange();
    userSave();
    goodsHandle();
    announceEdit();
    feedbackHandle();
    newAdd();
}


//对表格数据操作
// 用户数据修改
function userChange(msg) {
    $('.dataChange').unbind();
    $('.dataChange').each(function(index, element) {
        $(this).click(function() {
            $('.userChange').slideDown();
            $('.mainBg').show();
            userSave();
            $('.userChange input').eq(0).val($('.userM tbody tr').eq(index).children("td").children('input').eq(0).val());
            $('.userChange input').eq(1).val($('.userM tbody tr').eq(index).children("td").children('input').eq(1).val());
            $('.userChange input').eq(2).val($('.userM tbody tr').eq(index).children("td").children('input').eq(2).val());
            $('.userChange input').eq(3).val($('.userM tbody tr').eq(index).children("td").children('input').eq(3).val());
            if (msg.QQhide == 'checked')
                $('.userChange input').eq(4).attr('checked', msg.QQhide);
            else
                $('.userChange input').eq(4).attr('checked', false);
            $('.userChange input').eq(5).val($('.userM tbody tr').eq(index).children("td").children('input').eq(4).val());
            if (msg.wechatHide == 'checked')
                $('.userChange input').eq(4).attr('checked', msg.wechatHide);
            else
                $('.userChange input').eq(4).attr('checked', false);
            $('.userChange input').eq(7).val($('.userM tbody tr').eq(index).children("td").children('input').eq(5).val());
        });
    });
    // 点击按钮关闭添加
    $('.userAdd button').eq(1).click(function() {
        $('.userAdd').slideUp();
        $('.mainBg').hide();
    });
    $('.userAdd input').eq(0).keyup(function() {
        let mobileCheck = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        if (!$('.userAdd input').eq(0).val().match(mobileCheck))
            $('.mobileError').css('opacity', 1);
        else
            $('.mobileError').css('opacity', 0);
    })

    //添加新的内容
    $('.userAdd button').eq(0).click(function() {
        let mobileCheck = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        let data = {
            "mobile": $('.userAdd input').eq(0).val(),
            "password": $('.userAdd input').eq(1).val()
        }
        if (data.mobile.match(mobileCheck) && data.password != "") {
            $.ajax({
                type: "post",
                dataType: "text",
                data: data,
                url: "BuyerServlet?method=Add",
                success: function(msg) {
                    if (msg == 'success') {
                        alert('保存成功');
                        $('.userAdd input').val("");
                        $('.userAdd').slideUp();
                        $('.mainBg').hide();
                    }
                }
            })
        }
    });

}

// 用户数据保存
function userSave() {
    let servlet = setServlet(1);
    $('.userChange button').unbind();
    $('.userChange button').eq(0).click(function() {
        let msg = $('.userChange>p>input');
        let choose = $(".userChange>p input[type='checkbox']");
        let hide = [false, false];
        if (choose.eq(0).is(":checked")) {
            hide[0] = "checked";
        }
        if (choose.eq(1).is(":checked")) {
            hide[1] = "checked";
        }
        let data;
        let type;
        if ($('.userChange button').eq(0).html() == '保存')
            type = "Save";
        else
            type = "Add";
        data = {
            "id": msg.eq(0).val(),
            "name": msg.eq(1).val(),
            "address": msg.eq(5).val(),
            "QQ": msg.eq(3).val(),
            "wechat": msg.eq(4).val(),
            "mobile": msg.eq(2).val(),
            "wechatHide": hide[0],
            "QQHide": hide[1],
            "currentPage": $('.currentPage').html()
        };
        $.ajax({
            type: "post",
            dataType: "text",
            data: data,
            url: servlet + "?method=" + type,
            success: function(message) {
                alert('保存成功');
                dataDisplay(1);
                $('.mainBg').hide();
            }
        });
    });
    $('.userChange button').eq(1).click(function() {
        $('.userChange').slideUp();
        $('.userChange input').val("");
        $('.mainBg').hide();
    });
}
//商品操作
function goodsHandle() {
    let servlet = setServlet(2);
    var clickId;
    $('.goodsHandle button').unbind();
    $('.dataLook').each(function(index) {
        $(this).click(function() {
            let data = {
                "id": $('.goodsM tr').eq(index + 1).children('td').eq(0).children('span').html()
            };
            clickId = data.id;
            $.ajax({
                type: "post",
                dataType: "json",
                data: data,
                url: servlet + "?method=Edit",
                success: function(msg) {
                    $('.mainBg').show();
                    $('.goodsChange').slideDown();
                    $('.goodsChange button').eq(0).html('保存');
                    $('.goodsChange img').attr('src', msg.filename);
                    $('.goodsChange .gooodsName input').val(msg.name);
                    $('.goodsChange .goodsIntroduce input').val(msg.introduce);
                    $('.goodsChange .goodsPrice input').val(msg.price);
                    $('.goodsChange .goodsWays input').val(msg.ways);
                    if (msg.bargin == 1)
                        $('.goodsChange .goodsBargin input').val('是');
                    else
                        $('.goodsChange .goodsBargin input').val('否');
                    $('.goodsChange .goodsClassify input').eq(0).val(msg.parentClassify);
                    $('.goodsChange .goodsClassify input').eq(0).val(msg.childClassify);
                }
            });
        });
    });
    $('.goodsHandle button').eq(0).click(function() {
        let type;
        let name = $('.goodsChange .gooodsName input').val();
        let introduce = $('.goodsChange .goodsIntroduce input').val();
        let price = $('.goodsChange .goodsPrice input').val();
        let ways = $('.goodsChange .goodsWays input').val();
        if ($('.goodsChange .goodsBargin input').val() == '是')
            var bargin = 1;
        else
            var bargin = 0;
        let parent = $('.goodsChange .goodsClassify input').eq(0).val();
        let child = $('.goodsChange .goodsClassify input').eq(0).val();
        let data = {
            "id": clickId,
            "name": name,
            "introduce": introduce,
            "price": price,
            "ways": ways,
            "bargin": bargin,
            "parentClassify": parent,
            "childClassify": child
        }
        if ($('.goodsHandle button').eq(0).html() == '保存')
            type = "Save";
        else
            type = "Add";
        $.ajax({
            type: "post",
            dataType: "text",
            url: servlet + "?method=img",
            data: fileData,
            cache: false,
            processData: false,
            contentType: false,
            complete: function() {
                $.ajax({
                    type: "post",
                    dataType: "text",
                    url: servlet + "?method=" + type,
                    data: data,
                    success: function(msg) {
                        if (msg == "success") {
                            alert('修改保存成功');
                            $('.goodsHandle button').eq(1).trigger('click');
                        } else
                            alert('保存失败');
                    }
                });
            }
        });
    });
    $('.goodsHandle button').eq(1).click(function() {
        $('.mainBg').hide();
        $('.goodsChange').hide();
    });
}





// 公告编辑
function announceEdit() {
    let servlet = setServlet(3);
    var clickId;
    $('.editAnnounce button').unbind();
    $('.dataEdit').unbind();
    $('.dataEdit').each(function(index) {
        $(this).click(function() {
            let data = {
                "id": $('.announcesM tr').eq(index + 1).children('td').children('span').eq(0).html()
            };
            clickId = data.id;
            $.ajax({
                type: "post",
                dataType: "json",
                data: data,
                url: servlet + "?method=Edit",
                success: function(msg) {
                    $('.mainBg').fadeIn();
                    $('.editAnnounce button').eq(0).html('保存');
                    $('.editAnnounce').slideDown();
                    $('.editAnnounce input').val(msg.title);
                    $('.editAnnounce .announceTime').val(msg.time);
                    $('.editAnnounce textarea').val(msg.content);
                }
            });
        });
    });

    // 保存公告编辑
    $('.editAnnounce button').eq(0).click(function() {
        let type;
        let servlet = setServlet(3);
        let date = new Date();
        let thisDate = date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + "   " + date.getHours() + ":" + date.getMinutes();
        if ($('.editAnnounce button').eq(0).html() == "保存")
            type = "Save";
        else
            type = "Add";
        let data = {
            "id": clickId,
            "title": $('.editAnnounce input').val(),
            "content": $('.editAnnounce textarea').val(),
            "time": thisDate
        }
        $.ajax({
            type: "post",
            dataType: "text",
            data: data,
            url: servlet + "?method=" + type,
            success: function(msg) {
                if (msg == "success") {
                    alert('修改保存成功');
                    $('.editAnnounce button').eq(1).trigger('click');
                } else
                    alert('修改保存失败');
            }
        });
    });

    //返回到公告管理
    $('.editAnnounce button').eq(1).click(function() {
        $('.mainBg').hide();
        $('.editAnnounce').hide();
    });
}


//反馈操作
function feedbackHandle() {
    let servlet = setServlet(4);
    $('.lookFeedback button').unbind();
    $('.feedBackM .dataLook').unbind();
    $('.feedBackM .dataLook').each(function(index) {
        $(this).click(function() {
            console.log($('.feedBackM tr').eq(index + 1).children('td').eq(0).children('span').html());
            let data = {
                "id": $('.feedBackM tr').eq(index + 1).children('td').eq(0).children('span').html()
            };
            $.ajax({
                type: "post",
                dataType: "json",
                data: data,
                url: servlet + "?method=Edit",
                success: function(msg) {
                    $('.mainBg').fadeIn();
                    $('.lookFeedback').slideDown();
                    $('.announceTime').parent('p').show();
                    $('.feedbackPerson input').val(msg.person);
                    $('.feedbackTime input').val(msg.time);
                    $('.feedbackSatisify input').val(msg.satisfation);
                    $('.lookFeedback textarea').val(msg.content);
                }
            });
        });
    });
    $('.lookFeedback button').click(function() {
        $('.mainBg').hide();
        $('.lookFeedback').hide();
    });
}

//新增内容
function newAdd() {
    $('.newAdd').each(function(index) {
        $('.newAdd').eq(index).click(function() {
            $('.changeBlock').eq(index + 1).slideDown();
            $('.mainBg').show();
            $('.announceTime').parent('p').hide();
            if (index < 4) {
                $('.changeBlock:eq(' + (index + 1) + ') button').eq(0).html('新增');
            }
        });
    });
}

// 数据删除
function dataDelete(nav) {
    let servlet = setServlet(nav);
    $('.dataDelete').unbind();
    $('.dataDelete').each(function(index, element) {
        $(this).click(function() {
            if (confirm('是否确定删除该条数据')) {
                let id;
                if (nav == 1) {
                    console.log($(this).parent().parent().children('td').children('input').eq(0).val());
                    id = $(this).parent().parent().children('td').children('input').eq(0).val();
                } else {
                    id = $(this).parent().parent().children('td').children('span').eq(0).html();
                }
                data = {
                    "id": id
                }
                $.ajax({
                    type: "post",
                    dataType: "text",
                    data: data,
                    url: servlet + "?method=Delete",
                    success: function(msg) {
                        alert("删除成功");
                        dataDisplay(nav);
                    }
                });
            }
        });
    });
}


// 页数的变换
function pageTurn(nav) {
    $('.pageTurn span').unbind();
    $('.prePage').click(function() {
        if ($('.currentPage').html() > 1) {
            $('.currentPage').html(parseInt($('.currentPage').html()) - 1);
            turnHide();
            dataDisplay(nav);
        }
    });
    $('.nextPage').click(function() {
        if (parseInt($('.currentPage').html()) < parseInt($('.wholePage').html())) {
            $('.currentPage').html(parseInt($('.currentPage').html()) + 1);
            turnHide();
            dataDisplay(nav);
        }
    });
    $('.firstPage').click(function() {
        $('.currentPage').html(1);
        dataDisplay(nav);
    });
    $('.lastPage').click(function() {
        $('.currentPage').html($('.wholePage').html());
        turnHide();
        dataDisplay(nav);
    });
}

//隐藏首页和尾页
function turnHide(nav) {
    if ($('.currentPage').html() == 1)
        $('.firstPage').removeClass('turnHover').unbind();
    else
        $('.firstPage').addClass('turnHover');
    if ($('.currentPage').html() == $('.wholePage').html())
        $('.lastPage').removeClass('turnHover').unbind();
    else
        $('.lastPage').addClass('turnHover');
}

function tableContent() {
    let length = $('table').length;
    var table = [];
    for (let index = 0; index < length; index++) {
        table[index] = $('table').eq(index).children('tbody').eq(0).html();
    }
    return table;
}


//设置servlet
function setServlet(nav) {
    if (nav == 1)
        return "BuyerServlet";
    else if (nav == 2)
        return "ProductServlet";
    else if (nav == 3)
        return "NoticeServlet";
    else if (nav == 4)
        return "FeedbackServlet";
}


function getdataChange(nav) {
    //改变行数
    $('.rowsSelect').unbind();
    $('.pageSelect').unbind();
    $('.rowsSelect').change(function() {
        dataDisplay(nav);
    });
    //改变页数
    $('.pageSelect').change(function() {
        $('.currentPage').html($('.pageSelect option:checked').html());
        $('.pageSelect').val($('.pageSelect option:checked').html());
        dataDisplay(nav);
    });
}

function imgShow() {
    //商品图片上传
    $('.goodsImg input').change(function() {
        let file = this.files[0];
        fileData = imgUpload(file, ".goodsImg");
    });
    //用户图片上传
    // $('.userImg input').change(function() {
    //     let file = this.files[0];
    //     fileData = imgUpload(file, ".userImg");
    // });
}

// 商品图片渲染到页面
function imgUpload(file, node) {
    console.log(file);
    if ($(node).children('input').val() != "" && window.FileReader) {
        let reader = new FileReader();
        let data = new FormData();
        data.append("files", file);
        reader.readAsDataURL(file); //监听文件读取结束后事件
        reader.onloadend = function(e) {
            $(node).children('img').attr("src", e.target.result); //e.target.result就是最后的路径地址
            console.log(data.files);
        };
        console.log(data);
        return data;
    } else return "";
}