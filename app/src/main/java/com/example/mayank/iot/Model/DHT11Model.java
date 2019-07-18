package com.example.mayank.iot.Model;

import java.util.List;

/**
 * Created by Mayank on 3/16/2017.
 */
public class DHT11Model {

    /**
     * created_at : 2017-03-15T13:04:15-04:00
     * entry_id : 1
     * field1 : 27
     * field2 : 17
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
    }
}
