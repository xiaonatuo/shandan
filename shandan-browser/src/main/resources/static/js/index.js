/**
 * <p>
 *  index.html
 * </p>
 *
 * @author Administrator
 * @since 2021/6/17
 */
layui.use(['layer', 'globalTree', 'dropdown'], function () {
    const tree = layui.globalTree,
        layer = layui.layer,
        dropdown = layui.dropdown;

    // initConditionBlock()
    initDirTree();

    /**
     * 初始化目录树
     */
    function initDirTree(){
        tree.init({
            id: 'directory-tree',
            url: `${ctx}/browser/dir/tree`,
            data: [{id: '-', parentId: '', title: '根目录', leaf: false, last: false, spread: false}],
            cache: false,
            done: function (nodes, elem) {
                console.info(nodes);
                // 模拟鼠标点击事件展开第一层目录
                $('i.dtree-icon-jia[data-id="-"]').click();
            },
        });
    }

    function initConditionBlock(){
        dropdown.render({
            elem: '#condition-btn',
            align: 'right',
            content: ['<div class="layui-tab layui-tab-brief">'
                ,'<ul class="layui-tab-title">'
                ,'<li class="layui-this">Tab header 1</li>'
                ,'<li>Tab header 2</li>'
                ,'<li>Tab header 3</li>'
                ,'</ul>'
                ,'<div class="layui-tab-content">'
                ,'<div class="layui-tab-item layui-text layui-show"><p style="padding-bottom: 10px;">在 content 参数中插入任意的 html 内容，可替代默认的下拉菜单。 从而实现更多有趣的弹出内容。</p><p> 是否发现，dropdown 组件不仅仅只是一个下拉菜单或者右键菜单，它能被赋予许多的想象可能。</p></div>'
                ,'<div class="layui-tab-item">Tab body 2</div>'
                ,'<div class="layui-tab-item">Tab body 3</div>'
                ,'</div>'
                ,'</div>'].join('')
            ,style: 'width: 1542px; height: 200px; padding: 0 15px; box-shadow: 1px 1px 30px rgb(0 0 0 / 12%);'
            ,ready: function(){
                layui.use('element', function(element){
                    element.render('tab');
                });
            }
        });
    }
});