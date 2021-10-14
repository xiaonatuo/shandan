/**
 *  文件查看
 *
 * @author GuoXin
 * @since 2021/7/8
 */
function fileViewer(file){
    const layerContainer = `
                    <div class="file-viewer" id="fileViewer" style="height: ${window.innerHeight-51}px">
                        <!-- 图片轮播组件 -->
                        <div id="file-viewer-image" class="file-viewer layui-hide" style="height: 100%;text-align: center;">
                        </div>
                        <!-- video-js视频播放器 -->
                        <div id="file-viewer-video" class="file-viewer layui-hide" style="height: 100%">
                            <video id="videoPlayer" class="video-js" controls preload="auto" style="width: 100%; height: 100%">
                                <p class="vjs-no-js">
                                    要观看此视频，请启用 JavaScript，并考虑升级到
                                    <a href="https:videojs.comhtml5-video-support" target="_blank">支持 HTML5 视频</a>
                                    的网络浏览器
                                </p>
                            </video>
                        </div>
                        <!-- pdf浏览组件 -->
                        <div id="file-viewer-pdf" class="file-viewer layui-hide" style="height: 100%">
                            <iframe id="pdfViewer" style="height: 100%; width:100%" frameborder="0"></iframe>
                        </div>
                        <!-- 文本文件浏览组件 -->
                        <div id="file-viewer-text" class="file-viewer layui-hide" style="height: 100%; display: flex; justify-content: center; align-items: center">
                            <textarea readonly style="width: 98%; height: 98%; border:0"></textarea>
                        </div>
                        <!-- 不至于预览的文件 -->
                        <div id="file-viewer-other" class="file-viewer layui-hide" style="height: 100%;text-align: center;">
                            <p>该文件不支持预览，可下载后查看。</p>
                        </div>
                    </div>`
    let player;
    layui.use(['layer','laytpl'], function(){
        const layer = layui.layer;
        console.info('文件预览');
        layer.tab({
            area: [window.innerWidth +'px', window.innerHeight+'px'],
            tab: [{
                title: '文件预览',
                content: layerContainer
            }, {
                title: '文件属性',
                content: '<div id="fileProperties" style="padding:20px;">TAB2该说些啥</div>'
            }],
            success: function (layerObj, index) {
                //layer.full(index);
                viewFile(file);
                layui.laytpl($("#filePropertiesTemplate").html()).render(file, function (html) {
                    $("#fileProperties").html(html);
                })
            }
        });
        /**
         * 预览文件
         * @param file
         */
        function viewFile(file) {
            const fileSuffix = file.fileSuffix.toLowerCase();
            const filePath = `${bianmuServer}/upload/${file.path}`;
            if (viewType.image.includes(fileSuffix)) {
                let htm = `<img id="image-viewer" src="${filePath}" style="max-height: 100%; max-width: 100%"/>`
                $('#file-viewer-image').html(htm)
                showFileViewer('image');
            } else if (viewType.video.includes(fileSuffix) || viewType.audio.includes(fileSuffix)) {
                if (!player) {
                    initVideoPlayer();
                }
                player.src(filePath);
                showFileViewer('video');
            } else if (viewType.pdf.includes(fileSuffix)) {
                $('#pdfViewer').attr('src', filePath);
                showFileViewer('pdf');
            } else if (viewType.text.includes(fileSuffix)) {
                $.get(filePath, {}, function (res){
                    $('#file-viewer-text textarea').html(res);
                });
                showFileViewer('text');
            } else {
                let htm = `<p>该文件不支持预览，可下载后查看。</p>
                        <a href="javascript:void(0)"  id="download-file" data-id="${file.id}" style="color: blue; text-decoration: underline;">下载文件</a>`;
                $('#file-viewer-other').html(htm)
                $('#download-file').off('click')
                $('#download-file').on('click', function ({target}) {
                    let fileId = $(target).data('id')
                    window.open(`${bianmuServer}/sys/file/download/${fileId}`)
                })
                showFileViewer('other');
            }
        }

        /**
         * 切换显示文件展示区域
         * @param type 展示类型
         */
        function showFileViewer(type){
            $('#fileViewer .file-viewer').addClass('layui-hide');
            $(`#file-viewer-${type}`).removeClass('layui-hide');
        }

        /**
         * 视频播放器初始化
         */
        function initVideoPlayer() {
            player = videojs('videoPlayer');
            player.ready(function () {
                //this.play();
            });
        }
    })
}
