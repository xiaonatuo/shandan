/**
 * 审核状态常量
 * @type {{PASS: string, SUBMITTED: string, UN_SUBMIT: string, FAIL: string}}
 */
const ReviewStatus = {
    UN_SUBMIT: 'UN_SUBMIT',
    SUBMITTED: 'SUBMITTED', // 提交
    PASS: 'PASS', // 通过
    FAIL: 'FAIL', // 不通过
    REJECTED: 'REJECTED' //已驳回
}
/**
 * 审核状态中文释义
 * @type {{PASS: string, SUBMITTED: string, UN_SUBMIT: string, FAIL: string}}
 */
const ReviewStatusMsg = {
    UN_SUBMIT: '未发布',
    SUBMITTED: '待审核',
    PASS: '审核通过',
    FAIL: '审核不通过',
    REJECTED: '已驳回'
};
// 审核状态图标
const ReviewStatusIcon = {
    UN_SUBMIT: `<i class="layui-icon dtree-icon-circle1" title="未发布" id="##id##" data-id="##id##" style="margin-left: 5px;color: #1E9FFF;font-size: 14px;"></i>`,
    SUBMITTED: `<i class="layui-icon dtree-icon-jian1" title="等待审核" id="##id##" data-id="##id##" style="margin-left: 5px;color: orange;font-size: 14px;"></i>`,
    PASS: `<i class="layui-icon dtree-icon-roundcheck" title="审核通过" id="##id##" data-id="##id##" style="margin-left: 5px;color: green;font-size: 14px;"></i>`,
    FAIL: `<i class="layui-icon dtree-icon-roundclose icon-fail" title="审核不通过" id="tips-rw-##id##" data-id="##id##" style="margin-left: 5px;color: red;font-size: 14px;"></i>`,
    REJECTED: `<i class="layui-icon dtree-icon-roundclose icon-fail" title="已驳回" id="tips-rw-##id##" data-id="##id##" style="margin-left: 5px;color: red;font-size: 14px;"></i>`,
}

// 可预览的文件类型
const viewType = {
    image: ['.avif', '.bmp', '.gif', '.jpg', '.jpeg', '.jfif', '.pjpeg', '.pjp', '.png', '.svg', '.webp', '.ico', '.cur'],
    video: ['.mp4', '.webm', '.avi', '.mpeg', '.wmv', '.mov', '.mkv','.3gp'],
    audio: ['.mp3','.ogg', '.wav', '.aac', '.flac', '.wma', '.ape'],
    pdf: ['.pdf'],
    text: ['.txt']
};


/**
 * 审核数据实体类型
 * @type {{DIRECTORY: string, METADATA: string}}
 */
const ReviewEntityType = {
    DIRECTORY: 'DIRECTORY',
    METADATA: 'METADATA'
}

/**
 * 加解密操作简单封装一下
 */
aesUtil = {

    //获取key，
    genKey: function (length = 16) {
        let random = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        let str = "";
        for (let i = 0; i < length; i++) {
            str = str + random.charAt(Math.random() * random.length)
        }
        return str;
    },

    //加密
    encrypt: function (plaintext, key) {
        if (plaintext instanceof Object) {
            //JSON.stringify
            plaintext = JSON.stringify(plaintext)
        }
        let encrypted = CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(plaintext), CryptoJS.enc.Utf8.parse(key), {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();
    },

    //解密
    decrypt: function (ciphertext, key) {
        let decrypt = CryptoJS.AES.decrypt(ciphertext, CryptoJS.enc.Utf8.parse(key), {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        let decString = CryptoJS.enc.Utf8.stringify(decrypt).toString();
        if (decString.charAt(0) === "{" || decString.charAt(0) === "[") {
            //JSON.parse
            decString = JSON.parse(decString);
        }
        return decString;
    }
};
rsaUtil = {
    //RSA 位数，这里要跟后端对应
    bits: 1024,

    //当前JSEncrypted对象
    thisKeyPair: {},

    //生成密钥对(公钥和私钥)
    genKeyPair: function (bits = rsaUtil.bits) {
        let genKeyPair = {};
        rsaUtil.thisKeyPair = new JSEncrypt({default_key_size: bits});

        //获取私钥
        genKeyPair.privateKey = rsaUtil.thisKeyPair.getPrivateKey();

        //获取公钥
        genKeyPair.publicKey = rsaUtil.thisKeyPair.getPublicKey();

        return genKeyPair;
    },

    //公钥加密
    encrypt: function (plaintext, publicKey) {
        if (plaintext instanceof Object) {
            //1、JSON.stringify
            plaintext = JSON.stringify(plaintext)
        }
        publicKey && rsaUtil.thisKeyPair.setPublicKey(publicKey);
        return rsaUtil.thisKeyPair.encrypt(plaintext);
    },

    //私钥解密
    decrypt: function (ciphertext, privateKey) {
        privateKey && rsaUtil.thisKeyPair.setPrivateKey(privateKey);
        let decString = rsaUtil.thisKeyPair.decrypt(ciphertext);
        if (decString.charAt(0) === "{" || decString.charAt(0) === "[") {
            //JSON.parse
            decString = JSON.parse(decString);
        }
        return decString;
    }
};

/**
 * jQuery扩展
 */
jQueryExtend = {
    /**
     * 是否已经进行jq的ajax加密重写
     */
    ajaxExtendFlag: false,
    /**
     * 扩展jquery对象方法
     */
    fnExtend: function () {
        /**
         * 拓展表单对象：用于将对象序列化为JSON对象
         */
        $.fn.serializeObject = function () {
            var o = {};
            var a = this.serializeArray();
            $.each(a, function () {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        };

        /**
         * 拓展表单对象：表单自动回显
         * 使用参考：$("#form1").form({"id":"112","username":"ff","password":"111","type":"admin"});
         */
        $.fn.form = function (data) {
            let form = $(this);
            for (let i in data) {
                let name = i;
                let value = data[i];
                if (name !== "" && value !== "") {
                    valuAtion(name, value);
                }
            }

            function valuAtion(name, value) {
                if (form.length < 1) {
                    return;
                }
                if (form.find("[name='" + name + "']").length < 1) {
                    return;
                }
                let input = form.find("[name='" + name + "']")[0];
                if ($.inArray(input.type, ["text", "password", "hidden", "select-one", "textarea"]) > -1) {
                    $(input).val(value);
                } else if (input.type === "radio" || input.type === "checkbox") {
                    form.find("[name='" + name + "'][value='" + value + "']").attr("checked", true);
                }
            }
        };

        /**
         * 拓展jQuery对象：快速AJAX Delete删除
         */
        $.delete = function (url, params, callback) {
            if (!params || typeof params === 'string') {
                throw new Error('Error Params：' + params);
            }

            $.ajax({
                url: url,
                type: "DELETE",
                contentType: 'application/json',//发送格式（JSON串）
                data: JSON.stringify(params),
                success: function (result) {
                    callback && callback(result);
                }
            });
        };
    },

    /**
     * 重写jq的ajax加密，并保留原始ajax，命名为_ajax
     */
    ajaxExtend: function () {
        //判断api加密开关
        let flag = sessionStorage.getItem('sysApiEncrypt') == 'true';
        if (flag && !jQueryExtend.ajaxExtendFlag) {
            jQueryExtend.ajaxExtendFlag = true;
            let _ajax = $.ajax;//首先备份下jquery的ajax方法
            $.ajax = function (opt) {
                //默认值
                // opt = {
                //     type: 'post',
                //     url: url,
                //     contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                //     dataType: 'json',
                //     data: data,
                //     success: success,
                //     error: function (xhr, status, error) {
                //         console.log("ajax错误！");
                //     }
                // };

                //备份opt中error和success方法
                let fn = {
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    },
                    success: function (data, textStatus) {
                    }
                };
                if (opt.error) {
                    fn.error = opt.error;
                }
                if (opt.success) {
                    fn.success = opt.success;
                }

                let isFile = false;
                if (opt.data instanceof FormData) {
                    let file = opt.data.get('file');
                    if (file) {
                        isFile = true
                    }
                }

                //加密再传输
                if (opt.type.toLowerCase() === "post" && !opt.url.endsWith('file/upload') && !isFile) {
                    let data = opt.data;
                    //发送请求之前随机获取AES的key
                    let aesKey = aesUtil.genKey();
                    data = {
                        data: aesUtil.encrypt(data, aesKey),//AES加密后的数据
                        aesKey: rsaUtil.encrypt(aesKey, sessionStorage.getItem('javaPublicKey')),//后端RSA公钥加密后的AES的key
                        publicKey: window.jsPublicKey//前端公钥
                    };
                    opt.data = data;
                }

                //扩展增强处理
                let _opt = $.extend(opt, {
                    //成功回调方法增强处理
                    success: function (data, textStatus, request) {
                        try {
                            if (opt.type.toLowerCase() === "post" && data.data && !opt.url.endsWith('file/upload') && !isFile) {
                                data = aesUtil.decrypt(data.data.data, rsaUtil.decrypt(data.data.aesKey, window.jsPrivateKey));
                            }
                        } catch (e) {
                            console.error('加解密操作异常')
                        }
                        //先获取明文aesKey，再用明文key去解密数据
                        fn.success(data, textStatus, request);
                    },
                    error: function (res){
                        if(res.status == 401){
                            window.location.replace(`${ctx}/loginPage`)
                        }
                        fn.error(res)
                    }
                });
                return _ajax(_opt);
            };
        }
    },
};

let loadIndex = undefined;
window.showLoading = () => {
    if (!loadIndex) {
        loadIndex = layer.load();
    }
}
window.closeLoading = () => {
    if (loadIndex) {
        layer.close(loadIndex);
        loadIndex = undefined;
    }
}
window.showWarningMsg = (msg = '警告提示') => {
    layer.msg(msg, {icon: 0, time: 2000})
}
window.showOkMsg = (msg = 'OK提示') => {
    layer.msg(msg, {icon: 1, time: 1000})
}
window.showErrorMsg = (msg = '系统异常') => {
    layer.msg(msg, {icon: 2, time: 2000})
}
window.showConfirmMsg = (msg = '疑问提示') => {
    layer.msg(msg, {icon: 3, time: 2000})
}
window.showLockMsg = (msg = '禁用提示') => {
    layer.msg(msg, {icon: 4, time: 2000})
}
window.showErrorMsg2 = (msg = '服务器连接异常') => {
    layer.msg(msg, {icon: 5, time: 2000})
}
window.showOkMsg2 = (msg = 'OK提示') => {layer.msg(msg, {icon: 6, time: 1000})}

/**
 * 常用工具方法
 */
commonUtil = {
    /**
     * 获取当前时间，并格式化输出为：2018-05-18 14:21:46
     */
    getNowTime: function () {
        var time = new Date();
        var year = time.getFullYear();//获取年
        var month = time.getMonth() + 1;//或者月
        var day = time.getDate();//或者天

        var hour = time.getHours();//获取小时
        var minu = time.getMinutes();//获取分钟
        var second = time.getSeconds();//或者秒
        var data = year + "-";
        if (month < 10) {
            data += "0";
        }
        data += month + "-";
        if (day < 10) {
            data += "0"
        }
        data += day + " ";
        if (hour < 10) {
            data += "0"
        }


        data += hour + ":";
        if (minu < 10) {
            data += "0"
        }
        data += minu + ":";
        if (second < 10) {
            data += "0"
        }
        data += second;
        return data;
    },

    /**
     * 将我们响应的系统菜单数据转换成符合layui的tree结构
     */
    updateKeyForLayuiTree: function (arrar, spread = true) {
        let newArray = [];
        for (let i = 0; i < arrar.length; i++) {
            let obj1 = {};
            let obj = arrar[i];
            obj1.id = obj.menuId;
            obj1.title = obj.menuName;
            obj1.href = obj.menuPath;
            obj1.spread = spread;
            //自定义数据
            obj1.sortWeight = obj.sortWeight;

            if (obj.children.length > 0) {
                obj1.children = this.updateKeyForLayuiTree(obj.children);
            }
            newArray.push(obj1);
        }
        return newArray
    },

    /**
     * get请求
     * @param url 请求地址
     * @param data 请求数据
     * @param loading 是否显示loading状态
     * @returns {Promise<unknown>}
     */
    get: (url, data, loading=true) => Util.send(url, data, loading),

    /**
     * post请求
     * @param url 请求地址
     * @param data 请求数据
     * @param loading 是否显示loading状态
     * @returns {Promise<unknown>}
     */
    post: (url, data, loading=true) => Util.send(url, data, 'post',loading),

    /**
     * 封装ajax异步请求
     * @param url 请求地址
     * @param data 请求数据
     * @param type 请求类型
     * @param loading 是否显示loading状态
     * @returns {Promise<unknown>}
     */
    send: (url, data, type = 'get', loading=true) => {
        if(loading) showLoading()
        let promise = new Promise(function (resolve, reject) {
            url = url.startsWith("http://") || url.startsWith("https://") ? url : `${ctx}${url}`
            $.ajax({
                url,
                type: type,
                data: data,
                dataType: 'json',
                success: resolve,
                error: reject
            })
        })
        promise.finally(() => {
            if (loading) closeLoading()
        });
        return promise;
    },

    /**
     * 休眠
     * @param delay
     * @returns {Promise<unknown>}
     */
    sleep: function (delay) {
        return new Promise(resolve => setTimeout(resolve, delay));
    }
};
const Util = commonUtil;


/* 以下代码所有页面统一执行  */

//扩展jquery对象方法
jQueryExtend.fnExtend();

//获取前端RSA公钥密码、AES的key，并放到window
let genKeyPair = rsaUtil.genKeyPair();
window.jsPublicKey = genKeyPair.publicKey;
window.jsPrivateKey = genKeyPair.privateKey;

//重写jq的ajax加密
jQueryExtend.ajaxExtend();


const dict_url = `/sys/dict/list`;
// 储存的key
const STORE_DICT_KEY = 'SYS_DICT_DATA'
const STORE_DICT_INIT_TIME_KEY = 'SYS_DICT_INIT_TIME';
// 数据字典缓存初始化时间
let initTime = _Store.get(STORE_DICT_INIT_TIME_KEY)
const currentTime = new Date().getTime();
// 当上次初始化时间大于当前时间1分钟后，才可以执行初始化，避免重复请求
if(!initTime || (currentTime - initTime) > 1000 * 60){
    _Store.set(STORE_DICT_INIT_TIME_KEY, new Date().getTime());
    Util.post(dict_url, {}, false).then(res=>{
        if(res.flag){
            let data = res.data;
            let dict = {};
            _Store.set(STORE_DICT_KEY, dict)
            for (let item of data) {
                let key = item.typeId;
                if(!dict[key]){
                    dict[key] = [];
                }
                dict[key].push(item);
            }
            _Store.set(STORE_DICT_KEY, dict)
        }
    })
}

/**
 * layui组件扩展
 */
layui.config({
    base: `${ctx}/js/common/layui/extend/`
}).extend({
    dtree: 'dtree/dtree',
    dict: 'dict', // 数据字典组件
    globalTree: 'globalTree', // 通用树组件
    orgTree: 'orgTree', // 部门树组件
    menuTree: 'menuTree', // 菜单树组件
    gtable: 'gtable', // 数据表格组件
    editPage: 'editPage', // 数据表格组件
    listPage: 'listPage', // 数据表格组件
})



