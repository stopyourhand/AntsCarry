$(function() {
    $('button').click(function() {
        if ($('textarea').val() == "") {
            $('textarea').attr('placeholder', '请输入您的反馈').css('color', 'rgb(255,0,0)').keyup(function() {
                $(this).css('color', 'rgb(255,0,0)');
            });
        } else if (radioChoose() < 0) {
            $('.error').show();
        } else {
            let data = {
                "text": $('textarea').val(),
                "satisfy": $(".question:eq(1) label").eq(radioChoose()).html(),
                "mobile": $('.question:eq(2) input').eq(0).val(),
                "username": $('.question:eq(2) input').eq(1).val(),
            }
            $.ajax({
                type: "post",
                dataType: "json",
                url: "FeedBackServlet",
                data: data,
                success: function(msg) {
                    if (msg.judge == "success") {
                        alert('反馈成功，非常感谢您的意见');
                        location.replace(location.href);
                    }
                }
            });
        }
        return false;
    });
});

function radioChoose() {
    let flag = -1;
    for (let index = 0; index < $(".question:eq(1) input").length; index++) {
        if ($(".question:eq(1) input ").eq(index).is(':checked')) {
            flag = index;
        }
    }
    return flag;
}