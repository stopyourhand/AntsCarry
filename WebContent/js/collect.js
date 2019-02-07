$(function() {
    let content = $('.mainContents').html();
    // 数据渲染
    $.ajax({
        type: 'post',
        dataType: 'json',
        url: 'ShopServlet?method=data',
        success: function(msg) {
            console.log(msg);
            $('.mainContent').remove();
            for (let index = 0; index < msg.goodsImg.length; index++) {
                $('.mainContents').append(content);
                $('.mainContent .goodsName').eq(index).html(msg.goodsName[index]);
                $('.mainContent .goodsName').eq(index).attr('href', 'concreteGoods.html?id=' + msg.goodsID);
                $('.mainContent').eq(index).children('img').attr('src', msg.goodsImg[index][1]);
                $('.mainContent .goodsIntroduce').eq(index).html(msg.goodsIntroduce[index]);
                $('.mainContent .address').eq(index).html(msg.goodsAddress[index]);
                $('.mainContent .goodsPrice').eq(index).html('￥' + msg.goodsPrice[index]);
            }
            removeContent(msg);
        }
    });
});

// 删除购物车里的内容
function removeContent(msg) {
    $('.mainContent .icon-cancel').each(function(index, element) {
        $(this).click(function() {
            if (confirm('是否从购物车中移除该闲置')) {
                let data = { 'id': msg.goodsID[index] };
                $.ajax({
                    type: 'post',
                    dataType: 'json',
                    url: 'ShopServlet?method=delete',
                    data: data,
                    success: function(msg) {
                        alert('删除成功');
                        $('.mainContent').eq(index).remove();
                        $('.mainContent .icon-cancel').unbind();
                        removeContent(msg);
                    }
                });
            }
        });
    });
}