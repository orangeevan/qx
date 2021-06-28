package com.mmorpg.qx.common;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.haipaite.common.utility.MathUtils;
import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.move.Road;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.model.VirPoint;
import com.mmorpg.qx.module.worldMap.model.WorldMap;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.apache.commons.validator.routines.RegexValidator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * 游戏工具类
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public final class GameUtil {

    public static final float WIDTH = 60;
    public static final float HIGH = 30;
    public static final float RATIO_DIVISOR_100 = 100;
    public static final float RATIO_DIVISOR_10000 = 10000;
    public static final int[] DICE_POINTS = {1, 2, 3, 4, 5, 6};
    //public static final int[] DICE_POINTS = {6, 6, 6, 6, 6, 6};
    //public static final int[] DICE_POINTS = {1, 1, 1, 1, 1, 1};
    final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static ThreadLocal<ByteBuffer> tb = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(10240);
        }

        ;
    };
    private static final RegexValidator ipv4Validator = new RegexValidator("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
    private static ThreadLocal<Random> tr = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random(System.currentTimeMillis());
        }
    };

    public static Random getRandom() {
        return tr.get();
    }

    /**
     * 获取地格相邻格子
     *
     * @param worldMap
     * @param centerGrid
     * @param includeCenter
     * @return
     */
    public static List<MapGrid> findAroundGrid(WorldMap worldMap, int centerGrid, boolean includeCenter) {
        MapGrid mapGrid = worldMap.getMapGrid(centerGrid);
        List<MapGrid> aroundGrids = new ArrayList<>();
        if (includeCenter) {
            aroundGrids.add(mapGrid);
        }
        Map<DirType, Integer> neighbor = mapGrid.getNeighbor();
        if (CollectionUtils.isEmpty(neighbor)) {
            return aroundGrids;
        }
        neighbor.values().forEach(grid -> aroundGrids.add(worldMap.getMapGrid(grid)));
        return aroundGrids;
    }

    /**
     * 配置表属性构造，属性字段注意定义成public
     *
     * @param resource
     * @return
     */
    public static List<Attr> buildAttr(Object resource) {
        List<Attr> baseAttrsList = new ArrayList<>();
        Arrays.stream(resource.getClass().getFields()).forEach(field -> {
            field.setAccessible(true);
            String lowerCase = field.getName().toLowerCase();
            if (AttrType.isAttrName(lowerCase)) {
                try {
                    int value = (int) field.get(resource);
                    if (value <= 0) {
                        return;
                    }
                    AttrType attrType = AttrType.getByLowerName(lowerCase);
                    Attr attr = new Attr(attrType, value);
                    baseAttrsList.add(attr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return baseAttrsList;
    }

    /**
     * 属性列表是否有对应属性类型
     *
     * @param type
     * @param baseAttrsList
     * @return
     */
    public static boolean hasAttrType(AttrType type, List<Attr> baseAttrsList) {
        if (CollectionUtils.isEmpty(baseAttrsList)) {
            return false;
        }
        return baseAttrsList.stream().anyMatch(attr -> attr.getType() == type);
    }

    public static boolean isInArray(Object object, Object... objectArray) {
        if (object == null || objectArray == null) {
            return false;
        }
        return Arrays.stream(objectArray).anyMatch(o -> o.equals(object));
    }

    /**
     * 固定方向根据移动步数生成移动路径
     *
     * @param creature
     * @param step
     * @param beginDir
     * @param needCheckGrid
     * @return
     */
    public static Road generateRoadByStep(AbstractCreature creature, int step, DirType beginDir, boolean needCheckGrid) {
        WorldPosition firstPosition = creature.getPosition();
        if (firstPosition == null || step <= 0) {
            return null;
        }
        WorldMapInstance mapInstance = WorldMapService.getInstance().getWorldMapInstance(firstPosition.getMapId(), firstPosition.getInstanceId());
        if (mapInstance == null) {
            return null;
        }
        MapGrid mapGrid = mapInstance.getParent().getMapGrid(firstPosition.getGridId());
        if (mapGrid == null) {
            System.err.println(String.format("当前地图格子【%s】为空", firstPosition.getGridId()));
            return null;
        }
        List<Byte> roads = new ArrayList<>(step);
        int index = 0;
        DirType stepDir = beginDir;
        while (index < step) {
            int fixDirNeighbor = mapGrid.getFixDirNeighbor(stepDir);
            //指定方向没有格子
            if (fixDirNeighbor < 0) {
                stepDir = mapGrid.randomDirNeighbor(stepDir == null ? null : stepDir.getOpposite());
                fixDirNeighbor = mapGrid.getFixDirNeighbor(stepDir);
            }
            //到头了，事实不可能出现，每个格子都是有相邻的
            if (stepDir == null) {
                break;
            }
            roads.add(stepDir.getDir());
            index++;
            mapGrid = mapInstance.getParent().getMapGrid(fixDirNeighbor);
            if (Objects.isNull(mapGrid)) {
                break;
            }
            //如果目标格子上有魔物娘或者建筑，停留
            if (needCheckGrid) {
                Collection<AbstractVisibleObject> objects = mapInstance.findObjects(mapGrid.getId());
                if (!CollectionUtils.isEmpty(objects)) {
                    boolean needStop = objects.stream().anyMatch(object -> isInArray(object.getObjectType(), new Object[]{ObjectType.getBuildType(), ObjectType.FIRE, ObjectType.Equip, ObjectType.ITEM}));
                    if (needStop) {
                        break;
                    }
                    AbstractCreature mwn = objects.stream().filter(object -> object.getObjectType() == ObjectType.MWN).map(object -> (AbstractCreature) object).findFirst().orElse(null);
                    if (mwn != null) {
                        //敌方魔物娘
                        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(mwn);
                        if (RelationshipUtils.judgeRelationship(creature, mwnCreature, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)) {
                            //如果敌方魔物娘对驯养师触发效果需要停止
                            if (mwnCreature.getEffectController().hasAnyEffectTrigger(TriggerType.Move_Pass, creature, 0)) {
                                System.err.println("当前格子有敌方魔物娘触发效果，停止");
                                break;
                            }
                        }
                    }
//                    Optional<AbstractCreature> trainer = objects.stream().filter(object -> object.getObjectType() == ObjectType.PLAYER_TRAINER).map(object -> (AbstractCreature) object).findAny();
//                    if (trainer.isPresent()) {
//                        //敌方魔物娘
//                        AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(trainer.get());
//                        if (RelationshipUtils.judgeRelationship(creature, trainerCreature, RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER)) {
//                            System.err.println("当前格子有敌方驯养师，停止");
//                            break;
//                        }
//                    }
                }
            }
        }
        WorldPosition worldPosition;
        if (mapInstance.hasPosition(mapGrid.getId())) {
            worldPosition = mapInstance.getPosition(mapGrid.getId());
        } else {
            worldPosition = WorldMapService.getInstance().createWorldPosition(mapInstance, mapGrid.getId());
        }
        byte[] finalRoads = new byte[roads.size()];
        for (int in = 0; in < roads.size(); in++) {
            finalRoads[in] = roads.get(in);
        }
        Road road = Road.valueOf(finalRoads);
        road.setTargetPosition(worldPosition);
        return road;
    }

    /**
     * 根据原格子跟方向获取相邻一个格子
     *
     * @param worldMapInstance
     * @param originalGrid
     * @param dirType
     * @return
     */
    public static WorldPosition getNeighborGridByDir(WorldMapInstance worldMapInstance, WorldPosition originalGrid, DirType dirType) {
        if (Objects.isNull(worldMapInstance) || Objects.isNull(originalGrid) || Objects.isNull(dirType)) {
            return null;
        }
        MapGrid mapGrid = worldMapInstance.getParent().getMapGrid(originalGrid.getGridId());
        int fixDirNeighbor = mapGrid.getFixDirNeighbor(dirType);
        if (fixDirNeighbor <= 0) {
            DirType neighbor = mapGrid.randomDirNeighbor(dirType.getOpposite());
            fixDirNeighbor = mapGrid.getFixDirNeighbor(neighbor);
        }
        WorldPosition worldPosition = WorldMapService.getInstance().createWorldPosition(worldMapInstance, fixDirNeighbor);
        return worldPosition;
    }


    /**
     * 转换百分比
     *
     * @param value
     * @return
     */
    public static float toRatio100(int value) {
        return value / RATIO_DIVISOR_100;
    }

    /***
     * 转换万分比
     * @param value
     * @return
     */
    public static float toRatio10000(int value) {
        return value / RATIO_DIVISOR_10000;
    }

    /**
     * 前端目前无法解析整数列表，封装对象列表
     *
     * @param values
     * @return
     */
    public static List<IntegerVo> toIntegerVo(List<Integer> values) {
        List<IntegerVo> intvalues = new ArrayList<>();
        if (CollectionUtils.isEmpty(values)) {
            return intvalues;
        }
        values.stream().forEach(v -> intvalues.add(IntegerVo.valueOf(v)));
        return intvalues;
    }

    public static List<IntegerVo> toIntegerVo(byte[] roads) {
        List<IntegerVo> result = new ArrayList<>();
        if (Objects.isNull(roads) || roads.length == 0) {
            return result;
        }
        for (byte r : roads) {
            result.add(IntegerVo.valueOf(r));
        }
        return result;
    }

    public static class IntegerVo {
        @Protobuf(description = "整数值")
        private int value;

        public static IntegerVo valueOf(int value) {
            IntegerVo vo = new IntegerVo();
            vo.value = value;
            return vo;
        }
    }

    public static class LongVo {
        @Protobuf(description = "long数值")
        private long value;

        public static LongVo valueOf(long value) {
            LongVo longVo = new LongVo();
            longVo.value = value;
            return longVo;
        }
    }

    public static List<LongVo> toLongVo(List<Long> longList) {
        List<LongVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(longList)) {
            return result;
        }
        for (long r : longList) {
            result.add(LongVo.valueOf(r));
        }
        return result;
    }

    public static boolean hasJobOrEleAttr(List<Attr> attrs) {
        if (CollectionUtils.isEmpty(attrs)) {
            return false;
        }
        for (Attr attr : attrs) {
            if (AttrType.isJobOrEle(attr.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据中心格子号，两个方向领域范围，判定目标格是否在范围内
     *
     * @param worldMap     地图
     * @param centerPoint     中心坐标
     * @param targetGid    目标
     * @param faceRange    进攻方朝向领域范围
     * @param faceVerRange 进攻方朝向垂直领域范围S
     * @return
     */
    public static boolean isInRange(WorldMap worldMap, VirPoint centerPoint, int targetGid, int faceRange, int faceVerRange) {
        MapGrid tGid = worldMap.getMapGrid(targetGid);
        switch (centerPoint.getFaceDir()) {
            case XM:
            case XP:
                return isInRange(tGid.getX2(), centerPoint.getPointX() - faceRange, centerPoint.getPointX() + faceRange) &&
                        isInRange(tGid.getY2(), centerPoint.getPointY() - faceVerRange, centerPoint.getPointY() + faceVerRange);
            case YP:
            case YM:
                return isInRange(tGid.getX2(), centerPoint.getPointX() - faceVerRange, centerPoint.getPointX() + faceVerRange) &&
                        isInRange(tGid.getY2(), centerPoint.getPointY() - faceRange, centerPoint.getPointY() + faceRange);
        }
        return true;
    }

    /**
     * 根据格子号生成格子放置魔物娘虚拟坐标
     *
     * @param worldMap
     * @param sGrid
     * @return
     */
    public static VirPoint getMwnVirPoint(WorldMap worldMap, int sGrid) {
        MapGrid grid = worldMap.getMapGrid(sGrid);
        DirType birthDir = grid.getBirthDir();
        switch (birthDir) {
            case XM:
                return VirPoint.valueOf(grid.getX2() - 1, grid.getY2(), DirType.XP);
            case XP:
                return VirPoint.valueOf(grid.getX2() + 1, grid.getY2(), DirType.XM);
            case YM:
                return VirPoint.valueOf(grid.getX2(), grid.getY2() - 1, DirType.YP);
            case YP:
                return VirPoint.valueOf(grid.getX2(), grid.getY2() + 1, DirType.YM);
            default:
                return null;
        }
    }

    public static boolean isInRange(int checkNum, int min, int max) {
        return checkNum >= min && checkNum <= max;
    }


    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

    public static String currentStackTrace() {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            sb.append("\n\t");
            sb.append(ste.toString());
        }

        return sb.toString();
    }

    public static long elapsedTimeByNow(final long beginTime) {
        return System.currentTimeMillis() - beginTime;
    }

    public static long computeNextMorningTimeMillis() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long computeNextMinutesTimeMillis() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 0);
        cal.add(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long computeNextHourTimeMillis() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 0);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long computeNextHalfHourTimeMillis() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 0);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static String timeMillisToHumanString2(final long t) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        return String.format("%04d-%02d-%02d %02d:%02d:%02d,%03d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND));
    }

    public static String timeMillisToHumanString3(final long t) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        return String.format("%04d%02d%02d%02d%02d%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
    }

    public static int getDiskPartitionSpaceUsedPercent(final String path) {
        if (null == path || path.isEmpty())
            return -1;
        try {
            File file = new File(path);
            if (!file.exists())
                return -1;
            long totalSpace = file.getTotalSpace();
            if (totalSpace > 0) {
                long freeSpace = file.getFreeSpace();
                long usedSpace = totalSpace - freeSpace;
                return (int) (usedSpace / totalSpace) * 100;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public static int crc32(byte[] array) {
        if (array != null) {
            return crc32(array, 0, array.length);
        }

        return 0;
    }

    public static int crc32(byte[] array, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(array, offset, length);
        return (int) (crc32.getValue() & 0x7FFFFFFF);
    }

    public static String bytes2string(byte[] src) {
        char[] hexChars = new char[src.length * 2];
        for (int j = 0; j < src.length; j++) {
            int v = src[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] string2bytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] uncompress(final byte[] src) throws IOException {
        byte[] result = src;
        byte[] uncompressData = new byte[src.length];
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);

        try {
            while (true) {
                int len = inflaterInputStream.read(uncompressData, 0, uncompressData.length);
                if (len <= 0) {
                    break;
                }
                byteArrayOutputStream.write(uncompressData, 0, len);
            }
            byteArrayOutputStream.flush();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                throw e;
            }
            try {
                inflaterInputStream.close();
            } catch (IOException e) {
                throw e;
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                throw e;
            }
        }

        return result;
    }

    public static byte[] compress(final byte[] src, final int level) throws IOException {
        byte[] result = src;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);
        java.util.zip.Deflater defeater = new java.util.zip.Deflater(level);
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, defeater);
        try {
            deflaterOutputStream.write(src);
            deflaterOutputStream.finish();
            deflaterOutputStream.close();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            defeater.end();
            throw e;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException ignored) {
            }

            defeater.end();
        }

        return result;
    }

    public static int asInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long asLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Date parseDate(String date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }


    public static String frontStringAtLeast(final String str, final int size) {
        if (str != null) {
            if (str.length() > size) {
                return str.substring(0, size);
            }
        }

        return str;
    }

    public static String jstack() {
        return jstack(Thread.getAllStackTraces());
    }

    public static String jstack(Map<Thread, StackTraceElement[]> map) {
        StringBuilder result = new StringBuilder();
        try {
            Iterator<Map.Entry<Thread, StackTraceElement[]>> ite = map.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = ite.next();
                StackTraceElement[] elements = entry.getValue();
                Thread thread = entry.getKey();
                if (elements != null && elements.length > 0) {
                    String threadName = entry.getKey().getName();
                    result.append(String.format("%-40sTID: %d STATE: %s%n", threadName, thread.getId(), thread.getState()));
                    for (StackTraceElement el : elements) {
                        result.append(String.format("%-40s%s%n", threadName, el.toString()));
                    }
                    result.append("\n");
                }
            }
        } catch (Throwable e) {
            result.append(e);
        }

        return result.toString();
    }

    public static boolean isInternalIP(byte[] ip) {
        if (ip.length != 4) {
            throw new RuntimeException("illegal ipv4 bytes");
        }

        //10.0.0.0~10.255.255.255
        //172.16.0.0~172.31.255.255
        //192.168.0.0~192.168.255.255
        if (ip[0] == (byte) 10) {

            return true;
        } else if (ip[0] == (byte) 172) {
            if (ip[1] >= (byte) 16 && ip[1] <= (byte) 31) {
                return true;
            }
        } else if (ip[0] == (byte) 192) {
            if (ip[1] == (byte) 168) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInternalV6IP(InetAddress inetAddr) {
        if (inetAddr.isAnyLocalAddress() // Wild card ipv6
                || inetAddr.isLinkLocalAddress() // Single broadcast ipv6 address: fe80:xx:xx...
                || inetAddr.isLoopbackAddress() //Loopback ipv6 address
                || inetAddr.isSiteLocalAddress()) { // Site local ipv6 address: fec0:xx:xx...
            return true;
        }
        return false;
    }

    private static boolean ipCheck(byte[] ip) {
        if (ip.length != 4) {
            throw new RuntimeException("illegal ipv4 bytes");
        }

        if (ip[0] >= (byte) 1 && ip[0] <= (byte) 126) {
            if (ip[1] == (byte) 1 && ip[2] == (byte) 1 && ip[3] == (byte) 1) {
                return false;
            }
            if (ip[1] == (byte) 0 && ip[2] == (byte) 0 && ip[3] == (byte) 0) {
                return false;
            }
            return true;
        } else if (ip[0] >= (byte) 128 && ip[0] <= (byte) 191) {
            if (ip[2] == (byte) 1 && ip[3] == (byte) 1) {
                return false;
            }
            if (ip[2] == (byte) 0 && ip[3] == (byte) 0) {
                return false;
            }
            return true;
        } else if (ip[0] >= (byte) 192 && ip[0] <= (byte) 223) {
            if (ip[3] == (byte) 1) {
                return false;
            }
            if (ip[3] == (byte) 0) {
                return false;
            }
            return true;
        }
        return false;
    }


    public static String ipToIPv4Str(byte[] ip) {
        if (ip.length != 4) {
            return null;
        }
        return new StringBuilder().append(ip[0] & 0xFF).append(".").append(
                ip[1] & 0xFF).append(".").append(ip[2] & 0xFF)
                .append(".").append(ip[3] & 0xFF).toString();
    }

    public static String ipToIPv6Str(byte[] ip) {
        if (ip.length != 16) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ip.length; i++) {
            String hex = Integer.toHexString(ip[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
            if (i % 2 == 1 && i < ip.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static byte[] getIP() {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            byte[] internalIP = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        byte[] ipByte = ip.getAddress();
                        if (ipByte.length == 4) {
                            if (ipCheck(ipByte)) {
                                if (!isInternalIP(ipByte)) {
                                    return ipByte;
                                } else if (internalIP == null) {
                                    internalIP = ipByte;
                                }
                            }
                        }
                    } else if (ip != null && ip instanceof Inet6Address) {
                        byte[] ipByte = ip.getAddress();
                        if (ipByte.length == 16) {
                            if (ipV6Check(ipToIPv6Str(ipByte))) {
                                if (!isInternalV6IP(ip)) {
                                    return ipByte;
                                }
                            }
                        }
                    }
                }
            }
            if (internalIP != null) {
                return internalIP;
            } else {
                throw new RuntimeException("Can not get local ip");
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not get local ip", e);
        }
    }


    public static boolean ipV6Check(String inet6Address) {
        boolean containsCompressedZeroes = inet6Address.contains("::");
        if (containsCompressedZeroes && inet6Address.indexOf("::") != inet6Address.lastIndexOf("::")) {
            return false;
        } else if ((!inet6Address.startsWith(":") || inet6Address.startsWith("::")) && (!inet6Address.endsWith(":") || inet6Address.endsWith("::"))) {
            String[] octets = inet6Address.split(":");
            if (containsCompressedZeroes) {
                List<String> octetList = new ArrayList(Arrays.asList(octets));
                if (inet6Address.endsWith("::")) {
                    octetList.add("");
                } else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                    octetList.remove(0);
                }

                octets = (String[]) octetList.toArray(new String[octetList.size()]);
            }

            if (octets.length > 8) {
                return false;
            } else {
                int validOctets = 0;
                int emptyOctets = 0;

                for (int index = 0; index < octets.length; ++index) {
                    String octet = octets[index];
                    if (octet.length() == 0) {
                        ++emptyOctets;
                        if (emptyOctets > 1) {
                            return false;
                        }
                    } else {
                        emptyOctets = 0;
                        if (index == octets.length - 1 && octet.contains(".")) {
                            if (!isValidInet4Address(octet)) {
                                return false;
                            }

                            validOctets += 2;
                            continue;
                        }

                        if (octet.length() > 4) {
                            return false;
                        }

                        boolean var8 = false;

                        int octetInt;
                        try {
                            octetInt = Integer.parseInt(octet, 16);
                        } catch (NumberFormatException var10) {
                            return false;
                        }

                        if (octetInt < 0 || octetInt > 65535) {
                            return false;
                        }
                    }

                    ++validOctets;
                }

                if (validOctets <= 8 && (validOctets >= 8 || containsCompressedZeroes)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static boolean isValidInet4Address(String inet4Address) {
        String[] groups = ipv4Validator.match(inet4Address);
        if (groups == null) {
            return false;
        } else {
            String[] arr$ = groups;
            int len$ = groups.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String ipSegment = arr$[i$];
                if (ipSegment == null || ipSegment.length() == 0) {
                    return false;
                }

                boolean var7 = false;

                int iIpSegment;
                try {
                    iIpSegment = Integer.parseInt(ipSegment);
                } catch (NumberFormatException var9) {
                    return false;
                }

                if (iIpSegment > 255) {
                    return false;
                }

                if (ipSegment.length() > 1 && ipSegment.startsWith("0")) {
                    return false;
                }
            }

            return true;
        }
    }

    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                deleteFile(file1);
            }
            file.delete();
        }
    }

    public static String list2String(List<String> list, String splitor) {
        if (list == null || list.size() == 0) {
            return null;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            str.append(list.get(i));
            if (i == list.size() - 1) {
                continue;
            }
            str.append(splitor);
        }
        return str.toString();
    }

    public static List<String> string2List(String str, String splitor) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        String[] addrArray = str.split(splitor);
        return Arrays.asList(addrArray);
    }

    /**
     * 将两个列表属性合并到新列表
     *
     * @param source
     * @param bemerged
     */
    public static List<Attr> attrsMerge(List<Attr> source, List<Attr> bemerged) {
        if (CollectionUtils.isEmpty(bemerged)) {
            return source;
        }
        if (Objects.isNull(source) || CollectionUtils.isEmpty(source)) {
            source = bemerged;
            return source;
        }
        Map<AttrType, Integer> sourceMap = new HashMap<>();
        source.stream().forEach(attr -> addAttr(sourceMap, attr));
        bemerged.stream().forEach(attr -> addAttr(sourceMap, attr));
        return sourceMap.keySet().stream().map(attrType -> Attr.valueOf(attrType, sourceMap.get(attrType))).collect(Collectors.toList());
    }

    public static void addAttr(Map<AttrType, Integer> source, Attr newAttr) {
        if (source.containsKey(newAttr.getType())) {
            int attValue = source.get(newAttr.getType());
            int newValue = newAttr.getValue() + attValue;
            if (newValue != 0) {
                source.put(newAttr.getType(), newValue);
            } else {
                source.remove(newAttr.getType());
            }
        } else {
            source.put(newAttr.getType(), newAttr.getValue());
        }
    }

    public static List<Attr> multipleAttrValue(List<Attr> source, float mulitple) {
        if (mulitple == 1) {
            return source;
        }
        if (CollectionUtils.isEmpty(source)) {
            return source;
        }
        return source.stream().map(attr -> attr.multipleValue(mulitple)).collect(Collectors.toList());
    }

    /**
     * 魔法强度改变属性值
     *
     * @param effect
     * @param creature
     * @param source
     * @return
     */
    public static List<Attr> magicStreAlterEffectAttr(Effect effect, AbstractCreature creature, List<Attr> source) {
        if (!effect.getEffectResource().isMagicStrength() || !RelationshipUtils.isTrainer(creature)) {
            return source;
        }
        AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(creature);
        return multipleAttrValue(source, trainerCreature.getMagicStrength());
    }

    /**
     * 魔法强度改变效果值
     *
     * @param effect
     * @param creature
     * @param value
     * @return
     */
    public static int magicStrAlterEffectValue(Effect effect, AbstractCreature creature, int value) {
        if (!effect.getEffectResource().isMagicStrength() || !RelationshipUtils.isTrainer(creature)) {
            return value;
        }
        AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(creature);
        float magicStrength = trainerCreature.getMagicStrength();
        return (int) (value * magicStrength);
    }

//	/**
//	 * Returns distance between two 2D points
//	 *
//	 * @param point1
//	 *            first point
//	 * @param point2
//	 *            second point
//	 * @return distance between points
//	 */
//	public static double getDistance(Point point1, Point point2) {
//		return getDistance(point1.x, point1.y, point2.x, point2.y);
//	}
//
//	/**
//	 * Returns distance between two sets of coords
//	 *
//	 * @param x1
//	 *            first x coord
//	 * @param y1
//	 *            first y coord
//	 * @param x2
//	 *            second x coord
//	 * @param y2
//	 *            second y coord
//	 * @return distance between sets of coords
//	 */
//	public static double getDistance(int x1, int y1, float x2, float y2) {
//		// using long to avoid possible overflows when multiplying
//		float dx = x2 - x1;
//		float dy = y2 - y1;
//
//		// return Math.hypot(x2 - x1, y2 - y1); // Extremely slow
//		// return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // 20 times faster
//		// than hypot
//		return Math.sqrt(dx * dx + dy * dy); // 10 times faster then previous
//												// line
//	}
//
//	public static int getGridDistance(int x1, int y1, int x2, int y2) {
//		return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
//	}
//
//	public static int getStreetDistance(int x1, int y1, int x2, int y2) {
//		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
//	}
//
//	/**
//	 * Returns distance between 3D set of coords
//	 *
//	 * @param x1
//	 *            first x coord
//	 * @param y1
//	 *            first y coord
//	 * @param z1
//	 *            first z coord
//	 * @param x2
//	 *            second x coord
//	 * @param y2
//	 *            second y coord
//	 * @param z2
//	 *            second z coord
//	 * @return distance between coords
//	 */
//	public static double getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
//		float dx = x1 - x2;
//		float dy = y1 - y2;
//		float dz = z1 - z2;
//
//		// We should avoid Math.pow or Math.hypot due to perfomance reasons
//		return Math.sqrt(dx * dx + dy * dy + dz * dz);
//	}
//
//
//	/**
//	 * Returns closest point on segment to point
//	 *
//	 * @param ss
//	 *            segment start point
//	 * @param se
//	 *            segment end point
//	 * @param p
//	 *            point to found closest point on segment
//	 * @return closest point on segment to p
//	 */
//	public static Point getClosestPointOnSegment(Point ss, Point se, Point p) {
//		return getClosestPointOnSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
//	}
//
//	/**
//	 * Returns closest point on segment to point
//	 *
//	 * @param sx1
//	 *            segment x coord 1
//	 * @param sy1
//	 *            segment y coord 1
//	 * @param sx2
//	 *            segment x coord 2
//	 * @param sy2
//	 *            segment y coord 2
//	 * @param px
//	 *            point x coord
//	 * @param py
//	 *            point y coord
//	 * @return closets point on segment to point
//	 */
//	public static Point getClosestPointOnSegment(int sx1, int sy1, int sx2, int sy2, int px, int py) {
//		double xDelta = sx2 - sx1;
//		double yDelta = sy2 - sy1;
//
//		if ((xDelta == 0) && (yDelta == 0)) {
//			throw new IllegalArgumentException("Segment start equals segment end");
//		}
//
//		double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);
//
//		final Point closestPoint;
//		if (u < 0) {
//			closestPoint = new Point(sx1, sy1);
//		} else if (u > 1) {
//			closestPoint = new Point(sx2, sy2);
//		} else {
//			closestPoint = new Point((int) Math.round(sx1 + u * xDelta), (int) Math.round(sy1 + u * yDelta));
//		}
//
//		return closestPoint;
//	}
//
//
//
//	/**
//	 * Returns distance to segment
//	 *
//	 * @param ss
//	 *            segment start point
//	 * @param se
//	 *            segment end point
//	 * @param p
//	 *            point to found closest point on segment
//	 * @return distance to segment
//	 */
//	public static double getDistanceToSegment(Point ss, Point se, Point p) {
//		return getDistanceToSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
//	}
//
//	/**
//	 * Returns distance to segment
//	 *
//	 * @param sx1
//	 *            segment x coord 1
//	 * @param sy1
//	 *            segment y coord 1
//	 * @param sx2
//	 *            segment x coord 2
//	 * @param sy2
//	 *            segment y coord 2
//	 * @param px
//	 *            point x coord
//	 * @param py
//	 *            point y coord
//	 * @return distance to segment
//	 */
//	public static double getDistanceToSegment(int sx1, int sy1, int sx2, int sy2, int px, int py) {
//		Point closestPoint = getClosestPointOnSegment(sx1, sy1, sx2, sy2, px, py);
//		return getDistance(closestPoint.x, closestPoint.y, px, py);
//	}
//
//
//
//
//
//	public static boolean isInRange(int fx, int fy, int tx, int ty, int halfRowRange, int halfColRange) {
//		return (Math.abs((fx - tx)) <= halfRowRange && Math.abs((fy - ty)) <= halfColRange);
//	}
//
//	/**
//	 *
//	 * @param obj1X
//	 * @param obj1Y
//	 * @param obj2X
//	 * @param obj2Y
//	 * @return float
//	 */
//	public final static float calculateAngleFrom(float obj1X, float obj1Y, float obj2X, float obj2Y) {
//		float angleTarget = (float) Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
//		if (angleTarget < 0) {
//			angleTarget = 360 + angleTarget;
//		}
//		return angleTarget;
//	}
//	/**
//	 *
//	 * @param clientHeading
//	 * @return float
//	 */
//	public final static float convertHeadingToDegree(byte clientHeading) {
//		float degree = clientHeading * 3;
//		return degree;
//	}
//
//	public static Road quickFindRoad(int mapId, int fx, int fy, int tx, int ty) {
//		return quickFindRoad(mapId, fx, fy, tx, ty, 0);
//	}
//
//	public static Road quickFindRoad(int mapId, int fx, int fy, int tx, int ty, int distance) {
//		WorldMap worldMap = World.getInstance().getWorldMap(mapId);
//
//		if (checkPointOut(worldMap, tx, ty)) {
//			return null;
//		}
//
//		if (fx == tx && fy == ty) {
//			return null;
//		}
//
//		int dx = tx - fx;
//		int dy = ty - fy;
//		int ux = dx > 0 ? 1 : -1;
//		int uy = dy > 0 ? 1 : -1;
//		int x = fx;
//		int y = fy;
//		int eps = 0;
//		dx = Math.abs(dx);
//		dy = Math.abs(dy);
//
//		int lx = -1;
//		int ly = -1;
//		int step = 0;
//
//		byte[] result = new byte[dx + dy];
//
//		if (dx > dy) {
//			for (x = fx; x != tx; x += ux) {
//				if (lx != -1 && ly != -1) {
//					if (checkPointOut(worldMap, x, y)) {
//						return null;
//					}
//					result[step] = (byte) DirType.directions[x - lx + 1][y - ly + 1].ordinal();
//					step++;
//				}
//				lx = x;
//				ly = y;
//
//				eps += dy;
//				if ((eps << 1) >= dx) {
//					y += uy;
//					eps -= dx;
//				}
//			}
//		} else {
//			for (y = fy; y != ty; y += uy) {
//				if (lx != -1 && ly != -1) {
//					if (checkPointOut(worldMap, x, y)) {
//						return null;
//					}
//					result[step] = (byte) DirType.directions[x - lx + 1][y - ly + 1].ordinal();
//					step++;
//				}
//				lx = x;
//				ly = y;
//				eps += dx;
//				if ((eps << 1) >= dy) {
//					x += ux;
//					eps -= dy;
//				}
//			}
//		}
//
//		result[step] = (byte) DirType.directions[x - lx + 1][y - ly + 1].ordinal();
//
//		int length = step + 1;
//		distance = length > distance ? distance : length;
//
//		byte[] roadStep = new byte[length - distance];
//		System.arraycopy(result, 0, roadStep, 0, roadStep.length);
//
//		return Road.valueOf(roadStep);
//	}
//
//	public static Road findRoad(int mapId, int fx, int fy, int tx, int ty) {
//		return findRoadByStepAndDistance(mapId, fx, fy, tx, ty, 500, 0);
//	}
//
//	public static Road findRoadByStep(int mapId, int fx, int fy, int tx, int ty, int maxStep) {
//		return findRoadByStepAndDistance(mapId, fx, fy, tx, ty, maxStep, 0);
//	}
//
//	public static Road findRoadByDistance(int mapId, int fx, int fy, int tx, int ty, int distance) {
//		return findRoadByStepAndDistance(mapId, fx, fy, tx, ty, 500, distance);
//	}
//
////	public static Road findRoadByStepAndDistance(int mapId, int fx, int fy, int tx, int ty, int maxStep, int distance) {
////		WorldMap worldMap = World.getInstance().getWorldMap(mapId);
////
////		if (checkPointOut(worldMap, tx, ty)) {
////			return null;
////		}
////
////		Position start = new Position();
////		start.setX(fx);
////		start.setY(fy);
////
////		Position end = new Position();
////		end.setX(tx);
////		end.setY(ty);
////
////		// TODO 这个代码需要重新构建一下
////		if (start.equals(end)) {
////			return null;
////		}
////
////		Grid startGrid = new Grid(start.getX(), start.getY());
////		Grid endGrid = new Grid(end.getX(), end.getY());
////
////		RoadGrid startRoadGrid = new RoadGrid();
////		startRoadGrid.setGrid(startGrid);
////		RoadGrid endRoadGrid = new RoadGrid();
////		endRoadGrid.setGrid(endGrid);
////
////		Set<RoadGrid> waitting = new TreeSet<RoadGrid>();
////
////		HashMap<Integer, RoadGrid> counted = new HashMap<Integer, RoadGrid>();
////
////		HashSet<Integer> passed = new HashSet<Integer>();
////
////		waitting.add(startRoadGrid);
////		passed.add(startGrid.getKey());
////
////		int step = 0;
////
////		while (waitting.size() > 0 && (step < maxStep)) {
////			RoadGrid grid = waitting.iterator().next();
////			waitting.remove(grid);
////
////			if (grid.equals(endRoadGrid)) {
////				endRoadGrid = grid;
////				break;
////			}
////
////			step++;
////
////			counted.put(grid.getGrid().getKey(), grid);
////
////			List<Grid> rounds = getRoundGrid(worldMap, grid.getGrid());
////
////			for (Grid round : rounds) {
////				if (passed.contains(round.getKey())) {
////					continue;
////				}
////
////				RoadGrid roundGrid = new RoadGrid();
////				roundGrid.setGrid(round);
////				roundGrid.setFather(grid.getGrid().getKey());
////				passed.add(round.getKey());
////				roundGrid.setWeight(getStreetDistance(round.getX(), round.getY(), endGrid.getX(), endGrid.getY()));
////
////				waitting.add(roundGrid);
////			}
////
////		}
////
////		if (endRoadGrid.getFather() != -1) {
////			byte[] dirs = new byte[step];
////			step--;
////			RoadGrid grid = endRoadGrid;
////			dirs[step] = grid.getGrid().getDir();
////			while (grid.getFather() != -1) {
////				if (grid.getFather() == startGrid.getKey()) {
////					break;
////				}
////				step--;
////				grid = counted.get(grid.getFather());
////				dirs[step] = grid.getGrid().getDir();
////			}
////
////			byte[] newDirs = new byte[dirs.length - step];
////			System.arraycopy(dirs, step, newDirs, 0, newDirs.length);
////			dirs = newDirs;
////
////			int length = dirs.length;
////			distance = ((length > distance) ? distance : length);
////
////			if (distance > 0) {
////				byte[] result = new byte[length - distance];
////				System.arraycopy(dirs, 0, result, 0, result.length);
////				dirs = result;
////			}
////
////			return Road.valueOf(dirs);
////		}
////
////		return null;
////	}
//
////	private static List<Grid> getRoundGrid(WorldMap map, Grid grid) {
////		List<Grid> grids = new ArrayList<Grid>(9);
////		boolean out = false;
////		if (checkPointOut(map, grid.getX(), grid.getY())) {
////			out = true;
////		}
////
////		for (DirType dir : DirType.values()) {
////			int x = grid.getX() + dir.getAddX();
////			int y = grid.getY() + dir.getAddY();
////			if (checkPointOut(map, x, y) && !out) {
////				continue;
////			}
////			Grid temp = new Grid(x, y);
////			temp.setDir((byte) dir.ordinal());
////			grids.add(temp);
////		}
////		grids.add(grid);
////		return grids;
////	}
//
//
//
//	public static byte[] unzipRoads(byte[] data) {
//		byte[] result = null;
//		if (!ArrayUtils.isEmpty(data)) {
//			ByteBuffer buffer = tb.get();
//			buffer.clear();
//			for (byte r : data) {
//				int step = (r >>> 3) & 31;
//				byte d = (byte) (r & 7);
//				for (int i = 0; i < step; i++) {
//					buffer.put(d);
//				}
//			}
//			buffer.flip();
//			byte[] temp = buffer.array();
//			result = new byte[buffer.limit()];
//			System.arraycopy(temp, 0, result, 0, result.length);
//		}
//		return result;
//	}
//
//	public static byte[] zipRoads(byte[] data) {
//		byte[] result = null;
//		if (!ArrayUtils.isEmpty(data)) {
//			ByteBuffer buffer = tb.get();
//			buffer.clear();
//			byte dir = -1;
//			int step = 0;
//			for (byte temp : data) {
//				if (dir == -1) {
//					dir = temp;
//					step++;
//				} else {
//					if (dir == temp) {
//						if (step == 31) {
//							byte add = (byte) ((step << 3) | dir);
//							buffer.put(add);
//							step = 1;
//						} else {
//							step++;
//						}
//					} else {
//						byte add = (byte) ((step << 3) | dir);
//						buffer.put(add);
//						dir = temp;
//						step = 1;
//					}
//				}
//			}
//			byte add = (byte) ((step << 3) | dir);
//			buffer.put(add);
//
//			buffer.flip();
//			byte[] temp = buffer.array();
//			result = new byte[buffer.limit()];
//			System.arraycopy(temp, 0, result, 0, result.length);
//		}
//		return result;
//	}
//
////	public static Grid faceDirection(int x, int y, DirType direction, int distance) {
////		Grid grid = new Grid(x, y);
////		for (int i = 0; i < distance; i++) {
////			grid.setX(grid.getX() + direction.getAddX());
////			grid.setY(grid.getY() + direction.getAddY());
////		}
////		return grid;
////	}
//
//	public static class AddGrid {
//		private int addX;
//		private int addY;
//
//		public AddGrid() {
//
//		}
//
//		public AddGrid(int addX, int addY) {
//			this.addX = addX;
//			this.addY = addY;
//		}
//
//		public int getAddX() {
//			return addX;
//		}
//
//		public void setAddX(int addX) {
//			this.addX = addX;
//		}
//
//		public int getAddY() {
//			return addY;
//		}
//
//		public void setAddY(int addY) {
//			this.addY = addY;
//		}
//
////		public AddGrid addDir(DirType DirType) {
////			this.addX += DirType.getAddX();
////			this.addY += DirType.getAddY();
////			return this;
////		}
//
//		@Override
//		public AddGrid clone() throws CloneNotSupportedException {
//			AddGrid ag = new AddGrid();
//			ag.addX = this.addX;
//			ag.addY = this.addY;
//			return ag;
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + addX;
//			result = prime * result + addY;
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			AddGrid other = (AddGrid) obj;
//			if (addX != other.addX)
//				return false;
//			if (addY != other.addY)
//				return false;
//			return true;
//		}
//
//	}
//
//	public static DirType face(int fx, int fy, int tx, int ty) {
//		if (fx == tx && fy == ty) {
//			return null;
//		}
//
//		int dx = tx - fx;
//		int dy = ty - fy;
//
//		if (dx == 0) {
//			if (dy < 0) {
//				return DirType.U;
//			} else {
//				return DirType.D;
//			}
//		} else if (dy == 0) {
//			if (dx < 0) {
//				return DirType.L;
//			} else {
//				return DirType.R;
//			}
//		} else {
//			int adx = Math.abs(dx);
//			int ady = Math.abs(dy);
//			if (adx < ady) {
//				if (dy < 0) {
//					return DirType.U;
//				} else {
//					return DirType.D;
//				}
//			} else if (adx > ady) {
//				if (dx < 0) {
//					return DirType.L;
//				} else {
//					return DirType.R;
//				}
//			} else {
//				int sx = dx / adx;
//				int sy = dy / ady;
//				return DirType.directions[sx + 1][sy + 1];
//			}
//		}
//	}
//
//	public static int[] findRandomPoint(int fx, int fy, int tx, int ty, int step) {
//		DirType dir = face(tx, ty, fx, fy);
//		DirType[] dirs = new DirType[3];
//		dirs[0] = DirType.values()[(dir.ordinal() + 7) % 8];
//		dirs[1] = DirType.values()[dir.ordinal()];
//		dirs[2] = DirType.values()[(dir.ordinal() + 1) % 8];
//
//		Random random = GameMathUtil.getRandom();
//
//		for (int i = 0; i < step; i++) {
//			DirType temp = dirs[random.nextInt(3)];
//			tx += temp.getAddX();
//			ty += temp.getAddY();
//		}
//
//		return new int[] { tx, ty };
//	}
//
//	public static Road smoothFindRoadByStep(int mapId, int fx, int fy, int tx, int ty, int maxStep) {
//		WorldMap worldMap = World.getInstance().getWorldMap(mapId);
//
//		if (checkPointOut(worldMap, tx, ty)) {
//			return null;
//		}
//
//		if (fx == tx && fy == ty) {
//			return null;
//		}
//
//		int dx = Math.abs(tx - fx);
//		int dy = Math.abs(ty - fy);
//
//		int length = dx + dy;
//		byte[] roads = new byte[length];
//		int step = 0;
//		for (int i = 0; i < length; i++) {
//			DirType dir = face(fx, fy, tx, ty);
//			fx += dir.getAddX();
//			fy += dir.getAddY();
//			if (checkPointOut(worldMap, fx, fy)) {
//				return null;
//			}
//			roads[step++] = (byte) dir.ordinal();
//			if ((fx == tx && fy == ty) || (step == maxStep)) {
//				break;
//			}
//		}
//		byte[] realRoads = new byte[step];
//		System.arraycopy(roads, 0, realRoads, 0, step);
//		return Road.valueOf(realRoads);
//	}
//
//	public static Road smoothFindRoad(int mapId, int fx, int fy, int tx, int ty) {
//		WorldMap worldMap = MapResourceManager.getInstance().getWorldMap(mapId);
//
//		if (checkPointOut(worldMap, tx, ty)) {
//			return null;
//		}
//
//		if (fx == tx && fy == ty) {
//			return null;
//		}
//
//		int dx = Math.abs(tx - fx);
//		int dy = Math.abs(ty - fy);
//		if (dx == dy || dx == 0 || dy == 0) {
//			int length = Math.max(dx, dy);
//			byte[] roads = new byte[length];
//			DirType dir = face(fx, fy, tx, ty);
//			for (int i = 0; i < length; i++) {
//				fx += dir.getAddX();
//				fy += dir.getAddY();
//				if (checkPointOut(worldMap, fx, fy)) {
//					return null;
//				}
//				roads[i] = (byte) dir.ordinal();
//			}
//			return Road.valueOf(roads);
//		} else {
//			int length = dx + dy;
//			byte[] roads = new byte[length];
//			DirType dir = face(fx, fy, tx, ty);
//			int step = 0;
//			for (int i = 0; i < length; i++) {
//				fx += dir.getAddX();
//				fy += dir.getAddY();
//				if (checkPointOut(worldMap, fx, fy)) {
//					return null;
//				}
//				roads[step++] = (byte) dir.ordinal();
//				dx = Math.abs(tx - fx);
//				dy = Math.abs(ty - fy);
//				if (dx == dy || dx == 0 || dy == 0) {
//					break;
//				}
//			}
//			length = Math.max(dx, dy);
//			dir = face(fx, fy, tx, ty);
//			for (int i = 0; i < length; i++) {
//				fx += dir.getAddX();
//				fy += dir.getAddY();
//				if (checkPointOut(worldMap, fx, fy)) {
//					return null;
//				}
//				roads[step++] = (byte) dir.ordinal();
//			}
//			byte[] realRoads = new byte[step];
//			System.arraycopy(roads, 0, realRoads, 0, step);
//			return Road.valueOf(realRoads);
//		}
//	}
//
//	private static boolean checkPointOut(WorldMap worldMap, int x, int y) {
//		// return false;
//		return worldMap.isOut(x, y) || worldMap.isBlock(x, y);
//	}
//
//	public static void main(String[] args) {
//		// int fx = 44;
//		// int fy = 29;
//		// Road road = SmoothFindRoadByStep(0, fx, fy, 36, 29, 13);
//		// for (byte ddd : road.getRoads()) {
//		// DirType dir = DirType.values()[ddd];
//		// fx += dir.getAddX();
//		// fy += dir.getAddY();
//		// SystemOut.println("fx : " + fx + " fy : " + fy + " dir : " + dir);
//		// }
//		// / * 杀怪经验=MAX((1-1/30*min(max(人物等级-怪物等级 ,10)-10,60))*怪物经验,1)
//		// Scanner scanner = new Scanner(System.in);
//		// while (scanner.hasNext()) {
//		// int dif = scanner.nextInt();
//		// double factorA = (1 - 1 / 30.0 * Math.min(Math.max(dif, 10) - 10,
//		// 60));
//		// double monster = 10000.0;
//		// SystemOut.println(Math.ceil(Math.max(factorA * monster, 1)));
//		// }
//
//		int length = 1000;
//		Random random = new Random(System.currentTimeMillis());
//		byte[] road = new byte[length];
//		for (int i = 0; i < length; i++) {
//			road[i] = (byte) random.nextInt(8);
//		}
//
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < 100000; i++) {
//			byte[] temp1 = zipRoads(road);
//			unzipRoads(temp1);
//		}
//		SystemOut.println("use : " + (System.currentTimeMillis() - start) / 100000.0);
//		SystemOut.println((float) getDistance(0, 0, 25, 25));
//		SystemOut.println((int) getDistance(0, 0, 25, 25));
//
//	}
}
