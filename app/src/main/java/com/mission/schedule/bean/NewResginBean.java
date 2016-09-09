package com.mission.schedule.bean;

import java.util.List;

/**
 * Created by lenovo on 2016/7/21.
 */
public class NewResginBean {
    /**
     * list : [{"yzm":"916657","mobile":"13546449523"}]
     * message : 成功
     * status : 0
     */

    private String message;
    private int status;
    /**
     * yzm : 916657
     * mobile : 13546449523
     */

    private List<ListBean> list;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String yzm;
        private String mobile;

        public String getYzm() {
            return yzm;
        }

        public void setYzm(String yzm) {
            this.yzm = yzm;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
