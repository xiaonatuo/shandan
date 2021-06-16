/**
 * <p>
 *  本地存储
 * </p>
 *
 * @author Administrator
 * @since 2021/6/15
 */
class Store {
    constructor() {
        this._store = window.localStorage;
        //this._store.clear();
    }

    /**
     * 添加值
     * @param _k
     * @param _v
     */
    set(_k, _v) {
        if (!this._store) return
        let kType = this.getType(_k)
        if (kType === 'string') {
            this._store.setItem(_k, this.filterValue(_v))
        } else {
            console.error('key只能为字符串！')
        }
    }

    /**
     * 获取值
     * @param _k
     */
    get(_k) {
        if (!this._store) return

        let kType = this.getType(_k)
        if (kType !== 'string') {
            return console.error('key只能为字符串！')
        }
        let data = this._store.getItem(_k)
        if(typeof data === 'string'){
            try {
                return JSON.parse(data)
            }catch (e) {
            }
        }
        return data;
    }

    /**
     * @function 移除值
     * @param {string} _k 必须参数，属性
     */
    removeItem(_k) {
        if (!this._store) return
        let kType = this.getType(_k)
        if (kType === 'string') {
            this._store.removeItem(_k)
        } else {
            console.error('key只能为字符串！')
        }
    }

    /**
     * @function 判断类型
     * @param {any} para 必须参数，判断的值
     */
    getType(para) {
        let type = typeof para
        if (type === 'number' && isNaN(para)) return 'NaN'
        if (type !== 'object') return type
        return Object.prototype.toString
            .call(para)
            .replace(/[\[\]]/g, '') // eslint-disable-line
            .split(' ')[1]
            .toLowerCase()
    }

    /**
     * @function 过滤值
     * @param {any} val 必须参数，过滤的值
     */
    filterValue(val) {
        let vType = this.getType(val)
        let nullVal = ['null', 'undefined', 'NaN']
        let stringVal = ['boolen', 'number', 'string']
        if (nullVal.includes(vType)) return ''
        if (stringVal.includes(vType)) return val
        return JSON.stringify(val);
    }

}

const _Store = new Store();
