$(function() {
    fileData = new FormData();
    let index = locationExtract('nav');
    $('.informationNav>ul>li').eq(navStyle(locationExtract('nav')))
        .css({ 'background-color': 'rgb(255,115,0)', 'color': 'rgb(255,255,255)' })
        .hover(function() {
            $(this).children('a').css('color', 'rgb(255, 255, 255)');
        }, function() {
            $(this).children('a').css('color', 'rgb(0, 0, 0)');
        })
        .siblings('li').each(function(index, elemnt) {
            $(this).hover(function() {
                $(this).children('a').css('color', 'rgb(255, 115, 0)');
            }, function() {
                $(this).children('a').css('color', 'rgb(0, 0, 0)');
            });
        });
    $('.informationNav>ul>li').eq(navStyle(locationExtract('nav'))).children('ul').children('li').hover(function() {
        $(this).children('a').css('color', 'rgb(255, 255, 255)');
    }, function() {
        $(this).children('a').css('color', 'rgb(0, 0, 0)');
    });
    $('.informationNav>ul>li').each(function(index) {
        $(this).hover(function() {
            $('.informationNav>ul>li').eq(index).children('ul').stop();
            $('.informationNav>ul>li').eq(index).children('ul').slideDown();
        }, function() {
            $('.informationNav>ul>li').eq(index).children('ul').stop();
            $('.informationNav>ul>li').eq(index).children('ul').slideUp();
        });
    });
    // 数据渲染
    //    渲染头像和名字
    $.ajax({
        type: "post",
        dataType: "json",
        url: "UserServlet?method=user",
        success: function(msg) {
            $('.userImg img').eq(0).attr('src', msg.img);
            $('.userName').html(msg.name);
        }
    });
    // 渲染我的闲置
    switch (locationExtract('nav')) {
        case "0":
            myInformation();
            break;
        case "4":
            dataRender('sell');
            $('.changeInformation').append('<ul>' + $('.goodsInformation').eq(0).html() + '</ul>');
            break;
        case "5":
            dataRender('request');
            $('.changeInformation').append('<ul>' + $('.goodsInformation').eq(1).html() + '</ul>');
            break;
        case "6":
            dataRender('rent');
            $('.changeInformation').append('<ul>' + $('.goodsInformation').eq(2).html() + '</ul>');
            break;
        case "7":
            dataRender('trading');
            break;
        case "8":
            dataRender('sold');
            break;
        case "9":
            dataRender('free');
            break;
        default:
            break;
    }

    $('.changeInformation').append("<p><span class='restoreChange'>保存</span><span class='cancleChange'>取消</span></p>")

    $('.block').eq(index).siblings().remove();
    imgShow();
    $('select').eq(0).change(function() {
        $('select:eq(1) option').remove();
        $('select').eq(1).append('<option>小分类</option>');
        $('select option:selected').eq(1).text("小分类");
        if ($('select option:selected').eq(0).text() != '大分类') {
            let data = { 'parentClassify': $('select option:selected').eq(0).text() };
            $.ajax({
                type: "post",
                dataType: "json",
                url: "UpLoadServlet?method=classify",
                data: data,
                success: function(msg) {
                    $('select').eq(1).attr('disabled', false);
                    for (let index = 0; index < msg.childClassify.length; index++) {
                        $('select').eq(1).append("<option>" + msg.childClassify[index] + "</option>");
                    }
                }
            });
        } else {
            $('select').eq(1).attr('disabled', true);
            $('select option:selected').eq(1).text('小分类');
        }
    });
    dataVideo = videoShow();
});

function navStyle(nav) {
    if (nav <= 1)
        return nav;
    else if (nav > 1 && nav <= 3)
        return 1;
    else if (nav > 3 && nav <= 6)
        return 2;
    else if (nav > 6)
        return nav - 4;
}

// 物品渲染
function dataRender(method) {
    $.ajax({
        type: "post",
        dataType: "json",
        url: "SeekServlet?method=" + method,
        success: function(msg) {
            let goods = $('.userGoods').html();
            $('.userGoods').remove();
            for (let index = 0; index < msg.goodsName.length; index++) {
                $('.block').append("<div class='userGoods'></div>");
                $('.userGoods').eq(index).html(goods);
                $('.goodsName').eq(index).html(msg.goodsName[index]);
                $('.goodsLink').eq(index).attr('href', 'concreteGoods.html?id=' + msg.goodsID[index]);
                $('.goodsImg').eq(index).attr('src', msg.goodsImg[index][1]);
                $('.goodsPrice').eq(index).html('￥' + msg.goodsPrice[index]);
                $('.goodsIntroduce').eq(index).html(msg.goodsIntroduce[index]);
                if ($('.goodsSource').length > 0)
                    $('.goodsSource').html(msg.address[index]);
                if ($('.goodsCount').length > 0)
                    $('.goodsCount').html(msg.Stock[index]);
                if (locationExtract('nav') === "7") {
                    if (msg.status[index] == 1) {
                        $('.goodsType').eq(index).html('售');
                    } else if (msg.status[index] == 2) {
                        $('.goodsType').eq(index).html('求');
                    }
                }
                if (locationExtract('nav') == 1 || locationExtract('nav') == 2) {
                    $('.parentClassify').eq(index).html(msg.parentClassify[index]);
                    $('.childClassify').eq(index).html(msg.childClassify[index]);
                }
            }
            deleteGoods(msg);
            changeGoods(msg);
            finishTrad(msg);
        }
    });
}

// 提交商品信息
$('.upload').click(function() {
    if (!$('.getWays>input').is(':checked')) {
        $('.waysError:eq(0)').show();
    }
    if ($('.goodsInformation>p:eq(0)>input').val() == "") {
        $('.nameError:eq(0)').show();
    }
    // 发布闲置
    if (locationExtract('nav') == 1 || locationExtract('nav') == 3) {
        if (judgeNum($('.price').eq(0)) && $('.goodsInformation>p:eq(0)>input').val() != "" && $('select option:selected').eq(1).text() != '小分类') {
            if (confirm('是否确定发布该闲置物品')) {
                //let file = $('.uploadImg input').files[0];
                uploadData();
            }
        }
        if (!judgeNum($('.price').eq(1))) {
            $('.numError').show();
        }
        if ($('select option:selected').eq(1).text() == '小分类')
            $('.classifyError').show();
    }
    // 发布寻求 
    else if (locationExtract('nav') == 2) {
        if ($('.goodsInformation>p:eq(0)>input').val() != "" && $('select option:selected').eq(1).text() != '小分类') {
            if (confirm('是否确定发布该寻求')) {
                uploadData();
            }
        }
    }
});

// 删除我的闲置
function deleteGoods(msg) {
    $('.userGoods').each(function(index, element) {
        $('.deleteGoods').eq(index).click(function() {
            if (confirm('是否确定删除该闲置')) {
                let type;
                if (locationExtract('nav') == 4)
                    type = 'sell';
                else if (locationExtract('nav') == 5)
                    type = 'request';
                else if (locationExtract('nav') == 6)
                    type = "rent";
                else if (locationExtract('nav') == 7)
                    type = 'trading'
                let data = {
                    "goodsID": msg.goodsID[index],
                    "type": type
                };
                $.ajax({
                    type: "post",
                    dataType: "json",
                    data: data,
                    url: "SeekServlet?method=delete",
                    success: function(msg) {
                        if (msg.judge == 'success') {
                            alert('闲置已被删除');
                            $(location).attr('href', window.location.href);
                        }
                    }
                });
            }
        });
    });
}

// 修改闲置
function changeGoods(msg) {
    for (let index = 0; index < msg.goodsName.length; index++) {
        $('.changeGoods').eq(index).click(function() {
            $('.cancleChange').unbind();
            $('.cancleChange').click(function() {
                $('.changeInformation .icon-cancel').trigger('click');
            });
            $('.coverBg').show();
            $('.changeInformation').show();
            // 出售修改
            $('.changeInformation .uploadImg img').attr('src', msg.goodsImg[index][1]);
            $('.changeInformation .name input').val(msg.goodsName[index]);
            $('.changeInformation .introduce input').val(msg.goodsIntroduce[index]);
            $('.changeInformation .price input').val(msg.goodsPrice[index]);
            $('.changeInformation select option:selected').eq(0).text(msg.parentClassify[index]);
            $('.changeInformation select option:selected').eq(1).text(msg.childClassify[index]);
            $('.changeInformation .select').eq(1).attr('disabled', false);
            if($('.goodsCount').length>0)
            	$('.goodsCount').html(msg.Stock[index]);
            $('.uploadImgs').remove();
            $('.changeInformation .video').remove();
            for (let i = 0; i < 3; i++) {
                if (msg.goodsWays[index] == $('.changeInformation .getWays span').eq(i).html())
                    $('.changeInformation .getWays input').eq(i).attr('checked', true);
            }
            if (msg.goodsBargin[index] == '1')
                $('.changeInformation .bargin input').eq(0).attr('checked', true);
            else
                $('.changeInformation .bargin input').eq(1).attr('checked', true);
            if ($('.changeInformation .stock').length > 0)
                $('.changeInformation .stock').val(msg.Stock);
            // 上传修改内容
            $('.changeInformation .restoreChange').click(function() {
                let data = imgUpload(imgfiles, ".changeInformation .uploadImg");
                $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "UpLoadServlet?method=img",
                    data: data,
                    cache: false,
                    processData: false,
                    contentType: false,
                    complete: function() {
                        let bargin;
                        if ($('.bargin input').eq(0).is(':checked'))
                            bargin = 1;
                        else
                            bargin = 0;
                        // 判断取货方式
                        let ways;
                        for (let index = 0; index < $('.getWays input').length; index++) {
                            if ($('.getWays input:eq(' + index + ')').is(':checked')) {
                                ways = $('.getWays span').eq(index).html();
                            }
                        }
                        let haveImg = 'T';
                        if ($(".uploadImg input").val() == "")
                            haveImg = 'F';
                        let data = {
                            'goodsName': $('.changeInformation .name input').val(),
                            'goodsIntroduce': $('.changeInformation .introduce input').val(),
                            'goodsPrice': $('.changeInformation .price input').val(),
                            'isBargin': bargin,
                            'goodsPath': $(".uploadImg input").val(),
                            'haveImg': haveImg,
                            'ways': ways,
                            'id': msg.goodsID[index],
                            "stock": $('.changeInformation .stock input').val(),
                            "status": locationExtract('nav'),
                            'parentClassify': $('select option:selected').eq(0).text(),
                            'childClassify': $('select option:selected').eq(1).text()
                        };
                        $.ajax({
                            type: "post",
                            dataType: "text",
                            url: "UpLoadServlet?method=text",
                            data: data,
                            success: function(msg) {
                                if (msg == 'true')
                                    $(location).attr('href', window.location.href);
                            },
                            error: function() {
                                alert('上传失败');
                            }
                        });
                    },
                    error: function() {
                        console.log('图片上传失败');
                    }
                });
            });
        });
    }
}

// 点击关闭修改框
$('.changeInformation .icon-cancel').click(function() {
    $('.changeInformation').hide();
    $('.coverBg').hide();
});
// 完成交易
function finishTrad(msg) {
    $('.finishGoods').each(function(index, element) {
        $(this).click(function() {
            let data = { 'id': msg.goodsID[index] };
            $.ajax({
                type: "post",
                dataType: "json",
                url: "SeekServlet?method=finish",
                data: data,
                success: function(msg) {
                    if (msg.judge = 'success') {
                        if (confirm('是否确认交易已完成')) {
                            alert('交易已完成');
                            $(location).attr('href', window.location.href);
                        }
                    }
                }
            });
        });
    });
}

//图片预览
imgfiles = [];
imgInput = [];

function imgShow(index = 0, data = new FormData()) {
    $('.uploadImg input').unbind().change(function() {
        if ($('.imgWait img').length >= 4) {
            $('.uploadImg').hide();
        }
        let file = this.files[0];
        imgInput[imgInput.length] = $(".uploadImg input").val();
        imgfiles[index] = file;
        fileData = imgUpload(imgfiles, ".uploadImg");
        if ($('.uploadImgs').children('input').val() != "" && window.FileReader) {
            let reader = new FileReader();
            reader.readAsDataURL(imgfiles[index]); //监听文件读取结束后事件
            reader.onloadend = function(e) {
                if (locationExtract('nav') < 4)
                    $('.uploadImgs').append("<div class='imgWait'><img src=" + e.target.result + "><span class='icon-cancel'></span></div>");
                else
                    $('.uploadImg img').attr('src', e.target.result);

                //删除已添加的图片
                $('.imgWait .icon-cancel').unbind().each(function(i) {
                    $(this).click(function() {
                        $('.uploadImg input').show();
                        while (i < imgInput.length) {
                            imgInput[i] = imgInput[++i];
                        }
                        imgInput.length--;
                        imgShow(index, data);
                        $(this).parent('.imgWait').remove();
                    });
                });
            }; //将添加的图片渲染
        }
        index++;
        imgShow(index, data);
    });
}

// 商品图片渲染到页面
function imgUpload(imgfiles, node) {
    let data = new FormData();
    if ($(node).children('input').val() != "" && window.FileReader) {
        $.each(imgfiles, function(i, file) {
            data.append("files", file);
        });
        return data;
    } else return "";
}

//视频预览
function videoShow() {
    var reader = new FileReader();
    var data = new FormData();
    $('.videoUpload input').unbind().change(function() {
        data.append("files", $(this)[0].files[0]);
        reader.onload = function(e) {
            var maxSize = 100000;
            var maxTime = 60;
            var size = $('.videoUpload input')[0].files[0].size;
            var filesize = (size / 1024).toFixed(2);
            if (filesize < maxSize) {
                $('video').attr('src', e.target.result);
                setTimeout(function() {
                    if ($('video')[0].duration > maxTime) {
                        $('.video').attr('src', '');
                        alert('视频文件时间不可超过一分钟');
                    } else {
                        $(".videoUpload").hide();
                        $('video').show();
                    }
                }, 500);

            } else
                alert("视频文件太大，该视频文件大小为" + filesize + "kb");
        }
        reader.readAsDataURL($(this)[0].files[0]);
        return data;
    })
}


function uploadData() {
    $.ajax({
        type: "post",
        dataType: "json",
        url: "UpLoadServlet?method=img",
        data: fileData,
        crossDomain: true,
        cache: false,
        processData: false,
        contentType: false,
        complete: function() {
            // 判断是否选择了砍价
            let bargin;
            if ($('.bargin input').eq(0).is(':checked'))
                bargin = 1;
            else
                bargin = 0;
            // 判断取货方式
            let ways;
            for (let index = 0; index < $('.getWays input').length; index++) {
                if ($('.getWays input:eq(' + index + ')').is(':checked'))
                    ways = $('.getWays span').eq(index).html();
            }
            let haveImg = 'T';
            if ($(".uploadImg input").val() == "")
                haveImg = 'F';
            var imgSend;
            if (locationExtract('nav') >= 4)
                imgSend = imgInput[imgInput.length - 1];
            else
                imgSend = imgInput.join(";");
            $.ajax({
                type: "post",
                dataType: "json",
                url: "UpLoadServlet?method=video",
                data: dataVideo,
                crossDomain: true,
                cache: false,
                processData: false,
                contentType: false,
                complete() {

                }
            });
            let data = {
                'goodsName': $('.name input').eq(0).val(),
                'goodsIntroduce': $('.introduce input').eq(0).val(),
                'goodsPrice': $('.price input').eq(0).val(),
                'isBargin': bargin,
                'goodsPath': $('.videoUpload input').val() + ";" + imgSend,
                'thisImg': 1,
                'ways': ways,
                'stock': $('.stock input').eq(0).val(),
                'haveImg': haveImg,
                "status": locationExtract('nav'),
                'parentClassify': $('select option:selected').eq(0).text(),
                'childClassify': $('select option:selected').eq(1).text()
            };
            if (haveImg == 'F')
                alert('图片还未上传，请选择上传图片');
            else {
                $.ajax({
                    type: "post",
                    dataType: "text",
                    url: "UpLoadServlet?method=text",
                    data: data,
                    success: function(msg) {
                        if (msg == 'true')
                            $(location).attr('href', window.location.href);
                    },
                    error: function() {
                        alert('上传失败');
                    },
                    complete: function() {
                        imgInput = [];
                    }
                });
            }
        }
    });
}

// 判断价格是否为数字
function judgeNum(node) {
    let int = '^[0-9]*[1-9][0-9]*$',
        float = '^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$';
    if (node.val().match(int) || node.val().match(float)||node.val()==0)
        return true;
    else
        return false;
}

// 输入价格时判断是否为数字
$('.price').eq(1).keyup(function() {
    if (judgeNum($(this)))
        $('.numError').hide();
    else
        $('.numError').show();
});



// 我的资料修改保存
function myInformation() {
    let fileData = new FormData();
    // 获取我的资料并渲染
    $.ajax({
        type: "post",
        dataType: "json",
        url: "UserServlet?method=render",
        success: function(msg) {
            $('.myInformation ul>li').eq(0).children("input[type='text']").val(msg.name);
            $('.myInformation ul>li').eq(1).children("input[type='text']").val(msg.mobile);
            $('.myInformation ul>li').eq(2).children("input[type='text']").val(msg.address);
            $('.myInformation ul>li').eq(3).children("input[type='text']").val(msg.wechat);
            $('.myInformation ul>li').eq(4).children("input[type='text']").val(msg.QQ);
            if (msg.wcHide == 'checked')
                $('.hideChoose').children('input').eq(0).attr('checked', msg.wcHide);
            else
                $('.hideChoose').children('input').eq(0).attr('checked', false);
            if (msg.qqHide == 'checked')
                $('.hideChoose').children('input').eq(1).attr('checked', msg.qqHide);
            else
                $('.hideChoose').children('input').eq(1).attr('checked', false);
        }
    });
    // 点击修改我的资料
    $('.change').eq(0).click(function() {
        $('.userImg').append('<input type="file" name="files">');
        $('.myInformation ul>li').eq(1).siblings('li').children("input[type='text']").attr('disabled', false).css('border-bottom', '2px solid rgb(255,0,0)');
        $('.hideChoose input').removeAttr('disabled');
        $('.userImg img:eq(1)').animate({ opacity: 0.6 }, 500);
        $('.userImg input').change(function() {
            let file = [this.files[0]];
            fileData = imgUpload(file, ".userImg");
            if ($(".userImg").children('input').val() != "" && window.FileReader) {
                let reader = new FileReader();
                reader.readAsDataURL(files[0]); //监听文件读取结束后事件
                reader.onloadend = function(e) {
                    $(".userImg").children('img').attr("src", e.target.result);
                }; //将添加的图片渲染
            }
        });
    });
    $('.restore').eq(0).click(function() {
        if (confirm('是否确定保存个人资料修改')) {
            // 修改头像
            let data = fileData || "";
            $.ajax({
                type: "post",
                dataType: "json",
                url: "UserServlet?method=img",
                data: data,
                cache: false,
                processData: false,
                contentType: false,
                complete: function() {
                    let wcHide = 'false',
                        qqHide = 'false';
                    if ($('.hideChoose').children('input').eq(0).is(':checked'))
                        wcHide = 'checked';
                    if ($('.hideChoose').children('input').eq(1).is(':checked'))
                        qqHide = 'checked';
                    let data = {
                        "name": $('.myInformation ul>li').eq(0).children("input[type='text']").val(),
                        "address": $('.myInformation ul>li').eq(2).children("input[type='text']").val(),
                        "wechat": $('.myInformation ul>li').eq(3).children("input[type='text']").val(),
                        "QQ": $('.myInformation ul>li').eq(4).children("input[type='text']").val(),
                        "wechatHide": wcHide,
                        "QQHide": qqHide
                    }
                    $.ajax({
                        type: "post",
                        dataType: "json",
                        url: "UserServlet?method=save",
                        data: data,
                        success: function(msg) {
                            if (msg.judge == 'success') {
                                $('.myInformation ul li input').attr('disabled', true).css('border-color', 'rgb(0,0,0)');
                                $('.userImg img:last-child').animate({ opacity: 0 }, 500);
                                $('.userImg input').remove();
                                alert("已保存资料修改");
                                $(location).attr('href', window.location.href);
                            } else {
                                alert('个人资料保存失败');
                            }
                        },
                        error: function() {
                            alert('个人资料保存失败');
                        }
                    });
                }
            });
        }
    });
}

// 滑动显示发布
$(document).scroll(function() {
    if ($('.upload').length > 0) {
        if ($(window).scrollTop() > (parseInt($('.upload')[1].offsetTop) + 30)) {
            $('.floatUpload').show();
        } else {
            $('.floatUpload').hide();
        }
    }
});