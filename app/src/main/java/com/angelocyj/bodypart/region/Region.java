package com.angelocyj.bodypart.region;

/**
 * Created by angelo on 2015/2/28.
 */
public enum Region {
    HEAD ("头部", 1, LayoutSide.LEFT, 47, 38, 0),
    EYE ("眼部", 2, LayoutSide.LEFT, 49, 80, 0, 1),
    FACE ("面部", 5, LayoutSide.LEFT, 39, 121, 0, 2),
    THROAT ("咽喉", 6, LayoutSide.LEFT, 0, 199, 0, 3),
    EAR ("耳朵", 3, LayoutSide.RIGHT, 68, 82, 0),
    NOSEMOUTH ("口鼻", 4, LayoutSide.RIGHT, 0, 135, 0, 1),
    NECK ("脖子", 7, LayoutSide.RIGHT, 33, 192, 0, 2),
    SKIN ("皮肤", 17, LayoutSide.RIGHT, 0, 0, 0, 1),
    ARM("手臂", 10, LayoutSide.RIGHT, 170, 465, 335),
    HAND("手", 9, LayoutSide.LEFT, 245, 695, 545),
    SHOULDER("肩", 8, LayoutSide.LEFT, 120, 250, 338),
    CHEST("胸部", 11, LayoutSide.RIGHT, 59, 364, 125),
    ABDOMEN("腹部", 13, LayoutSide.LEFT, 0, 581, 415),
    WAIST("腰", 12, LayoutSide.LEFT, 112, 600, 415, 1),
    PELVIC("盆腔\n下体", 14, LayoutSide.RIGHT, 0, 687, 405),
    LEG("大腿", 15, LayoutSide.LEFT, 83, 877, 1026),
    FOOT("足", 16, LayoutSide.RIGHT, 56, 1313, 890), //1026

    BACKNECK ("脖子", 7, LayoutSide.LEFT, 0, 168, 0, 1),
    BACKSHOULDER("肩", 8, LayoutSide.LEFT, 120, 250, 168),
    BACKHAND("手", 9, LayoutSide.LEFT, 245, 695, 168, 1),
    BACKARM("手臂", 10, LayoutSide.RIGHT, 170, 465, 168, 1),
    BACKBACK("背", 18, LayoutSide.RIGHT, 0, 420, 168),
    BACKWAIST("腰", 12, LayoutSide.LEFT, 120, 610, 538),
    BACKPELVIC("盆腔\n下体", 14, LayoutSide.LEFT, 0, 656, 538, 1),
    BACKHIP("臀部", 19, LayoutSide.RIGHT, 54, 666, 538),
    BACKANUSRECTUM("肛门\n直肠", 20, LayoutSide.RIGHT, 0, 703, 538, 1),
    BACKFOOT("足", 16, LayoutSide.LEFT, 56, 1313, 1026),
    BACKLEG("大腿", 15, LayoutSide.RIGHT, 83, 877, 890),//1026
    OTHER("其他", 21, LayoutSide.RIGHT, 0, 0, 0),
    ALLDISEASE("全部症状", -1, LayoutSide.RIGHT, 0, 0, 0);

    private final String name;
    private final int value;
    private final int layoutSide;
    private final int offsetSX, offsetSY; //路径起始坐标点
    private final int offsetDY;   //部位中心点基准纵坐标
    private int startX, startY;
    private final int offSetNum;  //部位之间纵坐标偏移数
    private int destinationY; // = offsetDY + RegionParam.OFFSET_Y * offsetNum

    Region(final String name, final int value, final int layoutSide, final int offsetSX, final int offsetSY, final int offsetDY){
        this(name, value, layoutSide, offsetSX, offsetSY, offsetDY, 0, 0, 0, 0);
    }

    Region(final String name, final int value, final int layoutSide, final int offsetSX, final int offsetSY, final int offsetDY,
                   final int offSetNum){
        this(name, value, layoutSide, offsetSX, offsetSY, offsetDY, 0, 0, offSetNum, 0);
    }

    Region(final String name, final int value, final int layoutSide, final int offsetSX, final int offsetSY, final int offsetDY,
                   int startX, int startY, int offSetNum, int destinationY) {
        this.name = name;
        this.value = value;
        this.layoutSide = layoutSide;
        this.offsetSX = offsetSX;
        this.offsetSY = offsetSY;
        this.offsetDY = offsetDY;
        this.startX = startX;
        this.startY = startY;
        this.offSetNum = offSetNum;
        this.destinationY = destinationY;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static String getName(int value) {
        for (Region c : Region.values()) {
            if (c.getValue() == value) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getLayoutSide() {
        return layoutSide;
    }

    public int getOffsetSX() {
        return offsetSX;
    }

    public int getOffsetSY() {
        return offsetSY;
    }

    public int getOffsetDY() {
        return offsetDY;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getOffSetNum() {
        return offSetNum;
    }

    public int getDestinationY() {
        return destinationY;
    }

    public void setDestinationY(int destinationY) {
        this.destinationY = destinationY;
    }


    public static class LayoutSide {
        public final static int LEFT = 0;
        public final static int RIGHT = 1;
    }
}
