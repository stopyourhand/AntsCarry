$(function() {
    const otherGoods = $('.others').html();
    // 图片数据渲染
    $.ajax({
        type: "post",
        dataType: "json",
        url: "SingleServlet?method=data",
        data: { "id": locationExtract('id') },
        success: function(msg) {
            $('.others>*').remove();
            $('.sellerInformation img').attr('src', msg.userImg);
            $('.sellerInformation>span').html(msg.userName);
            $('.rightInfo .goodsName').html(msg.goodsName);
            $('.goodsNum').html('(' + msg.total + ')');
            if (msg.wcHide == 'false')
                $('.sellerWechat span').html(msg.userWechat);
            if (msg.qqHide == 'false')
                $('.sellerQQ span').html(msg.userQQ);
            $('.sellerLocation span').html(msg.userAddress);
            $('.imgContainer>img').attr('src', msg.goodsImg[0][1]);
            $('.moveImg>img').attr('src', msg.goodsImg[0][1]);
            $('.imgLarger>img').attr('src', msg.goodsImg[0][1]);
            $('.rightInfo .goodsPrice').html(msg.goodsPrice);
            $('.goodsIntroduce').html(msg.goodsIntroduce);
            for (let index = 1; index < msg.goodsImg[0].length&&index<5; index++) {
                $('.imgShow').append('<img src=' + msg.goodsImg[0][index] + '>');
            }
            bigImgChange(0,length,msg)
            for (let index = 0; index < $('.imgShow img').length&&index<5; index++) {
                $('.imgShow img').eq(index).click(function() {
                    $('.imgContainer>img').attr('src', msg.goodsImg[0][index + 1]);
                    $('.moveImg>img').attr('src', msg.goodsImg[0][index + 1]);
                    $('.imgLarger>img').attr('src', msg.goodsImg[0][index + 1]);
                  //点击左右切换图片
                    bigImgChange(index,$('.imgShow img').length,msg);
                });
            }
            addShop();
            if (msg.judge == 'false') {
                $('.addShop').attr('disabled', true);
                $('.addShop').html('已加入购物车');
                $('.addShop').unbind();
            }
            if (msg.goodsBargin == '1')
                $('.isBargin').html('该商品可以讲价');
            if (msg.login == 'hidden') {
                $('.sellerLocation span').html('请登录获取卖家地址');
                $('.sellerWechat span').html('请登录获取卖家微信');
                $('.sellerQQ span').html('请登录获取卖家QQ');
            }
            for (let index = 0; index < msg.otherGoodsImg.length; index++) {
                $('.others').append(otherGoods);
                $('.others a').eq(index).attr('href', 'concreteGoods.html?id=' + msg.otherGoodsID[index]);
                $('.otherShow img').eq(index).attr('src', msg.otherGoodsImg[index][1]);
                $('.otherShow .otherName').eq(index).html(msg.otherGoodsName[index]);
                $('.otherShow .goodsPrice').eq(index).html(msg.otherGoodsPrice[index]);
            }
            if(msg.goodsImg[0][0]!=''){
            	$('video').attr('src',msg.goodsImg[0][0]);
            }
            else{
            	$('.videoShow').remove();
            	$('.icon-play-circle').remove();
            }
        }
    });
});


// 点击放大图片和恢复
$('.moveBlock').click(function() {
    $('.imgBg').show();
});
$('.imgBg').click(function() {
    $('.imgBg').hide();
    $('.moveImg').hide();
});

function bigImgChange(index,length,msg){
	$('.imgLarger span').eq(0).unbind().click(function(){
    	if(index>0){
    		$('.imgContainer>img').attr('src', msg.goodsImg[0][index]);
            $('.moveImg>img').attr('src', msg.goodsImg[0][index]);
            $('.imgLarger>img').attr('src', msg.goodsImg[0][index]);
        	index--;
    	}
    	bigImgChange(index,length,msg);
    	event.stopPropagation(); 
    });
	$('.imgLarger span').eq(1).unbind().click(function(){
    	if(index<length){
    		$('.imgContainer>img').attr('src', msg.goodsImg[0][index+1]);
            $('.moveImg>img').attr('src', msg.goodsImg[0][index+2]);
            $('.imgLarger>img').attr('src', msg.goodsImg[0][index+2]);
        	index++;
    	}
    	bigImgChange(index,length,msg);
    	event.stopPropagation(); 
    });
}


// 滑动图片放大
$('.imgContainer').mousemove(function(e) {
    if ($('.videoShow').css('display') == "none"||$('.videoShow').length==0) {
        let x = e.clientX - parseInt($('.imgContainer img').offset().left);
        let y = e.clientY - parseInt($('.imgContainer img').offset().top) + $(window).scrollTop();
        let sideLen = parseInt($('.moveBlock').css('width'));
        if (y > 0 && x > 0 && x < parseInt($('.imgContainer img').css('width')) && y < parseInt($('.imgContainer img').css('height'))) {
            $('.moveBlock').show();
            $('.moveImg').show();
            let top = $('.imgContainer img').offset().top - $('.imgContainer').offset().top;
            let left = $('.imgContainer img').offset().left - $('.imgContainer').offset().left;
            if (x < sideLen / 2 && x > 0)
                x = sideLen / 2;
            else if (x > parseInt($('.imgContainer img').css('width')) - sideLen / 2)
                x = parseInt($('.imgContainer img').css('width')) - sideLen / 2;
            if (y < sideLen / 2 && y > 0)
                y = sideLen / 2;
            else if (y > parseInt($('.imgContainer img').css('height')) - sideLen / 2)
                y = parseInt($('.imgContainer img').css('height')) - sideLen / 2;
            $('.moveBlock').css('left', x + left - sideLen / 2).css('top', y + top - sideLen / 2);
            $('.moveImg img').css('top', -(y - sideLen / 2) * 440 / sideLen).css('left', -(x - sideLen / 2) * 440 / sideLen)
                .css("width", (440 * parseInt($('.imgContainer img').css('width')) / sideLen) + "px").css("height", (440 * parseInt($('.imgContainer img').css('height')) / sideLen))
        } else {
            $('.moveBlock').hide();
            $('.moveImg').hide();
        }
    }
    //点击开始停止播放
    $('.videoShow>.control').unbind().click(function() {
        $(this).toggleClass('icon-play-circle').toggleClass('icon-pause-circle');
        if ($(this).attr('class') == "control icon-play-circle") {
            $('.videoShow video')[0].pause();
        } else {
            $('.videoShow video')[0].play();
        }
    });
    //点击隐藏视频
    $('.videoShow>.icon-cancel').unbind().click(function() {
        $('.videoShow').hide();
        $('.videoShow>.control').attr('class', 'control icon-play-circle');
        $('.videoShow video')[0].pause();
        $('.imgContainer>.icon-play-circle').show();
    });
    // 点击显示视频
    $('.imgContainer>.icon-play-circle').unbind().click(function() {
        $('.videoShow').show();
        $(this).hide();
    });
});



// 联系卖家
$('.linkSeller').eq(0).click(function() {
    $.ajax({
        type: "post",
        dataType: 'json',
        url: 'ChatServlet?method=come',
        data: { 'id': locationExtract('id') },
        success: function(msg) {
            if (msg.judge == 'false') {
                alert('该物品为您本人发布');
            } else if (msg.judge == 'notlogin') {
                alert('您尚未登录，请先登录');
                $('.showContaniner>span').eq(0).trigger('click');
            } else {
            	window.open("newChat.html?id=" + locationExtract("id"));
            }
        }
    })
});

// 点击加入购物车
function addShop() {
    $('.addShop').click(function() {
        if (confirm('是否确认将该物品加入购物车')) {
            $.ajax({
                type: "post",
                dataType: "json",
                url: "ShopServlet?method=add",
                data: { "id": locationExtract('id') },
                success: function(msg) {
                    if (msg.judge == 'success') {
                        alert('加入购物车成功');
                        $('.addShop').attr('disabled', true);
                        $('.addShop').html('已加入购物车');
                        $('.addShop').unbind();
                    } else if (msg.judge == 'notlogin') {
                        alert('您尚未登录，请先登录');
                        $('.showContaniner>span').eq(0).trigger('click');
                    } else if (msg.judge == 'false') {
                        alert('该商品为您本人发布');
                    }
                }
            })
        }
    });
}