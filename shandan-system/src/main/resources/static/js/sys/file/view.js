/**
 * <p>
 *  文件预览
 * </p>
 *
 * @author Administrator
 * @since 2021/6/10
 */
layui.use(['layer', 'laytpl', 'dropdown', 'carousel', 'form'], function () {
    const carousel = layui.carousel;

    let imgInit = false;
    let player;

    const file_uri = `${ctx}/upload/${file.path}`;

    file.fileName += file.fileSuffix;
    for(let col in file){
        let val = file[col] || '';
        $(`label[name="${col}"]`).text(val);
    }
    const fileSuffix = file.fileSuffix.toLowerCase();

    if (viewType.image.includes(fileSuffix)) {
        if (!imgInit) { // 没有初始化则先初始化
            initImageViewer();
        }
        showFileViewer('image');
    } else if (viewType.video.includes(fileSuffix) || viewType.audio.includes(fileSuffix)) {
        if (!player) {
            initVideoPlayer();
        }
        player.src(file_uri);
        showFileViewer('video');
    } else if (viewType.pdf.includes(fileSuffix)) {
        $('#pdfViewer').attr('src', file_uri);
        showFileViewer('pdf');
    } else if (viewType.text.includes(fileSuffix)) {
        $('#txtViewer').attr('src', file_uri);
        showFileViewer('text');
    } else {
        let htm = `<p>该文件不支持预览，可下载后查看。</p>`;
        $('#file-viewer-other').html(htm)
        showFileViewer('other');
    }

    /**
     * 视频播放器初始化
     */
    function initVideoPlayer() {
        player = videojs('videoPlayer');
        player.ready(function () {
            let player = this;
            player.play();
        });
    }

    /**
     * 初始化图片浏览组件
     */
    function initImageViewer(file) {
        const carouselDiv = $('#image-carousel div[carousel-item]');
        carouselDiv.html('');
        carouselDiv.append(`<img width="auto" height="auto" src="${file_uri}" />`);
        carousel.render({
            elem: '#image-carousel',
            width: '100%', //设置容器宽度
            height: '100%',
            arrow: 'none', //始终显示箭头
            autoplay: false,
            indicator: 'none'
        });
        //触发轮播切换事件
        carousel.on('change(image-carousel)', function ({index}) { //test1来源于对应HTML容器的 lay-filter="test1" 属性值
            $('#viewFileListMenu li').removeClass('layui-menu-item-checked');
            $(`#viewFileListMenu li:eq(${index})`).addClass('layui-menu-item-checked');
        });
        imgInit = true;
    }
});