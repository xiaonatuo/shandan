layui.extend({
    //你的webuploader.js路径
    webuploader: 'uploader/exts/webuploader'
}).define(['layer', 'laytpl', 'table', 'element', 'webuploader'], function (exports) {
    var $ = layui.$
        , webUploader = layui.webuploader
        , element = layui.element
        , layer = layui.layer
        , table = layui.table
        , rowData = []//保存上传文件属性集合,添加table用
        , fileSize = undefined // 100*1024*1024//默认上传文件大小
        , chunkSize = 1024 * 1024//默认文件片段
        , fileType = '' //'doc,docx,pdf,xls,xlsx,ppt,pptx,gif,jpg,jpeg,bmp,png,rar,zip'
        , upload
        , headers
        , nowtime = 0;
    var index = 0;
    var fileAllNum = 0;
    var fileAllSize = 0;
    var successNum = 0;
    var successSize = 0;
    var percentages = {}; // 所有文件的进度信息，key为file id
    var fList = [];
    var fileBoxEle = "#fileBoxEle";
    var fileMap = new Map();
    //加载样式
    layui.link(ctx + '/js/common/layui/extend/uploader/exts/webuploader.css');

    var Class = function (options) {
        var that = this;
        that.options = options;
        that.register();
        that.init();
        that.events();
    };
    Class.prototype.init = function () {
        var that = this,
            options = that.options;
        if (!that.strIsNull(options.size)) {
            fileSize = options.size
        }
        if (!that.strIsNull(options.chunkSize)) {
            chunkSize = options.chunkSize;
        }
        if (!that.strIsNull(that.options.fileType)) {
            fileType = that.options.fileType;
        }

        var fileBox = `
                    <div id="extend-upload-chooseFile" style="margin-left: 5px;margin-top: 5px;height: 32px;line-height:22px;">选择文件</div>
                    <table style="margin-top:-10px;" class="layui-table" id="extend-uploader-form" lay-filter="extend-uploader-form">
                      <thead>
                        <tr>
                          <th lay-data="{type:'numbers', fixed:'left'}"></th>
                          <th lay-data="{field:'fileName'}">文件名称</th>
                          <th lay-data="{field:'fileSize', width:100}">文件大小</th>
                          <th lay-data="{field:'progress', width:200,templet:'#button-form-optProcess'}">进度</th>
                          <th lay-data="{field:'oper', width: 100,templet: '#button-form-uploadTalbe'}">操作</th>
                        </tr>
                      </thead>
                    </table>
                    <script type="text/html" id="button-form-uploadTalbe">
                        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
                    </script>
                    <script type="text/html" id="button-form-optProcess">
                        <div style="margin-top: 5px;" class="layui-progress layui-progress-big" lay-filter="{{d.fileId}}"  lay-showPercent="true">
                            <div class="layui-progress-bar layui-bg-blue" lay-percent="0%"></div>
                        </div>
                    </script>`
        var fileBoxE = options.fileBoxEle || fileBoxEle;
        $(fileBoxE).html(fileBox);
        that.createUploader(options, that, fileType, fileSize, chunkSize);
        if (options.chooseFolder) {
            setTimeout(function () {
                $('#extend-upload-chooseFile input[type="file"]').attr('webkitdirectory', '');
            }, 100);
        }
    };

    Class.prototype.createUploader = function (options, that, fileType, fileSize, chunkSize) {
        table.init('extend-uploader-form', {
            height: 260,
            limit: 10000,//默认好像是一页10条，我们只需要一页展示完，所以写大一点
            unresize: true
        });

        upload = webUploader.create({
            // 不压缩image
            resize: false,
            // swf文件路径
            swf: 'uploader/Uploader.swf',
            // 默认文件接收服务端。
            server: options.url,
            pick: {
                id: '#extend-upload-chooseFile',//指定选择文件的按钮容器，不指定则不创建按钮。注意 这里虽然写的是 id, 不仅支持 id, 还支持 class, 或者 dom 节点。
                innerHTML: '选择文件夹',
                multiple: true //开启文件多选
            },
            //接收文件类型--自行添加options
            accept: [{
                title: 'file',
                extensions: fileType,
                mimeTypes: that.buildFileType(fileType)
            }],
            fileNumLimit: undefined,//验证文件总数量, 超出则不允许加入队列,默认值：undefined,如果不配置，则不限制数量
            fileSizeLimit: undefined, //1kb=1024*1024,验证文件总大小是否超出限制, 超出则不允许加入队列。
            fileSingleSizeLimit: undefined, //验证单个文件大小是否超出限制, 超出则不允许加入队列。
            chunked: true,//是否开启分片上传
            threads: 1,
            chunkSize: chunkSize,//如果要分片，每一片的文件大小
            prepareNextFile: false//在上传当前文件时，准备好下一个文件,请设置成false，不然开启文件多选你浏览器会卡死
        });
    };

    Class.prototype.formatFileSize = function (size) {
        var fileSize = 0;
        if (size / 1024 > 1024) {
            var len = size / 1024 / 1024;
            fileSize = len.toFixed(2) + "MB";
        } else if (size / 1024 / 1024 > 1024) {
            var len = size / 1024 / 1024;
            fileSize = len.toFixed(2) + "GB";
        } else {
            var len = size / 1024;
            fileSize = len.toFixed(2) + "KB";
        }
        return fileSize;
    };

    Class.prototype.buildFileType = function (type) {
        var ts = type.split(',');
        var ty = '';

        for (var i = 0; i < ts.length; i++) {
            ty = ty + "." + ts[i] + ",";
        }
        return ty.substring(0, ty.length - 1)
    };

    Class.prototype.strIsNull = function (str) {
        if (typeof str == "undefined" || str == null || str == "")
            return true;
        else
            return false;
    };


    Class.prototype.events = function () {
        var that = this, option = this.options;
        var {uploadBeforeSend, error, uploadSuccess, uploadFinished, uploadProgress} = option;
        /*let flag;
        let div = document.createElement('div');
        div.style = 'position:absolute; width:100px; height:100px; z-index:999; background:red;';*/
        //添加文件之前
        upload.on('beforeFileQueued', function(file){
            /*if(!flag){
                /!*console.info('sss', div);
                let body = document.getElementById('uploader-table')
                //  body.appendChild(div);
                flag = true;*!/
            }*/
        })
        // 当一批文件被添加之后
        upload.on('filesQueued', function () {

        });
        upload.on('fileQueued', function (file) {
            file.nameBak = file.name;
            file.name = file.source.source.webkitRelativePath;
            var fileSize = that.formatFileSize(file.size);
            var row = {
                fileId: file.id,
                fileName: file.name,
                fileSize: fileSize,
                validateMd5: '0%',
                progress: file.id,
                state: '就绪'
            };
            rowData.push(row);
            that.reloadData(rowData);
            element.render('progress');
        });

        //监听进度条,更新进度条信息
        upload.on('uploadProgress', function (file, percentage) {
            element.progress(file.id, (percentage * 100).toFixed(0) + '%');
            uploadProgress && uploadProgress(file, percentage);
        });


        /**上传之前**/
        upload.on('uploadBeforeSend', function (block, data, headers) {
            nowtime = new Date().getTime();
            data.contentType = block.file.type;
            data.chunks = block.file.chunks;
            data.fileMd5 = block.file.fileMd5;
            data.chunkMd5 = block.zoneMd5;
            data.chunkTotal = block.chunks;
            data.chunkIndex = block.chunk;
            data.fileSize = block.total;
            data.startOffset = block.start;
            data.endOffset = block.end;
            data.id = '';
            data.fileName = block.file.nameBak;
            data.fileFullName = block.file.name;
            if (option.getFormData) {
                let d = option.getFormData();
                if (d) {
                    data = Object.assign(data, option.getFormData())
                }
            }
            if (that.isEmpty(that.options.headers)) {
                headers = that.options.headers;
            }
            uploadBeforeSend && uploadBeforeSend(block, data, headers);
        });

        //错误信息监听
        upload.on('error', function (handler) {

            if (handler == 'F_EXCEED_SIZE') {
                layer.alert('上传的单个太大! <br>最大支持' + that.formatFileSize(fileSize) + '! <br>操作无法进行,如有需求请联系管理员', {icon: 5});
            } else if (handler == 'Q_TYPE_DENIED') {
                layer.alert('不允许上传此类文件!。<br>操作无法进行,如有需求请联系管理员', {icon: 5});
            }
            error && error(handler);
        });

        /**从文件队列移除**/
        upload.on('fileDequeued', function (file) {
            fileAllNum--;
            fileAllSize -= file.size;
            delete percentages[file.id];
        });

        //移除上传的文件
        table.on('tool(extend-uploader-form)', function (obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                rowData = that.removeArray(rowData, data.fileId);
                fList = that.removeArray(fList, data.fileId);
                upload.removeFile(data.fileId, true);

                obj.del();
            } else if (obj.event === 'stop') {//暂停、继续
                upload.stop(true);

            }
        });

        //当文件上传成功时触发。file {ArchivesFile} File对象, response {Object}服务端返回的数据
        upload.on('uploadSuccess', function (file, res) {
            if (res.flag) {
                that.setTableBtn(file.id, '正在校验文件...');
            }
            uploadSuccess && uploadSuccess(file, res);
        })
        //所有文件上传成功后
        upload.on('uploadFinished', function () {//成功后
            $("#extent-button-uploader").text("开始上传");
            $("#extent-button-uploader").removeClass('layui-btn-disabled');
            uploadFinished && uploadFinished();
        });
    };

    Class.prototype.reloadData = function (data) {
        layui.table.reload('extend-uploader-form', {
            data: data
        });
    };

    Class.prototype.register = function () {
        var that = this,
            options = that.options;

        headers = options.headers || {};
        var fileCheckUrl = options.fileCheckUrl;//检测文件是否存在url

        //监控文件上传的三个时间点(注意：该段代码必须放在WebUploader.create之前)
        //时间点1：:所有分块进行上传之前（1.可以计算文件的唯一标记;2.可以判断是否秒传）
        //时间点2： 如果分块上传，每个分块上传之前（1.询问后台该分块是否已经保存成功，用于断点续传）
        //时间点3：所有分块上传成功之后（1.通知后台进行分块文件的合并工作）
        webUploader.Uploader.register({
            "before-send-file": "beforeSendFile",
            "before-send": "beforeSend",
            "after-send-file": "afterSendFile"
        }, {
            //时间点1：:所有分块进行上传之前调用此函数
            beforeSendFile: function (file) {//利用md5File（）方法计算文件的唯一标记符
                //创建一个deffered
                var deferred = webUploader.Deferred();
                //1.计算文件的唯一标记，用于断点续传和秒传,获取文件前20m的md5值，越小越快，防止碰撞，把文件名文件大小和md5拼接作为文件唯一标识
                (new webUploader.Uploader()).md5File(file, 0, 10 * 1024 * 1024).progress(function (percentage) {
                }).then(function (val) {
                    file.fileMd5 = val;
                    let {fileMd5, name} = file;
                    let formData = options.getFormData() || {};
                    $.ajax({
                            type: "POST",
                            url: fileCheckUrl,
                            headers: headers,
                            data: {fileMd5, name},
                            dataType: "json",
                            success: function (response) {
                                if (response.flag) {
                                    let _file = response.data.file, name_exists = response.data.name_exists;
                                    fileMap.set(file.id, _file);
                                    if (_file && _file.isMerge) {
                                        upload.skipFile(file);
                                        that.setTableBtn(file.id, "上传成功");
                                        element.progress(file.id, '100%');
                                        successNum++;
                                        successSize += file.size;
                                        //如果存在，则跳过该文件，秒传成功
                                        fList.push(_file);
                                        // 未找到文件名相同的文件，则将文件信息复制一份并保存
                                        if (!name_exists) {
                                            saveFileAndDir(_file.id, formData.entityId, file.name);
                                        }
                                        deferred.reject();
                                    } else {
                                        deferred.resolve();
                                    }
                                } else {
                                    deferred.resolve();
                                }
                            }
                        }
                    );
                });
                //返回deffered
                return deferred.promise();
            },
            //时间点2：如果有分块上传，则 每个分块上传之前调用此函数
            //block:代表当前分块对象
            beforeSend: function (block) {
                let _block_file = block.file;
                let _file = fileMap.get(_block_file.id);

                let deferred = webUploader.Deferred();
                if (_file && block.chunk <= _file.currentChunkIndex) {
                    //分片索引小于已上传的分片索引则不上传
                    deferred.reject();
                } else {
                    block.zoneMd5 = _block_file.fileMd5 + "_" + block.chunk;
                    deferred.resolve();
                }
                return deferred.promise();
            },
            //时间点3：所有分块上传成功之后调用此函数
            afterSendFile: function (file, res, hds) {
                if (res.flag) {
                    let formData = options.getFormData() || {};
                    saveFileAndDir(res.data.id, formData.entityId, file.fullName).then(res => {
                        upload.skipFile(file);
                        that.setTableBtn(file.id, '上传成功');
                        element.progress(file.id, '100%');
                        successNum++;
                        successSize += file.size;
                        fList.push(fileMap.get(file.id));
                    });
                }
            }
        });
    };

    /***
     * 注意更改了table列的位置,或自行新增了表格,请自行在这修改
     */
    Class.prototype.getTableHead = function (field) {
        //获取table头的单元格class,保证动态设置table内容后单元格不变形
        var div = $("#extend-uploader-form").next().find('div[class="layui-table-header"]');
        var div2 = div[0];
        var table = $(div2).find('table');
        var td = table.find('th[data-field="' + field + '"]').find('div').attr('class');
        return td;
    };

    Class.prototype.setTableBtn = function (fileId, val) {
        var td = this.getTableHead('oper');
        //获取操作栏,修改其状态
        var table = $("#extend-uploader-form").next().find('div[class="layui-table-body layui-table-main"]').find('table');
        var pro = table.find('td[data-field="progress"]');
        for (var i = 0; i < pro.length; i++) {
            var d = $(pro[i]).attr('data-content');
            if (d == fileId) {
                var t = $(pro[i]).next();
                t.empty();
                t.append('<div class="' + td + '"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="ok">' + val + '</a></div>')
            }
        }
    };

    Class.prototype.uploadToServer = function () {
        if (rowData.length <= 0) {
            layer.msg('没有上传的文件', {icon: 5});
            return;
        }
        upload.upload();
    };

    Class.prototype.removeArray = function (array, fileId) {
        for (var i = 0; i < array.length; i++) {
            if (array[i].fileId == fileId) {
                array.splice(i, 1);
            }
        }
        return array;
    };

    Class.prototype.getData = function () {
        var files = [];
        for (var i = 0; i < fList.length; i++) {
            if (this.isEmpty(fList[i])) {
                files.push(fList[i]);
            }
        }
        var obj = {};
        files = files.reduce(function (item, next) {//去重，第二次上传在获取时，会重复
            obj[next.md5Value] ? '' : obj[next.md5Value] = true && item.push(next);
            return item;
        }, []);
        return files;
    }
    Class.prototype.isEmpty = function (value) {
        if (value != null && value != undefined && value != "") {
            return true;
        }
        if (parseInt(value) == 0) {//等于0不算空值系列,可能 ""==0也为true，所以转成数字来比较
            return true;
        }
        return false;
    }
    Class.prototype.clearData = function () {
        fList = [];
        rowData = [];
    }
    Class.prototype.setFileType = function (fileType) {
        var that = this;
        that.options.fileType = fileType;
        that.register();
        that.init();
        that.events();
    }

    function saveFileAndDir(fileId, currentDirId, fileFullName) {
        let data = {fileId, currentDirId, fileFullName}
        return new Promise(((resolve, reject) => {
            Util.post(`/sys/file/upload/chunk/complete`, data).then(resolve).catch(reject)
        }))
    }

    var layWebupload = {
        render: function (options) {
            var inst = new Class(options);
            return inst;
        },
        startUpload: function () {
            upload.upload();
        },
    };
    exports('uploader', layWebupload);
});