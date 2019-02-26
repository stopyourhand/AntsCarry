let goodsSort = true;
let dataDisplaying = false;
if (locationExtract('ways') < 3)
    $('.goodsStock').remove();
const goodsShowCol = "<div class='singleShow'>" + $('.singleShow').html() + '</div>';
const goodsShowRow = "<div class='singleShowRow'>" + $('.singleShowRow').html() + '</div>';
$(function() {

    navChange();
    priceHandle();
    sortChange();
    buttonClick();
    $('.goodsShow div').remove();
    let data = {
        "name": locationExtract('classify'),
        "currentPage": locationExtract('page'),
        "status": locationExtract('ways'),
        "type": 0,
        "lowPrice": $('.priceSelect input').eq(0).val(),
        "highPrice": $('.priceSelect input').eq(1).val()
    };
    dataDislay();
    //添加分类
    $.ajax({
        type: "post",
        dataType: "json",
        url: "SaleServlet?method=classify",
        data: data,
        success: function(msg) {
            let liSum = msg.classify.length / 5 + 1;
            for (let index = 0; index < liSum; index++) {
                $('.wholeClassify ul').append("<li><a></a><a></a><a></a><a></a><a></a></li>");
            }
            if (msg.classify != undefined) {
                for (let index = 0; index < msg.classify.length; index++) {
                    $('.wholeClassify ul a').eq(index).attr('href', "goods.html?page=0&ways=0&classify=" + msg.classify[index]);
                    $('.wholeClassify ul a').eq(index).html(msg.classify[index]);
                }
            }
        }
    });
});

function dataDislay() {
    // 根据搜索渲染数据
    if (locationExtract('search')) {
        searchDisplay(0, sortShow());
    }
    //根据分类渲染数据
    else if (locationExtract('classify')) {
        classifyShow(0, sortShow());
    }
}

// 按钮点击样式
function buttonClick() {
    $('.priceSearch').each(function(index) {
        $(this).mousedown(function() {
            $(this).css('background', 'linear-gradient(rgb(255, 153, 0) 20%,  rgba(255, 153, 0, 0.2) 145%)');
        });
        $('html').mouseup(function() {
            $('.priceSearch').eq(index).css('background', 'linear-gradient(rgba(255, 153, 0, 0.2) 2%, rgb(255, 153, 0))');
        });
    });
}

// 展开收回分类
$('.handle').click(function() {
    if ($('.handle').html() == "展开")
        $('.wholeClassify').animate({
            height: 148
        }, 500, function() { $('.handle').html("收回") });
    else
        $('.wholeClassify').animate({
            height: 60
        }, 500, function() { $('.handle').html("展开") });
});

// 页码的设置
function pageSet(wholePage) {
    $('.pageChange>*').remove();
    $('.pageChange').append('<a class="prePage">上一页</a><a class="nextPage">下一页</a>');
    if (wholePage == undefined)
        $('.pageChange').remove();
    else {
        var page = locationExtract('page');
        if (wholePage <= 6) {
            for (let index = 0; index < wholePage; index++) {
                $('.nextPage').before("<a href='goods.html?page=" + index + "&ways=" + locationExtract('ways') + "&classify=" + locationExtract('classify') + "'>" + (index + 1) + "</a>");
            }
        } else {
            if (page <= 2) {
                for (let index = 0; index < 6; index++) {
                    $('.nextPage').before("<a href='goods.html?page=" + index + "&ways=" + locationExtract('ways') + "&classify=" + locationExtract('classify') + "</a>");
                }
            } else if (page >= wholePage - 3) {
                for (let index = page - 2; index < wholePage; index++) {
                    $('.nextPage').before("<a href='goods.html?page=" + index + "&ways=" + locationExtract('ways') + "&classify=" + locationExtract('classify') + "</a>");
                }
            } else {
                for (let index = page - 2; index < page + 4; index++) {
                    $('.nextPage').before("<a href='goods.html?page=" + index + "&ways=" + locationExtract('ways') + "&classify=" + locationExtract('classify') + "</a>");
                }
            }
        }
        let pageLength = $('.pageChange a').length;
        for (let index = 0; index < pageLength; index++) {
            if ($('.pageChange a').eq(index).html() == '' + (page + 1))
                $('.pageChange a').eq(index).css('background-color', 'rgb(255,153,0)')
                .css('color', 'rgb(255,255,255)');
        }
        if (page == 0) {
            $('.prePage').remove();
        } else
            $('.prePage').attr('href', "goods.html?page=" + (page - 1) + "&ways=" + locationExtract('ways') + "&classify=" + locationExtract('classify'));
        if ((page + 1) + '' == $('.pageChange a').eq($('.pageChange a').length - 2).html())
            $('.nextPage').remove();
        else
            $('.nextPage').attr('href', "goods.html?page=" + (page + 1) + "&ways=" + locationExtract('ways') + "&classify=" + locationExtract('classify'));
    }
}

// 导航栏标签变更
function navChange() {
    let ways = locationExtract('ways');
    $('.head li>a').eq(parseInt(ways) + 1).addClass('headSelect');
}


//根据分类渲染
function classifyShow(type, sort) {
    $('.goodsShow div').remove();
    let data = {
        "name": locationExtract('classify'),
        "currentPage": locationExtract('page'),
        "status": locationExtract('ways'),
        "type": type,
        "lowPrice": $('.priceSelect input').eq(0).val(),
        "highPrice": $('.priceSelect input').eq(1).val()
    };
    $.ajax({
        type: "post",
        dataType: "json",
        url: "SaleServlet?method=goods",
        data: data,
        success: function(msg) {
            if (msg.goodsID.length == 0) {
                $('.pageChange').remove();
                $('.goodsShow').append("<img src='img/noGoods.png'>");
            } else {
                pageSet(msg.totalPage);
                let nextLocation = 'goods.html?page=0&ways=0&classify=';
                dataDisplaying = false;
                setTimeout(function() {
                    $('.goodsShow>*').remove();
                    dataDisplaying = true;
                    dataShow(0, msg, sort);
                }, 100);
                if (msg.goodsBargin == '1')
                    $('.isBargin').html('该商品可以讲价');
                if (msg.classify != undefined) {
                    $('.currentNav').html("<a href='goods.html?page=0&amp;ways=0&amp;classify=全部'>全部</a>");
                    for (let index = 0; index < msg.classify.length; index++) {
                        $('.currentNav').append("<a>" + ">" + msg.classify[index] + "</a>");
                        $('.currentNav a').eq(index + 1).attr('href', nextLocation + msg.classify[index]);
                    }
                }
            }
        }
    });
}


//根据搜索渲染
function searchDisplay(type, sort) {
    $('.goodsShow div').remove();
    let data = {
        "name": locationExtract('classify'),
        "currentPage": locationExtract('page'),
        "status": locationExtract('ways'),
        "type": type,
        "lowPrice": $('.priceSelect input').eq(0).val(),
        "highPrice": $('.priceSelect input').eq(1).val()
    };
    $.ajax({
        type: "post",
        dataType: "json",
        url: "SaleServlet?method=search",
        data: {
            "search": locationExtract("search"),
            "classify": locationExtract('classify')
        },
        success: function(msg) {
            if (msg.goodsID.length == 0) {
                $('.pageChange').remove();
                $('.goodsShow').append("<img src='img/noGoods.png'>");
            } else {
                pageSet(msg.totalPage);
                dataDisplaying = false;
                setTimeout(function() {
                    $('.goodsShow>*').remove();
                    dataDisplaying = true;
                    dataShow(0, msg, sort);
                }, 100);
            }
        }
    });
    //    if(locationExtract('ways')<2)
    //    	$('.goodsStock').removeClass('goodsStock');
}


//价格块操作
function priceHandle() {
    $('.priceSelect button').eq(0).unbind();
    $('.upPrice').unbind();
    $('.downPrice').unbind();
    $('.priceSelect input').unbind();
    $('.priceSelect button').eq(0).click(function() {
        if ($('.priceSelect input').eq(0).val() == "")
            $('.priceSelect input').eq(0).val(0);
        if ($('.priceSelect input').eq(1).val() == "")
            $('.priceSelect input').eq(1).val(9999999);
        if (locationExtract('search')) {
            searchDisplay(3, sortShow());
        } else if (locationExtract('classify')) {
            classifyShow(3, sortShow());
        }
    });
    $('.priceSelect input').eq(0).keyup(function(event) {
        if (event.keyCode == '13')
            $('.priceSelect button').eq(0).trigger('click');
    });
    $('.priceSelect input').eq(1).keyup(function(event) {
        if (event.keyCode == '13')
            $('.priceSelect button').eq(0).trigger('click');
    });
    $('.upPrice').click(function() {
        if (locationExtract('search')) {
            searchDisplay(1, sortShow());
        } else if (locationExtract('classify')) {
            classifyShow(1, sortShow());
        }
    });
    $('.downPrice').click(function() {
        if (locationExtract('search')) {
            searchDisplay(2, sortShow());
        } else if (locationExtract('classify')) {
            classifyShow(2, sortShow());
        }
    });
}

//判断排列方式
function sortShow() {
    if (goodsSort)
        return goodsShowCol;
    else
        return goodsShowRow;
}

//改变排列方式
function sortChange() {
    $('.icon-arrest').click(function() {
        goodsSort = false;
        dataDislay();
        $(this).css("opacity", 0.6);
        $('.icon-view-tile').css('opacity', 1);
        return false;
    });
    $('.icon-view-tile').click(function() {
        goodsSort = true;
        dataDislay();
        $(this).css("opacity", 0.6);
        $('.icon-arrest').css('opacity', 1);
        return false;
    });
}

function dataShow(index, msg, sort) {
    if (index < msg.goodsImg.length) {
        $('.goodsShow').append(sort);
        $('.goodsShow>div').eq(index).addClass('blockDisplay');
        $('.goodsShow .goodsLink').eq(index).attr('href', 'concreteGoods.html?id=' + msg.goodsID[index]);
        $('.goodsShow .sellerName').eq(index).html(msg.userName[index]);
        $('.goodsShow .sellerImg').eq(index).attr('src', msg.userImg[index]);
        $('.goodsShow .goodsImg').eq(index).attr('src', msg.goodsImg[index][1]);
        if (msg.goodsPrice != undefined)
            $('.goodsShow .goodsPrice').eq(index).html('￥' + msg.goodsPrice[index]);
        $('.goodsShow .goodsIntroduce').eq(index).html(msg.goodsIntroduce[index]);
        $('.goodsShow .goodsName').eq(index).html(msg.goodsName[index]);
        $('.goodsShow .goodsSource').eq(index).html(msg.goodsSource[index]);
        if (msg.stock != undefined)
            $('.goodsShow .goodsStock').eq(index).html(msg.Stock[index]);
        setTimeout(function() {
            if (dataDisplaying)
                dataShow(index + 1, msg, sort);
        }, 80);
    }
}