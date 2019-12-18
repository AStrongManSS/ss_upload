<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>upload</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
</head>
<body>
<input type="file" name="file" id="file">
<button id="upload" onClick="upload()">upload</button>
<script type="text/javascript">
    var bytesPerPiece = 1024 * 1024 * 100 ; // 每个文件切片大小定为100MB .
    var totalPieces;
    //发送请求
    function upload() {
        var blob = document.getElementById("file").files[0];
        var start = 0;
        var end;
        var index = 0;
        var filesize = blob.size;
        var filename = blob.name;

        var uuid0 = uuid();

        //计算文件切片总数
        totalPieces = Math.ceil(filesize / bytesPerPiece);
        while(start < filesize) {
            end = start + bytesPerPiece;
            if(end > filesize) {
                end = filesize;
            }

            var chunk = blob.slice(start,end);//切割文件
            var sliceIndex= blob.name + index;
            var formData = new FormData();
            formData.append("file", chunk, filename);
            console.log(start);
            formData.append("start", start);
            formData.append("end", end);
            formData.append("uuid", uuid0);
            formData.append("chunk", index);
            formData.append("chunks", totalPieces);
            formData.append("filesize", filesize);
            $.ajax({
                url: './uploadFile',
                type: 'POST',
                cache: false,
                data: formData,
                processData: false,
                contentType: false,
            }).done(function(res){

            }).fail(function(res) {

            });
            start = end;
            index++;
        }
    }

    function uuid() {
        var s = [];
        var hexDigits = "0123456789abcdef";
        for (var i = 0; i < 36; i++) {
            s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
        s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
        s[8] = s[13] = s[18] = s[23] = "-";

        var uuid = s.join("");
        return uuid;
    }
</script>
</body>
</html>