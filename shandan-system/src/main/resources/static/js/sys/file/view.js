/**
 * <p>
 *  文件预览
 * </p>
 *
 * @author Administrator
 * @since 2021/6/10
 */
layui.use(['layer', 'laytpl', 'dropdown', 'carousel'], function () {
    const layer = layui.layer,
        laytpl = layui.laytpl,
        dropdown = layui.dropdown,
        carousel = layui.carousel;

    const entityId = layui.url().search.entityId || '';
    if(!entityId){
        console.error('预览文件出错，entityId为空')
    }

    let viewFiles = [];
    let noViewFiles = [];
    let images = [];
    // 查询文件列表
    getFileList().then(() => {
        // 渲染文件列表
        renderFileList();

    });

    /**
     * 渲染文件列表
     */
    function renderFileList() {
        const templateHtml = $('#fileListTemplate').html();
        // 渲染文件列表模板
        laytpl(templateHtml).render({
            fileListID: 'viewFileListMenu',
            list: viewFiles
        }, html => $('#viewFileList').html(html));
        // 不可预览文件列表
        laytpl(templateHtml).render({
            fileListID: 'noViewFileListMenu',
            list: noViewFiles
        }, html => $('#noViewFileList').html(html));

        // 列表点击事件绑定
        dropdown.on('click(viewFileListMenu)', fileListItemClick);

        // 渲染完成后自动点击第一个
        $('#viewFileListMenu li:first').click();
    }

    let imgInit = false;
    let player;
    /**
     * 文件列表点击事件回调函数
     * @param file 文件信息
     */
    function fileListItemClick(file) {
        const fileSuffix = file.fileSuffix.toLowerCase();

        if (viewType.image.includes(fileSuffix)) {
            if(!imgInit){ // 没有初始化则先初始化
                initImageViewer();
            }else{
                // 切换显示到指定图片
                let index = images.findIndex((img) => img.id === file.id)
                $(`#image-carousel div.layui-carousel-ind ul li:eq(${index})`).click();
            }
            showFileViewer('image');
        } else if (viewType.video.includes(fileSuffix) || viewType.audio.includes(fileSuffix)) {
            if(!player){
                initVideoPlayer();
            }
            player.src(`${ctx}/upload/${file.path}`);
            showFileViewer('video');
        } else if (viewType.pdf.includes(fileSuffix)) {
            $('#pdfViewer').attr('src', `${ctx}/upload/${file.path}`);
            showFileViewer('pdf');
        } else if (viewType.text.includes(fileSuffix)) {
            $('#txtViewer').attr('src', `${ctx}/upload/${file.path}`);
            showFileViewer('text');
        }
    }

    /**
     * 视频播放器初始化
     */
    function initVideoPlayer(){
        player = videojs('videoPlayer');
        player.ready(function(){
            let player = this;
            player.play();
        });
    }

    /**
     * 初始化图片浏览组件
     */
    function initImageViewer(){
        const carouselDiv = $('#image-carousel div[carousel-item]');
        carouselDiv.html('');
        images = viewFiles.filter(file => viewType.image.includes(file.fileSuffix));
        images.forEach(file => {
            carouselDiv.append(`<img width="auto" height="auto" src="${ctx}/upload/${file.path}" />`);
        })
        carousel.render({
            elem: '#image-carousel',
            width: '100%', //设置容器宽度
            height: '100%',
            arrow: 'hover', //始终显示箭头
            autoplay: false
        });
        //触发轮播切换事件
        carousel.on('change(image-carousel)', function ({index}) { //test1来源于对应HTML容器的 lay-filter="test1" 属性值
            $('#viewFileListMenu li').removeClass('layui-menu-item-checked');
            $(`#viewFileListMenu li:eq(${index})`).addClass('layui-menu-item-checked');
        });
        imgInit = true;
    }

    /**
     * 查询文件列表
     * @returns {Promise<void>}
     */
    async function getFileList() {
        const loadIndex = layer.load();
        await $.ajax({
            url: `${ctx}/sys/file/list`,
            data: {entityId},
            type: 'post',
            dataType: 'json',
            async: false,
            success: function (res) {
                layer.close(loadIndex);
                if (res.flag) {
                    // 将可预览的文件和不可预览的文件进行拆分
                    noViewFiles = res.data;
                    for (let type in viewType) {
                        res.data.forEach((file, index) => {
                            if (viewType[type].includes(file.fileSuffix)) {
                                viewFiles.push(file);
                                delete noViewFiles[index]
                            }
                        })
                    }
                    // 将数组中空值过滤掉， 因为上面的遍历删除操作会使数组中产生空值
                    noViewFiles = noViewFiles.filter(a => a);
                } else {
                    layer.alert(res.msg);
                }
            },
            error: function (res) {
                layer.close(loadIndex);
                layer.alert('服务器异常！');
            }
        })
    }
});