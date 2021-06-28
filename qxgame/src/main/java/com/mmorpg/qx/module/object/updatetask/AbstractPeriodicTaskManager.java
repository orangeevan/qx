package com.mmorpg.qx.module.object.updatetask;

import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPeriodicTaskManager extends AbstractLockManager implements Runnable {
	protected static final Logger log = SysLoggerFactory.getLogger(AbstractPeriodicTaskManager.class);

	private final int period;

	public AbstractPeriodicTaskManager(int period) {
		this.period = period;
	}

	@PostConstruct
	public void init() {
		int dispatcherHashCode = Math.abs(this.hashCode());
		IdentityEventExecutorGroup.addScheduleAtFixedRate(new AbstractDispatcherHashCodeRunable() {

			@Override
			public long timeoutNanoTime() {
				return TimeUnit.MILLISECONDS.toNanos(1);
			}

			@Override
			public String name() {
				return "AbstractPeriodicTaskManager";
			}

			@Override
			public int getDispatcherHashCode() {
				return dispatcherHashCode;
			}

			@Override
			public void doRun() {
				AbstractPeriodicTaskManager.this.run();
			}
		}, 1000, period, TimeUnit.MILLISECONDS);
	}

	@Override
	public abstract void run();
}
