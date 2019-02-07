$(function() {
    $.ajax({
        type: "post",
        dataType: "json",
        url: "Announcement?method=more",
        success: function(msg) {
            const li = "<li><a>main</a> <span>time</span></li>";
            for (let index = 0; index < msg.title.length; index++) {
                $('.announce .tMain>ul').append(li);
                $('.announce .tMain>ul a').html(msg.title[index]);
                $('.announce .tMain>ul a').attr('href', "singleAnnounce.html?id=" + msg.id[index]);
                $('.announce .tMain>ul span').html(msg.time[index]);
            }
        }
    })
});