$(function() {
    var it = animateLogo(500);
    setInterval(function() {
        it.next();
    }, 500);
    search('.slideSearch', '.slideChoose', '.searchGoods');
    search('.search', '.claChoose', '.searchFor');
    if ($(window).scrollTop() > 400)
        $('.icon-arrow-up').show();
    $('.icon-arrow-up').click(function() {
        scrollToTop();
    });

});


// 滑动到顶部
function scrollToTop() {
    setTimeout(function() {
        let scrollStep = $(window).scrollTop() / 5;
        $(window).scrollTop($(window).scrollTop() - scrollStep);
        if ($(window).scrollTop() > 0) {
            scrollToTop();
        } else {
            $(window).scrollTop(0);
        }
    }, 40);
}


function search(search, classify, searchfor) {
    // 在文本框输入内容时显示文本框
    $(searchfor + ' input').keyup(function() {
        let data = {
            "text": $(search + ' input').val(),
            "classify": $(classify + '>span').html()
        };
        // 当文本框为空时清除扩展框内容并隐藏
        if (data.text === "") {
            $(search + '>ul *').remove();
            $(search + '>ul').fadeOut();
        } else if (data.classify == '分类') {
            $(search + ' ' + classify + '>span').html('待售');
            searchShow(data, search, classify, searchfor);
        } else {
            searchShow(data, search, classify, searchfor);
        }
    });
    // 回车触发搜索
    $(searchfor + ' input').keydown(function(event) {
        if (event.keyCode == 13)
            $(searchfor + '>span').trigger('click');
        else if (event.keyCode == 40)
            downChoose();
    });
    // 点击搜索
    $(searchfor + '>span').click(function() {
        let data = {
            "text": $(searchfor + ' input').val(),
            "classify": $(classify + '>span').html()
        };
        if (data === "" || data.classify == '分类') {} else {
            window.open('goods.html?page=0&ways=0&search=' + data.text + '&classify=' + data.classify, '_blank');
        }
    });
    // 阻止事件冒泡
    $(searchfor).click(function(event) {
        event.stopPropagation();
    });

    // 点击界面关闭搜索扩展框
    $('body').click(function() {
        $(search + '>ul').fadeOut();
        $(classify + '>ul').slideUp();
    });

    // 点击分类下拉
    $(classify + '>span').click(function(event) {
        $('.follow').css('overflow', 'visible');
        $(classify + '>ul').slideDown();
        event.stopPropagation();
    });

    // 选中分类
    $(classify + '>ul li').each(function(index, element) {
        $(this).click(function(event) {
            $(classify + '>span').html($(this).html());
            $(search + ' .error').slideUp();
            let data = {
                "text": $(searchfor + ' input').val(),
                "classify": $(classify + '>span').html()
            };
            if (data.text != "")
                searchShow(data, search, classify, searchfor);
            $(classify + '>ul').slideUp();

            event.stopPropagation();
        });
    });

}

// 搜索框显示
function searchShow(data, search, classify, searchfor) {
    $(search + '>ul').fadeIn();
    $('.follow').css('overflow', 'visible');
    $.ajax({
        type: "post",
        dataType: "json",
        url: "SearchServlet?method=keyup",
        data: data,
        success: function(msg) {
            $(search + '>ul *').remove();
            for (let index = 0; index < msg.goodsName.length; index++) {
                console.log(msg.id);
                let name = insertWord(msg.goodsName[index], data.text);
                $(search + '>ul').append("<li><a href=concreteGoods.html?id=" + msg.goodsID[index] + ">" + name + "</a></li>");
            }
        },
        error: function() {
            console.error("error");
        },
        complete: function() {
            console.log("complete");
        }
    });
}

function insertWord(whole, insert) {
    return whole.slice(0, whole.indexOf(insert)) + "<span class='insert'>" + whole.slice(whole.indexOf(insert), whole.indexOf(insert) + insert.length) + "</span>" + whole.slice(whole.indexOf(insert) + insert.length, whole.length);
}




// 按向下键移到下一个选项
function downChoose() {
    let length = $('.search>ul li').length;
    for (let index = 0; index < length; index++) {
        if ($('.search>ul li').eq(index).css('color') == 'rgb(102,102,102)') {
            if (index == length - 1)
                $('.search>ul li').eq(0).css('color', 'rgb(102,102,102)').siblings('li').css('color', 'rgb(0,0,0');
            else
                $('.search>ul li').eq(index + 1).css('color', 'rgb(102,102,102)').siblings('li').css('color', 'rgb(0,0,0');
        }
        $('.search>ul li').eq(0).css('color', 'rgb(102,102,102)').siblings('li').css('color', 'rgb(0,0,0');
    }
}


// logo动画
// function animateLogo(index, time) {
//     if (index > 5)
//         index = 0;
//     if (index == 0) {
//         $('.logoImg>span').show().stop();
//         $('.logoImg>span').animate({
//             width: 163,
//             left: 0
//         }, time);
//     } else if (index == 1) {
//         $('.logoImg>span').show().stop();
//         $('.logoImg>span').animate({
//             height: 33
//         }, time);
//     } else if (index == 2) {
//         $('.logoImg>img').eq(1).stop();
//         $('.logoImg>img').eq(1).slideDown(time, function() {
//             $(this).removeClass('top').addClass('bottom');
//         });
//     } else if (index == 3) {
//         $('.logoImg>img').eq(1).stop();
//         $('.logoImg>img').eq(1).slideUp(time * 1.5, function() {
//             $(this).removeClass('bottom').addClass('top');
//         });
//     } else if (index == 4) {
//         $('.logoImg>span').show().stop();
//         $('.logoImg>span').animate({
//             height: 0
//         }, time);
//     } else if (index == 5) {
//         $('.logoImg>span').stop();
//         $('.logoImg>span').animate({
//             width: 0,
//             left: 83
//         }, time, function() {
//             $(this).hide();
//         });
//     }
//     index++;
//     setTimeout(function() {
//         animateLogo(index, time);
//     }, time * 1.5);
// }

function* animateLogo(time) {
    yield;
    $('.logoImg>span').show().stop();
    $('.logoImg>span').animate({
        width: 163,
        left: 0
    }, time);

    yield;
    $('.logoImg>span').show().stop();
    $('.logoImg>span').animate({
        height: 33
    }, time);
    yield;
    $('.logoImg>img').eq(1).stop();
    $('.logoImg>img').eq(1).slideDown(time, function() {
        $(this).removeClass('top').addClass('bottom');
    });
    yield;
    yield;
    $('.logoImg>img').eq(1).stop();
    $('.logoImg>img').eq(1).slideUp(time * 1.5, function() {
        $(this).removeClass('bottom').addClass('top');
    });
    yield;
    $('.logoImg>span').show().stop();
    $('.logoImg>span').animate({
        height: 0
    }, time);
    yield;
    $('.logoImg>span').stop();
    $('.logoImg>span').animate({
        width: 0,
        left: 83
    }, time, function() {
        $(this).hide();
    });
    yield* animateLogo(time);
}

// 滑过滑块时滑出注释
$('.slider a').each(function(index, event) {
    $this = $(this);
    $this.hover(function() {
        $('.slider span').eq(index).addClass('sliderOut');
        $('.slider span').eq(index).removeClass('sliderHide');
    }, function() {
        $('.slider span').eq(index).addClass('sliderHide');
        $('.slider span').eq(index).removeClass('sliderOut');
    });
});