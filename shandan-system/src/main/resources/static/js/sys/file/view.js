/**
 * <p>
 *  文件预览
 * </p>
 *
 * @author Administrator
 * @since 2021/6/10
 */
layui.use(['layer', 'laytpl', 'dropdown', 'carousel', 'form'], function () {

    const file_uri = `${ctx}/upload/${file.path}`;

    file.fileName += file.fileSuffix;
    for (let col in file) {
        let val = file[col] || '';
        $(`label[name="${col}"]`).text(val);
    }

    let if_src = 'http://127.0.0.1:8012/onlinePreview?url=' + encodeURIComponent(Base64.encode(window.location.origin + file_uri))
    let if_elem = document.getElementById('office-online');
    showLoading();
    if_elem.onload = function () {
        closeLoading();
    };
    if_elem.src = if_src;
});