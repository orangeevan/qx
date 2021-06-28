package clientRobot;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.haipaite.common.utility.CryptUtils;
import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.client.Wclient;
import com.mmorpg.qx.common.socket.core.WrequestPacket;
import com.mmorpg.qx.module.account.packet.LoginAuthReq;
import com.mmorpg.qx.module.mwn.packet.CreateMwnReq;
import com.mmorpg.qx.module.player.packet.CreatePlayerReq;
import com.mmorpg.qx.module.roundFight.enums.RoomType;
import com.mmorpg.qx.module.roundFight.packet.PlayerCreateRoomReq;
import com.mmorpg.qx.module.roundFight.packet.PlayerRoundFightStartReq;
import com.mmorpg.qx.module.trainer.packet.CreateTrainerReq;
import com.mmorpg.qx.module.worldMap.packet.CreateWorldMapReq;
import io.netty.channel.ChannelFuture;

/**
 * 客户端机器人，连接服务端完成定制消息接收和下发测试
 *
 * @author wangke
 * @since v1.0 2019/3/26
 */
public class ClientRobot {
    private static final byte[] nodata = new byte[0];

    public void run() {
        ChannelFuture connect = null;
        try {
            Wclient client = new Wclient();
            client.addChannelHandler(new SimpleClientHandler());

            connect = client.connect(9999, "192.168.19.31");
            if (connect.isSuccess()) {
                System.err.println("客户端连接成功");
            }
            Thread.currentThread().sleep(1000);


            //向服务端发账号起登录请求
            loginReq(connect);
            System.err.println("登录请求");
            Thread.currentThread().sleep(5000);

            //向服务器发起创角请求
            createReq(connect);
            System.err.println("创建请求");
            Thread.currentThread().sleep(1000);

            //向服务端发起第一次心跳
            clientHeartbeat(connect, 1);

            //创建驯养师
            createPlayerTrainerReq(connect);
            Thread.currentThread().sleep(1000);
            //创建魔物娘
            createMwnReq(connect);
            Thread.currentThread().sleep(1000);
            //创建房间
            createRoomReq(connect);
            Thread.currentThread().sleep(1000);
            //开启战斗
            //startRoundFight(connect);
            //向服务器发起创建地图请求
            //createMapReq(connect);

            //向服务器发起踢人请求
            //sendPacket(connect, PacketId.KICK_REASON_RESP, new byte[0]);

            //向服务端发起停服请求
            //sendPacket(connect, PacketId.SERVER_STOP, new byte[0]);

            //无限循环心跳
            //clientHeartbeat(connect, Integer.MAX_VALUE);

            Thread.currentThread().sleep(10000);
            connect.channel().close().sync();
            Runtime.getRuntime().exit(1);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (connect != null) {
                connect.channel().close();
            }
        }
    }

    public static void main(String[] args) {
        int clientNum = 1;
        Thread[] clients = new Thread[clientNum];
        for (int i = 0; i < clientNum; i++) {
            clients[i] = new Thread(() -> new ClientRobot().run());
            clients[i].start();
            try {
                clients[i].sleep(100);
            }catch (Exception e){

            }

        }

    }

    public static void clientHeartbeat(ChannelFuture connect, long times) throws InterruptedException {
        long initTimes = times;
        while (connect.channel().isActive() && times > 0) {
            sendPacket(connect, PacketId.PLAYER_HEARTBEAT_REQ, nodata);
            System.err.println(String.format("客户端发送心跳包，第[%s]次心跳", initTimes - times--));
            //Thread.currentThread().sleep(ServerConfigConstant.PLAYER_HEAR_BEAT_PERIOD * 1L);
        }
    }

    public static void loginReq(ChannelFuture connect) {
        try {
            LoginAuthReq loginAuthReq = new LoginAuthReq();
            loginAuthReq.setAccount("ac1"+ RandomUtils.nextInt());
            loginAuthReq.setServerId(1);
            //loginAuthReq.setAccount("13042092280");
            // loginAuthReq.setPassword("11111");
            String token = "3k.com";
            StringBuilder str = new StringBuilder();
            str.append(loginAuthReq.getAccount());
            str.append(loginAuthReq.getServerId());
            str.append(token);
            System.err.println("客户端准备md5字符串: " + str.toString());
            String expectMD5 = CryptUtils.md5(str.toString());
            System.err.println("客户端发送token： " + expectMD5);
            loginAuthReq.setMd5check(expectMD5);
            Codec<LoginAuthReq> codec = ProtobufProxy.create(LoginAuthReq.class, false, null);
            byte[] encode = codec.encode(loginAuthReq);
            sendPacket(connect, PacketId.LOGIN_AUTH_REQ, encode);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void createReq(ChannelFuture connect) {
        try {
            CreatePlayerReq createAuthReq = new CreatePlayerReq();
            createAuthReq.setName("1344209224011" + RandomUtils.nextInt());
            createAuthReq.setRole(1);
            createAuthReq.setSex(1);
            Codec<CreatePlayerReq> codec = ProtobufProxy.create(CreatePlayerReq.class, false, null);
            byte[] encode = codec.encode(createAuthReq);
            sendPacket(connect, PacketId.CREATE_PLAYER_REQ, encode);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void createMapReq(ChannelFuture connect) {
        try {
            CreateWorldMapReq createWorldMapReq = new CreateWorldMapReq();
            createWorldMapReq.setMapId(1002);
            Codec<CreateWorldMapReq> codec = ProtobufProxy.create(CreateWorldMapReq.class, false, null);
            byte[] encode = codec.encode(createWorldMapReq);
            sendPacket(connect, PacketId.CREATE_MAP_REQ, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建驯养师
     *
     * @param connect
     */
    public static void createPlayerTrainerReq(ChannelFuture connect) {
        try {
            CreateTrainerReq createTrainerReq = new CreateTrainerReq();
            createTrainerReq.setTrainerId(1);
            Codec<CreateTrainerReq> codec = ProtobufProxy.create(CreateTrainerReq.class, false, null);
            byte[] encode = codec.encode(createTrainerReq);
            sendPacket(connect, PacketId.CREATE_TRAINER_REQ, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 创建魔物娘
     * @param connect
     */
    public static void createMwnReq(ChannelFuture connect) {
        try {
            CreateMwnReq createMwnReq = new CreateMwnReq();
            createMwnReq.setMwnId(1);
            Codec<CreateMwnReq> codec = ProtobufProxy.create(CreateMwnReq.class, false, null);
            byte[] encode = codec.encode(createMwnReq);
            sendPacket(connect, PacketId.CREATE_MWN_REQ, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 创建房间
     * @param connect
     */
    public static void createRoomReq(ChannelFuture connect) {
        try {
            PlayerCreateRoomReq playerCreateRoomReq = new PlayerCreateRoomReq();
            playerCreateRoomReq.setName("一号房");
            playerCreateRoomReq.setRoomType(RoomType.PVE);
            playerCreateRoomReq.setTrainerId(1);
            playerCreateRoomReq.setMapId(1002);
            //playerCreateRoomReq.setCardBagIndex(0);
            Codec<PlayerCreateRoomReq> codec = ProtobufProxy.create(PlayerCreateRoomReq.class, false, null);
            byte[] encode = codec.encode(playerCreateRoomReq);
            sendPacket(connect, PacketId.PLAYER_ROOM_CREATE_REQ, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 开启战斗
     * @param connect
     */
    public static void startRoundFight(ChannelFuture connect) {
        try {
            PlayerRoundFightStartReq roundFightStartReq = new PlayerRoundFightStartReq();
            Codec<PlayerRoundFightStartReq> codec = ProtobufProxy.create(PlayerRoundFightStartReq.class, false, null);
            byte[] encode = codec.encode(roundFightStartReq);
            sendPacket(connect, PacketId.PLAYER_ROUND_FIGHT_START_REQ, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPacket(ChannelFuture connect, short packetId, byte[] data) {
        try {
            WrequestPacket packet = WrequestPacket.valueOf(packetId, data);
            connect.channel().writeAndFlush(packet);
            System.err.println("客户端发送消息包: " + packetId);
            Thread.currentThread().sleep(2000);
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
