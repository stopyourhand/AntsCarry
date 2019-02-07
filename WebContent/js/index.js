$(function() {
    for (let index = 0; index < $('.navHoverBlock a').length; index++) {
        $('.navHoverBlock a').eq(index).attr('href', "goods.html?page=0&ways=0&search=" + $('.navHoverBlock a').eq(index).html() + "&classify=待售");
    }
    specialHover(".specialLove");
    specialHover(".specialNew");
    specialHover(".specialHot");
    hrefAdd();
    searchFloat();
    noRequest = $('.noRequest').html();
    $('.noRequest').remove();
    imgSlideMove(0);
    $.ajax({
        type: "post",
        dataType: "json",
        url: "dataServlet",
        success: function(msg) {
            //滑过特殊物品栏显示不同内容
            $('.specialShowNav span').each(function(index, element) {
                $this = $(this);
                $this.hover(function() {
                    $(this).siblings().removeClass('specialNavHover');
                    $(this).addClass('specialNavHover');
                    $('.goodsSpecial').css('display', 'none')
                        .eq(index).css('display', 'block');
                    singleRender(msg, index);
                })
            })
            dataRender(msg);
        },
        error: function() {
            console.log("error");
        }
    })
});

function hrefAdd() {
    for (let index = 0; index < $('.leftNav li>a').length; index++) {
        $('.leftNav li>a').eq(index).attr('href', 'goods.html?page=0&ways=0&classify=' + $('.leftNav li>a').eq(index).html());
    }
}

//数据渲染
function dataRender(msg) {
	$('.dataShow div').append("<span class='icon-spinner'></span>");
    for (let index = 0; index < msg.id.length; index++) {
        if (($(window).scrollTop() + $(window).height()) > $('.dataShow')[index].offsetTop && $('.dataShow')[index].offsetTop > 0)
        setTimeout(function() {
            $('.dataShow:eq('+index+') .icon-spinner').remove();
            singleRender(msg, index);
        }, 100);
        $(document).scroll(function() {
            if (($(window).scrollTop() + $(window).height()) > $('.dataShow')[index].offsetTop && $('.dataShow')[index].offsetTop > 0)
            setTimeout(function() {
            	$('.dataShow:eq('+index+') .icon-spinner').remove();
                singleRender(msg, index);
            }, 100);
        });
    }
    //公告渲染
    let li = "<li><a class=\"releaseContent\" ></a><span class=\"releaseTime\"></span></li>";
    $('.forumInformation>ul *').remove();
    for (let index = 0; index < msg.title.length; index++) {
        $('.forumInformation>ul').append(li);
        $('.releaseContent').eq(index).attr('href', "singleAnnounce.html?id=" + msg.ID[index]);
        $('.releaseContent').eq(index).html(msg.title[index]);
        $('.releaseTime').eq(index).html(msg.time[index]);
    }
}

//单块数据渲染
function singleRender(msg, index) {
    for (let i = 0; i < msg.id[index].length; i++) {
        $('.dataShow:eq(' + index + ') a').eq(i).attr("href", "concreteGoods.html?id=" + msg.id[index][i]);
        $('.dataShow:eq(' + index + ') .goodsName').eq(i).html(msg.name[index][i]);
        $('.dataShow:eq(' + index + ') img').eq(i).attr('src', msg.img[index][i][1]);
    }
}

var slideFlag = true;
// 轮播图播放
function imgSlideMove(i) {
    $('.slideHandle span').eq(i).css('background-color', 'rgb(0,0,0)')
        .siblings().css('background-color', 'rgb(102,102,102)');
    var length = $('.imgSlide img').length;
    $('.imgSlide img').css('display', 'none');
    $('.imgSlide img').eq(i).addClass('imgSlideShow').css('display', 'inline');
    $('.imgBg').attr('src', $('.imgSlide img').eq(i).attr('src'));
    imgHover = setTimeout(function() {
        if (slideFlag) {
            $('.imgSlide img').eq(i).removeClass('imgSlideShow');
            if (i + 1 == length)
                i = 0;
            else
                i++;
            imgSlideMove(i);
        }
    }, 4000);
}

// 轮播图小圆滑过
$('.slideHandle span').each(function(index, element) {
    $this = $(this);
    $this.hover(function() {
        $('.slideHandle span').eq(index).css('background-color', 'rgb(0,0,0)')
            .siblings().css('background-color', 'rgb(102,102,102)');
        $('.imgSlide img').removeClass('imgSlideShow');
        slideFlag = false;
        $('.imgSlide img').css('display', 'none').eq(index).css('display', 'inline');
    }, function() {
        clearTimeout(imgHover);
        imgSlideMove(index);
        slideFlag = true;
    });
});

// 找出当前滑到的点的下标
function findHover() {
    for (let index = 0; index < $('.slideHandle span').length; index++) {
        if ($('.slideHandle span').eq(index).css('background-color') == "rgb(0, 0, 0)")
            return index;
    }
}
//点击向左滑动
$('.icon-chevron-left').click(function() {
    let index = findHover();
    if (index > 0) {
        $('.slideHandle span').eq(index - 1).trigger("mouseover").trigger("mouseout");
    } else {
        $('.slideHandle span').eq(2).trigger("mouseover").trigger("mouseout");
    }
});
//点击向右滑动
$('.icon-chevron-right').click(function() {
    let index = findHover();
    if (index < 2) {
        $('.slideHandle span').eq(index + 1).trigger("mouseover").trigger("mouseout");
    } else {
        $('.slideHandle span').eq(0).trigger("mouseover").trigger("mouseout");
    }
});


$(document).scroll(function() {
    $('.slideSearch>ul').hide();
    searchFloat();
});

// 判断显示浮动搜索框显示回到顶部
function searchFloat() {
    if ($(window).scrollTop() > 400) {
        $('.follow').css('overflow', 'hidden');
        $('.icon-arrow-up').show();
        $('.follow').show(500);
        $('.topBg').css('position', 'fixed').css('z-index', '2').addClass('fadeDown');

    } else {
        $('.follow').css('overflow', 'hidden');
        $('.follow').hide();
        $('.icon-arrow-up').hide();
        $('.topBg').css('position', 'absolute').css('background-color', 'rgb(255,255,255').css('z-index', '-1').removeClass('fadeDown');
    }
}

//判断索引
function judgeIndex(node) {
    let length = $(node).length;
    for (let index = 0; index < length; index++) {
        if ($(node).eq(index).css('display') != 'none')
            return index;
    }
}

// 点击回到顶部块外的滑动块时判断是否登录
$('.icon-arrow-up').siblings('.slideIcon').click(function() {
    judgeLogin();
});

// 图片导航动画
$('.handleBlock').each(function(index, element) {
    $(this).hover(function() {
        $('.handleBlock img').eq(index).stop();
        $('.handleBlock .handleTitle').eq(index).stop();
        $('.handleBlock img').eq(index).animate({
            width: 40,
            height: 40,
            marginTop: 20
        }, 500);
    }, function() {
        $('.handleBlock img').eq(index).stop();
        $('.handleBlock .handleTitle').eq(index).stop();
        $('.handleBlock img').eq(index).animate({
            width: 84,
            height: 84,
            marginTop: 12
        }, 500);
    })
});

// 特殊物品滑过效果
function specialHover(parent) {
    $(parent + ' .specialGoods').each(function(index, element) {
        $(this).hover(function() {
            $(parent + ' .specialGoods img').stop();
            $(parent + ' .specialGoods .goodsName').stop();
            $(parent + ' .specialGoods').stop();
            $(parent + ' .specialGoods .goodsName').eq(index).animate({
                left: 0,
                width: '100%',
                borderRadius:0
            }, 500);
            $(parent + ' .specialGoods').eq(index).animate({
                width: 240
            }, 500);
            for (let i = 0; i < $(parent + ' .specialGoods img').length; i++) {
                if (i != index) {
                    $(parent + ' .specialGoods .goodsName').eq(i).animate({
                        left: "10%",
                        width: '80%',
                        borderRadius:10
                    }, 500);
                    $(parent + ' .specialGoods').eq(i).animate({
                        width: 200
                    }, 500);
                }
            }
        });
    });
}