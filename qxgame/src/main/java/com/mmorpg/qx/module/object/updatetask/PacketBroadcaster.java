package com.mmorpg.qx.module.object.updatetask;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public final class PacketBroadcaster extends AbstractFIFOPeriodicTaskManager<AbstractCreature> {

	private static final Logger logger = SysLoggerFactory.getLogger(BroadcastMode.class);
	private static PacketBroadcaster self;

	public static PacketBroadcaster getInstance() {
		return self;
	}

	private PacketBroadcaster() {
		super(500);
	}

	@PostConstruct
	public void init() {
		super.init();
		self = this;
	}

	public static enum BroadcastMode {

		/**
		 * 通知货币变化
		 **/
		UPDATE_PLAYER_PURSE {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// TODO
				// Player player = (Player) creature;
				// PacketSendUtility.sendPacket(player, SM_Currency.valueOf(player));
			}
		},
		/**
		 * 通知伤害结果
		 **/
		BORADCAST_DAMAGE_STAT {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// TODO
				// PacketSendUtility.broadcastPacket(creature,
				// SM_ATTACK_STATUS.valueOf(creature));
			}
		},
		/**
		 * 更新hp状态
		 **/
		UPDATE_PLAYER_HP_STAT {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// TODO
				// ((Player) creature).getLifeStats().sendHpPacketUpdateImpl();
			}
		},
		/**
		 * 更新mp状态
		 **/
		UPDATE_PLAYER_MP_STAT {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// ((Player) creature).getLifeStats().sendMpPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_DP_STAT {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// ((Player) creature).getLifeStats().sendDpPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_BARRIER_STAT {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// creature.getLifeStats().sendBarrierPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_RP_STAT {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// ((Player) creature).getRp().sendRpPacketUpdateImpl();
			}
		},
		BROAD_CAST_EFFECTS {
			@Override
			public void sendPacket(AbstractCreature creature) {
				// creature.getEffectController().broadCastEffectsImp();
			}
		};

		private final int MASK;

		private BroadcastMode() {
			MASK = (int) (1 << ordinal());
		}

		public int mask() {
			return MASK;
		}

		protected abstract void sendPacket(AbstractCreature creature);

		protected final void trySendPacket(final AbstractCreature creature, int mask) {
			if ((mask & mask()) == mask()) {
				try {
					sendPacket(creature);
				} catch (Exception e) {
					logger.error("推送队列报错", e);
				}
				//creature.removePacketBroadcastMask(this);
			}
		}
	}

	private static final BroadcastMode[] VALUES = BroadcastMode.values();

	@Override
	protected void callTask(AbstractCreature creature) {
		for (int mask; (mask = creature.getPacketBroadcastMask()) != 0; ) {
			for (BroadcastMode mode : VALUES) {
				mode.trySendPacket(creature, mask);
			}
		}
	}

	@Override
	protected String getCalledMethodName() {
		return "packetBroadcast()";
	}
}
