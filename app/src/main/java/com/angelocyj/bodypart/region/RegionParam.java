package com.angelocyj.bodypart.region;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by angelo on 2015/2/26.
 */
public class RegionParam {
    public static final int REGION_FRONT_HEAD = 0x00000001;
    public static final int REGION_FRONT_HAND = 0x00000002;
    public static final int REGION_FRONT_CHEST = 0x00000004;
    public static final int REGION_FRONT_WAIST = 0x00000008;
    public static final int REGION_FRONT_LEG = 0x00000010;
    public static final int REGION_BACK_HEAD = 0x00000020;
    public static final int REGION_BACK_UPPER_PART = 0x00000040;
    public static final int REGION_BACK_MIDDLE_PART = 0x00000080;
    public static final int REGION_BACK_LOWER_PART = 0x00000100;

    /**
     * 身体图片标准长度 in pixels
     */
    public static final int standardHeight = 1378;

    /**
     * 身体图片标准宽度 in pixels
     */
    public static final int standardWidth = 693;

    /**
     * 顶部透明部分偏移量
     */
    public static final int standardOffsetY = 10;

    /**
     * 圆形部位宽度  in dp
     */
    public static final int REGION_WIDTH = 50;

    /**
     * 部位之间的纵坐标偏移量  in dp
     */
    public static final int STANDARD_OFFSET_Y = 20;

    /**
     * 转化成px后的部位纵坐标偏移量  in pixels
     */
    public static int OFFSET_Y = 90;

    /**
     * 路径横坐标折点偏移量 in dp
     */
    public static final float PATH_OFFSET_X = REGION_WIDTH + 20f;
    public static int LEFT_REGION_X = 50;
    public static int RIGHT_REGION_X = 50;

    public static final Map<Integer,Region[]> regionItems = new HashMap<>();
    static {
        regionItems.put(REGION_FRONT_HEAD, new Region[]{Region.HEAD, Region.EYE, Region.FACE, Region.THROAT, Region.EAR, Region.NOSEMOUTH, Region.NECK});
        regionItems.put(REGION_FRONT_HAND, new Region[]{Region.HAND, Region.ARM});
        regionItems.put(REGION_FRONT_CHEST, new Region[]{Region.SHOULDER, Region.CHEST});
        regionItems.put(REGION_FRONT_WAIST, new Region[]{Region.ABDOMEN, Region.WAIST, Region.PELVIC});
        regionItems.put(REGION_FRONT_LEG, new Region[]{Region.LEG, Region.FOOT});

        regionItems.put(REGION_BACK_HEAD, new Region[]{Region.HEAD, Region.BACKNECK, Region.EAR});
        regionItems.put(REGION_BACK_UPPER_PART, new Region[]{Region.BACKSHOULDER, Region.BACKHAND, Region.BACKBACK, Region.BACKARM});
        regionItems.put(REGION_BACK_MIDDLE_PART, new Region[]{Region.BACKWAIST, Region.BACKPELVIC, Region.BACKHIP, Region.BACKANUSRECTUM});
        regionItems.put(REGION_BACK_LOWER_PART, new Region[]{Region.BACKFOOT, Region.BACKLEG});

    }
}
