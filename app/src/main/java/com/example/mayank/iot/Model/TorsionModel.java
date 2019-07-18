package com.example.mayank.iot.Model;

import java.util.List;

/**
 * Created by Mayank on 3/16/2017.
 */
public class TorsionModel {


    /**
     * created_at : 2017-03-15T11:13:30-04:00
     * entry_id : 1
     * field1 : 15March2017
     * field2 : ABC
     * field3 : 11
     * field4 : 19
     * field5 : 11
     * field6 : 41
     * field7 : 45
     */

    private List<FeedsEntity> feeds;

    public List<FeedsEntity> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<FeedsEntity> feeds) {
        this.feeds = feeds;
    }

    public static class FeedsEntity {
        private String created_at;
        private int entry_id;
        private String field1;
        private String field2;
        private String field3;
        private String field4;
        private String field5;
        private String field6;
        private String field7;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getEntry_id() {
            return entry_id;
        }

        public void setEntry_id(int entry_id) {
            this.entry_id = entry_id;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField3() {
            return Float.parseFloat(field3) / 10.0 + "";
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }

        public String getField4() {
            return Float.parseFloat(field4) / 10.0 + "";
        }

        public void setField4(String field4) {
            this.field4 = field4;
        }

        public String getField5() {
            return Float.parseFloat(field5) / 10.0 + "";
        }

        public void setField5(String field5) {
            this.field5 = field5;
        }

        public String getField6() {
            return Float.parseFloat(field6) / 10.0 + "";
        }

        public void setField6(String field6) {
            this.field6 = field6;
        }

        public String getField7() {
            return field7;
        }

        public void setField7(String field7) {
            this.field7 = field7;
        }
    }
}
