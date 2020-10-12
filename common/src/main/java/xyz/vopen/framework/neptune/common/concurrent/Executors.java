package xyz.vopen.framework.neptune.common.concurrent;

import scala.concurrent.ExecutionContext;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Collection of {@link Executor}, {@link ExecutorService} and {@link ExecutionContext} implementations.
 */
public class Executors {

	/**
	 * Return a direct executor. The direct executor directly executes the runnable in the calling
	 * thread.
	 *
	 * @return Direct executor
	 */
	public static Executor directExecutor() {
		return DirectExecutorService.INSTANCE;
	}

	/**
	 * Return a new direct executor service.
	 *
	 * <p>The direct executor service directly executes the runnables and the callables in the calling
	 * thread.
	 *
	 * @return New direct executor service
	 */
	public static ExecutorService newDirectExecutorService() {
		return new DirectExecutorService();
	}

	/**
	 * Return a direct execution context. The direct execution context executes the runnable directly
	 * in the calling thread.
	 *
	 * @return Direct execution context.
	 */
	public static ExecutionContext directExecutionContext() {
		return DirectExecutionContext.INSTANCE;
	}

	/**
	 * Direct execution context.
	 */
	private static class DirectExecutionContext implements ExecutionContext {

		static final DirectExecutionContext INSTANCE = new DirectExecutionContext();

		private DirectExecutionContext() {}

		@Override
		public void execute(Runnable runnable) {
			runnable.run();
		}

		@Override
		public void reportFailure(Throwable cause) {
			throw new IllegalStateException("Error in direct execution context.", cause);
		}

		@Override
		public ExecutionContext prepare() {
			return this;
		}
	}
}
