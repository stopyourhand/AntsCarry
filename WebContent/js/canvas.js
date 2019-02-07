$(function() {
    var canvas = $('canvas')[0];
    var context = canvas.getContext('2d');
    canvas.width = 900;
    canvas.height = 600;
    var colors = ["rgb(243,80,60)", "rgb(89,85,159)", "rgb(114,192,150)", "rgb(34,195,249)", "rgb(177,113,191)", "rgb(254,201,63)", "rgb(157,150,150)"];
    var nums = ["0", "10", "20", "30", "40", "50", "60", "70", "80", "90"];
    $.ajax({
        type: "post",
        dataType: "json",
        url: "ProductServlet?method=getsold",
        success: function(msg) {
            context.sectorDraw(300, 200, 150, msg.count, msg.name, colors);
            $('.sector').click(() => {
                context.sectorDraw(300, 200, 150, msg.count, msg.name, colors);
                context.titleDraw("蚂蚁置物用户交易数分析扇形图");
            });
            $('.strip').click(() => {
                context.stripDraw(400, msg.name, nums, msg.count, colors);
                context.titleDraw("蚂蚁置物用户交易数分析条形图");
            });
        }
    })
});


function preSum(index, arr) {
    let sum = 0;
    for (let i = 0; i < index + 1; i++)
        sum += arr[i];
    return sum;
}

function degTurn(num, sum) {
    return Math.ceil(num * 2 / sum * 10) / 10;
}

CanvasRenderingContext2D.prototype.titleDraw = function(text) {
    this.beginPath();
    this.fillStyle = 'rgb(0,0,0)';
    this.textAlign = 'center';
    this.textBaseline = 'middle';
    this.font = '30px Adobe Ming Std';
    this.fillText(text, 300, 50);
    this.closePath();
}

CanvasRenderingContext2D.prototype.sectorDraw = function(x, y, l, arr, text, colors) {
    this.clearRect(0, 0, 900, 900);
    let sum = 0;
    for (let index = 0; index < arr.length; index++) {
        sum += arr[index];
    }
    for (let index = 0; index < arr.length; index++) {
        let next = {
            x: parseInt(x - l * Math.cos(degTurn(preSum(index, arr), sum) * Math.PI)),
            y: parseInt(y - l * Math.sin(degTurn(preSum(index, arr), sum) * Math.PI))
        }
        let deg;
        this.strokeStyle = "rgb(0,0,0)";
        this.fillStyle = colors[index];
        this.beginPath();
        this.moveTo(x, y);
        if (index == 0) {
            this.lineTo(x - l, y);
            this.arc(x, y, l, Math.PI, (1 + degTurn(preSum(index, arr), sum)) * Math.PI, false);
        } else {
            let pre = {
                x: x - l * Math.cos(degTurn(preSum(index - 1, arr), sum) * Math.PI),
                y: y - l * Math.sin(degTurn(preSum(index - 1, arr), sum) * Math.PI)
            }
            this.lineTo(pre.x, pre.y);
            this.arc(x, y, l, (1 + degTurn(preSum(index - 1, arr), sum)) * Math.PI, (1 + degTurn(preSum(index, arr), sum)) * Math.PI, false);
        }
        this.lineTo(x, y);
        this.closePath();
        this.stroke();
        this.fill();

    }
    //绘制文字
    for (let i = 0; i < arr.length; i++) {
        this.fillStyle = "rgb(0,0,0)";
        let deg;
        if (i == 0)
            deg = degTurn(preSum(i, arr), sum) * Math.PI / 2;
        else
            deg = degTurn((preSum(i, arr) + preSum(i - 1, arr)) / 2, sum) * Math.PI;
        this.beginPath();
        this.textAlign = 'center';
        this.textBaseline = 'middle';
        this.font = '20px Adobe Ming Std';
        this.fillText(arr[i] / sum * 100 + "%", x - l / 2 * Math.cos(deg), y - l / 2 * Math.sin(deg));
        this.closePath();
        this.fill();
    }
    this.tipDraw(text, colors);
}

//颜色标识
CanvasRenderingContext2D.prototype.tipDraw = function(text, colors) {
    let per = 30;
    for (let index = 0; index < text.length; index++) {
        this.beginPath();
        this.textAlign = 'left';
        this.textBaseline = 'middle';
        this.font = '14px Adobe Ming Std';
        this.fillStyle = "rgb(0,0,0)";
        this.fillText(text[index], 600, 100 + per * index);
        this.closePath();
        this.rectDraw(560, 90 + per * index, 590, 110 + per * index, colors[index]);
    }

}


// 条形图
CanvasRenderingContext2D.prototype.stripDraw = function(l, numX, numY, count, colors) {
    let perx = l / numX.length;
    let pery = l / numY.length;
    let margin = 20;
    this.clearRect(0, 0, 900, 900);
    this.axisDraw(l, numX, numY);
    let halfWidth = 10;
    for (let index = 0; index < numX.length; index++) {
        this.rectDraw(margin + perx * (index + 1 / 2) - halfWidth, l + margin, margin + perx * (index + 1 / 2) + halfWidth, l + margin - count[index] * pery / 10, colors[index]);
    }
    this.tipDraw(numX, colors);
}

CanvasRenderingContext2D.prototype.axisDraw = function(l, numX, numY, color = 'rgb(0,0,0)') {
    let perx = l / numX.length;
    let pery = l / numY.length;
    let margin = 20;
    // x坐标轴
    this.beginPath();
    this.strokeStyle = color;
    this.moveTo(margin, l + margin);
    this.lineTo(margin, margin);
    this.stroke();
    this.closePath();
    // y坐标轴
    this.beginPath();
    this.moveTo(margin, l + margin);
    this.lineTo(margin + l, margin + l);
    this.stroke();
    this.closePath();
    //坐标数字
    this.beginPath();
    this.textAlign = 'center';
    this.textBaseline = 'middle';
    this.font = '10px Adobe Ming Std';
    this.fillStyle = color;
    // this.fillText("0", margin / 2, l + margin / 2 * 3);
    for (let index = 0; index < numX.length; index++) {
        this.fillText(numX[index], margin + perx * (index + 1 / 2), l + margin / 2 * 3);
    }
    for (let index = 0; index < numY.length; index++) {
        this.fillText(numY[index], margin / 2, l + margin - pery * (index + 1));
    }
}

CanvasRenderingContext2D.prototype.rectDraw = function(x1, y1, x2, y2, color) {
    this.beginPath();
    this.moveTo(x1, y1);
    this.lineTo(x1, y2);
    this.lineTo(x2, y2);
    this.lineTo(x2, y1);
    this.strokeStyle = 'rgb(0,0,0)';
    this.fillStyle = color;
    this.stroke();
    this.fill();
    this.closePath();
}