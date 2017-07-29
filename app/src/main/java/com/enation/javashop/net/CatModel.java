package com.enation.javashop.net;

/**
 * Created by LDD on 17/7/28.
 */

public class CatModel {

    /**
     * category_id : 516
     * name : 一会测试的都删除
     * parent_id : 0
     * category_path : 0|516|
     * goods_count : 0
     * category_order : 0
     * list_show : 1
     * image :
     * total_num : 0
     * state : null
     * children : null
     */

    private int category_id;
    private String name;
    private int parent_id;
    private String category_path;
    private int goods_count;
    private int category_order;
    private int list_show;
    private String image;
    private int total_num;
    private Object state;
    private Object children;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getCategory_path() {
        return category_path;
    }

    public void setCategory_path(String category_path) {
        this.category_path = category_path;
    }

    public int getGoods_count() {
        return goods_count;
    }

    public void setGoods_count(int goods_count) {
        this.goods_count = goods_count;
    }

    public int getCategory_order() {
        return category_order;
    }

    public void setCategory_order(int category_order) {
        this.category_order = category_order;
    }

    public int getList_show() {
        return list_show;
    }

    public void setList_show(int list_show) {
        this.list_show = list_show;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }
}
