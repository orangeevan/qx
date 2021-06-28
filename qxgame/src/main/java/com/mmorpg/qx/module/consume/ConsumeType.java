package com.mmorpg.qx.module.consume;

import com.mmorpg.qx.module.consume.impl.PurseConsume;
import com.mmorpg.qx.module.consume.impl.HpConsume;
import com.mmorpg.qx.module.consume.impl.ItemConsume;
import com.mmorpg.qx.module.consume.resource.ConsumeResource;

public enum ConsumeType {

	Item {
		@Override
		public ItemConsume create() {
			return new ItemConsume();
		}

		@Override
		public ItemConsume create(ConsumeResource resource) {
			ItemConsume consume = create();
			consume.init(resource);
			return consume;
		}
	},

	HP {
		@Override
		public HpConsume create() {
			return new HpConsume();
		}

		@Override
		public HpConsume create(ConsumeResource resource) {
			HpConsume consume = create();
			consume.init(resource);
			return consume;
		}
	},

	PURSE {
		@Override
		public PurseConsume create() {
			return new PurseConsume();
		}

		@Override
		public PurseConsume create(ConsumeResource resource) {
			PurseConsume consume = create();
			consume.init(resource);
			return consume;
		}
	};

	public abstract <T extends AbstractConsume> T create();

	public abstract <T extends AbstractConsume> T create(ConsumeResource resource);

}
