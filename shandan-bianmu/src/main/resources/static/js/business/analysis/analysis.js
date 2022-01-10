/**
 *  关联关系分析
 *
 * @author Administrator
 * @since 2021/6/24
 */
layui.use(['layer', 'form','laytpl', 'table', 'dict'], function () {
    const form = layui.form,
    layer = layui.layer;

    // 数据资源表map，由子页面回传
    let metadataTableMap = new Map();

    /**
     * 打开选择数据表的弹窗
     */
    $("#btn_selectAnalysis").on('click', function(){
        let layerWin;
        parent.layer.open({
            id: 'selectTable',
            type: 2,
            area: ['550px', '550px'],
            content: `${ctx}/business/analysis/selectAnalysisLayer`,
            btn: ['确定', '取消'],
            success: function (layerObj) {
                layerWin = parent.window[layerObj.find('iframe')[0]['name']]
            },
            yes: function (index) {
                layerWin.ok().then((data) => {
                    chooseData = data;
                    $("#dataSourcesSelect").val(chooseData.data.metadataName);
                    $("#dataSourcesSelectId").val(chooseData.data.id);
                    //renderEcharts(res.data);

                    $.post(`${ctx}/business/analysis/data`, {datasourceId: chooseData.data.id}, function (res) {
                        if(res.flag){
                            renderEcharts(res.data);
                        }else{
                            layer.msg('没有查询到相关数据')
                        }
                    });
                })
                parent.layer.close(index);
            }
        });
    });


    function renderEcharts(graph){

        let chartDom = document.getElementById('echarts-div');
        let myChart = echarts.init(chartDom);
        let option = {
            tooltip: {},
            animationDuration: 1500,
            animationEasingUpdate: 'quinticInOut',
            series: [
                {
                    name: '关联关系分析',
                    type: 'graph',
                    layout: 'force', // force, circular
                    force:{
                        repulsion:5000
                    },
                    symbolSize: 80,
                    data: graph.nodes,
                    links: graph.links,
                    categories: graph.categories,
                    roam: false,                    //拖拽 移动
                    label: {
                        show:true
                    },
                    lineStyle: {
                        normal: {
                            color: '#000',          // 线的颜色[ default: '#aaa' ]
                            width: 1,               // 线宽[ default: 1 ]
                            type: 'solid',          // 线的类型[ default: solid ]   'dashed'    'dotted'
                            opacity: 0.5,           // 图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。[ default: 0.5 ]
                            curveness: 0            // 边的曲度，支持从 0 到 1 的值，值越大曲度越大。[ default: 0 ]
                        }
                    },
                    edgeSymbolSize: [4, 10],
                    edgeLabel: {//边的设置
                        //show: true,
                        //position: "middle",
                        fontSize: 5,
                    },
                    edgeSymbol: ["circle", "arrow"], //边两边的类型
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

