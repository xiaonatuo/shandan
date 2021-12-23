const htmlTemplate = `
    <div class="layui-tab layui-tab-brief layui-hide" id="metadataCardBody" lay-filter="metadataCardBody">
        <ul class="layui-tab-title">
            <li class="layui-this">基础信息</li>
            <li class="db-source">字段信息</li>
            <li class="db-source">示例数据</li>
        </ul>
        <div class="layui-tab-content">
            <!-- 基础信息 -->
            <div class="layui-tab-item layui-show">
                <div class="layui-form" lay-filter="details-form">
                    <div class="layui-form-item">
                        <label class="layui-form-label">数据名称</label>
                        <div class="layui-input-block">
                            <input type="text" readonly class="layui-input" name="metadataName">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">注释</label>
                        <div class="layui-input-block">
                            <input type="text" readonly class="layui-input" name="metadataComment">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">数据密级</label>
                        <div class="layui-input-block">
                            <input type="text" readonly class="layui-input" name="secretLevel">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">主题任务</label>
                        <div class="layui-input-block">
                            <input type="text" readonly class="layui-input" name="themeTask">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">数据来源</label>
                        <div class="layui-input-block">
                            <input type="text" readonly class="layui-input" name="dataFrom">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">采集时间</label>
                        <div class="layui-input-block">
                            <input type="text" readonly class="layui-input" name="collectionTime">
                        </div>
                    </div>
                </div>
            </div>
            <!-- 字段信息 -->
            <div class="layui-tab-item">
                <table class="layui-hide" id="metadataColumnTable" lay-filter="metadataColumnTable"></table>
            </div>
            <!-- 示例数据 -->
            <div class="layui-tab-item">
                <table class="layui-hide" id="metadataExampleTable"
                       lay-filter="metadataExampleTable"></table>
            </div>
        </div>
    </div>
`;
layui.define(['layer', 'gtable'], function (exports) {
    let layer = layui.layer,
        gtable = layui.gtable;

    const component = {
        showLayer: function (id) {
            layer.open({

            })
        }
    }
    exports('MetadataDetailsComponent', component);
});