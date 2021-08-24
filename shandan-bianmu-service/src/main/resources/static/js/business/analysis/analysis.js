/**
 *  关联关系分析
 *
 * @author Administrator
 * @since 2021/6/24
 */
layui.use(['layer', 'form'], function () {
    const form = layui.form,
    layer = layui.layer;

    // 先加载数据源
    loadDatasource();

    $('#start-analysis').on('click', function(){
        const dsId = $('#dataSourcesSelect').val();
        if(!dsId){
            layer.msg('请选择数据源');
            return;
        }
        $.post(`${ctx}/business/analysis/data`, {datasourceId: dsId}, function (res) {
            if(res.flag){
                console.info(res.data)
                renderEcharts(res.data);
            }else{
                layer.msg('没有查询到相关数据')
            }
        });
    })
    /**
     * 加载数据源
     */
    function loadDatasource(){
        $.post(`${ctx}/control/datasource/list`, {}, function (res) {
            if (res.flag) {
                let html = ``;
                for (let source of res.data) {
                    html += `<option value="${source.id}" >${source.name}</option>`;
                }
                $('#dataSourcesSelect').append(html);
            } else {
                layer.msg('数据源查询失败');
            }
            form.render('select');
        })
    }

    function renderEcharts(graph){

        let chartDom = document.getElementById('echarts-div');
        let myChart = echarts.init(chartDom);
        let option = {
            tooltip: {},
            animationDuration: 1500,
            animationEasingUpdate: 'quinticInOut',
            series: [
                {
                    //name: '关联关系分析',
                    type: 'graph',
                    layout: 'force', // force, circular
                    force:{repulsion:5000},
                    symbolSize: 50,
                    data: graph.nodes,
                    links: graph.links,
                    categories: graph.categories,
                    roam: true,
                    label: {
                        show:true
                    },
                    lineStyle: {
                        color: 'source',
                        curveness: 0.3
                    },
                    edgeSymbolSize: [4, 10],
                    edgeLabel: {
                        fontSize: 20
                    },
                    emphasis: {
                        focus: 'adjacency',
                        lineStyle: {
                            width: 10
                        }
                    }
                }
            ]
        };
        myChart.setOption(option);
    }
});

